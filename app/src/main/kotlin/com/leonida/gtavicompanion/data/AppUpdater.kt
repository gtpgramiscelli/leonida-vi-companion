package com.leonida.gtavicompanion.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

data class ReleaseInfo(
    val tagName: String,
    val title: String,
    val notes: String,
    val apkDownloadUrl: String,
    val apkFileName: String,
    val apkSizeBytes: Long
)

sealed interface UpdateCheckResult {
    data class UpdateAvailable(val release: ReleaseInfo) : UpdateCheckResult
    data class UpToDate(val latestTag: String) : UpdateCheckResult
    data class Error(val message: String) : UpdateCheckResult
}

object VersionComparator {

    private val repoPattern = Regex("^[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+$")

    /**
     * Accepts either "owner/repo" or a full GitHub URL like "https://github.com/owner/repo"
     * and normalizes it to "owner/repo", or returns null if invalid.
     */
    fun normalizeRepo(raw: String): String? {
        val cleaned = raw.trim()
            .removePrefix("https://github.com/")
            .removePrefix("http://github.com/")
            .removePrefix("github.com/")
            .removeSuffix(".git")
            .trim('/')
        return cleaned.takeIf { repoPattern.matches(it) }
    }

    /**
     * Parses versions like "v1.2.3", "1.2.3", "1.2", "v2.0.0-beta1" into integer parts.
     */
    fun parseVersionParts(version: String): List<Int> {
        val core = version.trim()
            .removePrefix("v")
            .removePrefix("V")
            .substringBefore('-')
            .substringBefore('+')
        if (core.isBlank()) return listOf(0)
        return core.split('.').map { segment ->
            segment.takeWhile { it.isDigit() }.toIntOrNull() ?: 0
        }
    }

    fun isNewerVersion(remoteVersion: String, currentVersion: String): Boolean {
        val remote = parseVersionParts(remoteVersion)
        val current = parseVersionParts(currentVersion)
        val maxLen = maxOf(remote.size, current.size)
        for (i in 0 until maxLen) {
            val r = remote.getOrElse(i) { 0 }
            val c = current.getOrElse(i) { 0 }
            if (r != c) return r > c
        }
        return false
    }
}

class GitHubAppUpdater(private val context: Context) {

    suspend fun checkForUpdate(
        repoSlug: String,
        currentVersion: String
    ): UpdateCheckResult = withContext(Dispatchers.IO) {
        val normalized = VersionComparator.normalizeRepo(repoSlug)
            ?: return@withContext UpdateCheckResult.Error(
                "Repositório inválido. Use o formato usuario/repositorio."
            )

        var conn: HttpURLConnection? = null
        try {
            val endpoint = URL("https://api.github.com/repos/$normalized/releases/latest")
            conn = (endpoint.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10_000
                readTimeout = 12_000
                setRequestProperty("Accept", "application/vnd.github+json")
                setRequestProperty("User-Agent", "LeonidaCompanion-Android/$currentVersion")
            }

            val code = conn.responseCode
            if (code == HttpURLConnection.HTTP_NOT_FOUND) {
                return@withContext UpdateCheckResult.Error(
                    "Nenhuma Release encontrada em $normalized. Verifique se o repositório é público e possui uma Release publicada."
                )
            }
            if (code == HttpURLConnection.HTTP_FORBIDDEN) {
                return@withContext UpdateCheckResult.Error(
                    "Limite temporário da API do GitHub atingido. Tente novamente em alguns minutos."
                )
            }
            if (code !in 200..299) {
                return@withContext UpdateCheckResult.Error(
                    "GitHub retornou erro HTTP $code ao consultar atualizações."
                )
            }

            val body = conn.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(body)
            val tagName = json.optString("tag_name", "").trim()
            if (tagName.isBlank()) {
                return@withContext UpdateCheckResult.Error("A última Release não possui tag de versão.")
            }

            if (!VersionComparator.isNewerVersion(tagName, currentVersion)) {
                return@withContext UpdateCheckResult.UpToDate(tagName)
            }

            val assets = json.optJSONArray("assets")
            var apkUrl: String? = null
            var apkName = "leonida-update.apk"
            var apkSize = 0L

            if (assets != null) {
                for (i in 0 until assets.length()) {
                    val asset = assets.getJSONObject(i)
                    val name = asset.optString("name", "")
                    if (name.endsWith(".apk", ignoreCase = true)) {
                        apkUrl = asset.optString("browser_download_url", "")
                        apkName = name
                        apkSize = asset.optLong("size", 0L)
                        break
                    }
                }
            }

            if (apkUrl.isNullOrBlank()) {
                return@withContext UpdateCheckResult.Error(
                    "A versão $tagName existe no GitHub, mas ainda não tem um arquivo .apk anexado à Release."
                )
            }

            val title = json.optString("name", tagName).ifBlank { tagName }
            val notes = json.optString("body", "").ifBlank {
                "Melhorias gerais, novos dados e ajustes para sua jogatina de GTA VI."
            }

            UpdateCheckResult.UpdateAvailable(
                ReleaseInfo(
                    tagName = tagName,
                    title = title,
                    notes = notes,
                    apkDownloadUrl = apkUrl,
                    apkFileName = apkName,
                    apkSizeBytes = apkSize
                )
            )
        } catch (e: Exception) {
            UpdateCheckResult.Error(
                "Não foi possível conectar ao GitHub. Verifique sua internet (${e.javaClass.simpleName})."
            )
        } finally {
            conn?.disconnect()
        }
    }

    suspend fun downloadApk(
        release: ReleaseInfo,
        onProgress: (Int) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        var conn: HttpURLConnection? = null
        try {
            val updatesDir = File(context.cacheDir, "updates").apply { mkdirs() }
            // Clean previous APKs
            updatesDir.listFiles()?.forEach { it.delete() }
            val targetFile = File(updatesDir, "leonida-update.apk")

            conn = (URL(release.apkDownloadUrl).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                instanceFollowRedirects = true
                connectTimeout = 15_000
                readTimeout = 30_000
                setRequestProperty("User-Agent", "LeonidaCompanion-Android")
            }

            val code = conn.responseCode
            if (code !in 200..299) {
                return@withContext Result.failure(IllegalStateException("Falha no download (HTTP $code)"))
            }

            val totalBytes = conn.contentLengthLong.takeIf { it > 0 } ?: release.apkSizeBytes
            var downloaded = 0L
            var lastReported = -1

            conn.inputStream.use { input ->
                FileOutputStream(targetFile).use { output ->
                    val buffer = ByteArray(16 * 1024)
                    while (true) {
                        val read = input.read(buffer)
                        if (read == -1) break
                        output.write(buffer, 0, read)
                        downloaded += read
                        if (totalBytes > 0) {
                            val pct = ((downloaded * 100) / totalBytes).toInt().coerceIn(0, 100)
                            if (pct != lastReported) {
                                lastReported = pct
                                onProgress(pct)
                            }
                        }
                    }
                    output.flush()
                }
            }

            onProgress(100)
            Result.success(targetFile)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            conn?.disconnect()
        }
    }

    fun canRequestPackageInstalls(): Boolean {
        return context.packageManager.canRequestPackageInstalls()
    }

    fun buildInstallUnknownAppsSettingsIntent(): Intent {
        return Intent(
            Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
            Uri.parse("package:${context.packageName}")
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    fun buildInstallApkIntent(apkFile: File): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )
        return Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}
