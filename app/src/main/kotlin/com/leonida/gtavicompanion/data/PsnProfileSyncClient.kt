package com.leonida.gtavicompanion.data

import com.leonida.gtavicompanion.model.SyncDetection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

sealed interface PsnSyncResult {
    data class Success(val detection: SyncDetection) : PsnSyncResult
    data class FallbackNeeded(val title: String, val message: String) : PsnSyncResult
}

/**
 * Clean, transparent public PSN trophy profile checker.
 * Respects website access controls — never spoofs Sony first-party app secrets
 * and never attempts to bypass anti-bot/CAPTCHA gates. If public web tracking
 * requires human verification or the PSN profile is private, clearly guides
 * the player to use the instant PS App Screenshot / TV Camera OCR right below.
 */
class PsnProfileSyncClient {

    suspend fun syncPublicProfile(rawPsnId: String): PsnSyncResult = withContext(Dispatchers.IO) {
        val psnId = rawPsnId.trim()
        if (psnId.length < 3 || !psnId.matches(Regex("^[A-Za-z0-9_-]{3,16}$"))) {
            return@withContext PsnSyncResult.FallbackNeeded(
                title = "PSN ID inválida",
                message = "Digite sua PSN Online ID válida (3 a 16 caracteres, apenas letras, números, - ou _)."
            )
        }

        val encoded = URLEncoder.encode(psnId, "UTF-8")
        val candidateUrls = listOf(
            "https://www.exophase.com/psn/user/$encoded/",
            "https://psnprofiles.com/$encoded"
        )

        var sawBotGate = false
        var sawNotFound = false

        for (endpoint in candidateUrls) {
            var conn: HttpURLConnection? = null
            try {
                conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    instanceFollowRedirects = true
                    connectTimeout = 10_000
                    readTimeout = 12_000
                    setRequestProperty("User-Agent", "LeonidaCompanion-Android/1.1.0")
                    setRequestProperty("Accept", "text/html,application/xhtml+xml")
                }

                val code = conn.responseCode
                if (code == 403 || code == 429 || code == 503) {
                    sawBotGate = true
                    continue
                }
                if (code == 404) {
                    sawNotFound = true
                    continue
                }
                if (code !in 200..299) continue

                val html = conn.inputStream.bufferedReader().use { it.readText() }
                if (html.contains("cf-turnstile", ignoreCase = true) ||
                    html.contains("Just a moment", ignoreCase = true)
                ) {
                    sawBotGate = true
                    continue
                }

                // Look for GTA VI game entry link on the public profile page
                val gtaGameUrl = findGtaViGameUrl(html, endpoint)
                if (gtaGameUrl != null) {
                    val gameHtml = fetchSimpleHtml(gtaGameUrl)
                    if (gameHtml != null) {
                        val stripped = stripHtml(gameHtml)
                        val parsed = GameScreenOcrParser.parse(
                            rawText = stripped,
                            sourceLabel = "PSN Pública ($psnId)"
                        )
                        if (parsed.hasAnySignal) {
                            return@withContext PsnSyncResult.Success(parsed)
                        }
                    }
                }
            } catch (_: Exception) {
                // Continue to next public source
            } finally {
                conn?.disconnect()
            }
        }

        return@withContext when {
            sawBotGate -> PsnSyncResult.FallbackNeeded(
                title = "Portal público exige verificação no navegador",
                message = "Os portais públicos da PSN estão com proteção anti-bot ativa agora. Para sincronizar seus troféus em 3 segundos sem risco à sua conta Sony, tire um print da lista de troféus no PlayStation App e toque em \"Importar Print (PS App)\" ou aponte a câmera para a TV!"
            )
            sawNotFound -> PsnSyncResult.FallbackNeeded(
                title = "Perfil PSN não encontrado ou privado",
                message = "Não encontramos o perfil público \"$psnId\". No PS5, verifique se Privacidade > Histórico de Jogos está como \"Qualquer pessoa\", ou use o Scanner de Câmera / Print do PS App logo acima para sincronizar na hora!"
            )
            else -> PsnSyncResult.FallbackNeeded(
                title = "GTA VI ainda não apareceu no perfil público",
                message = "Seu perfil \"$psnId\" pode ainda não ter sincronizado os troféus de GTA VI nos portais públicos. Use o botão \"Importar Print (PS App)\" ou aponte a câmera para a tela de Troféus do PS5 para sincronizar imediatamente!"
            )
        }
    }

    private fun findGtaViGameUrl(profileHtml: String, baseUrl: String): String? {
        val hrefRegex = Regex("""href=["']([^"']*(?:grand-theft-auto-vi|gta-vi|gta-6)[^"']*)["']""", RegexOption.IGNORE_CASE)
        val match = hrefRegex.find(profileHtml) ?: return null
        val path = match.groupValues[1]
        return when {
            path.startsWith("http") -> path
            path.startsWith("/") -> {
                val u = URL(baseUrl)
                "${u.protocol}://${u.host}$path"
            }
            else -> null
        }
    }

    private fun fetchSimpleHtml(urlString: String): String? {
        var conn: HttpURLConnection? = null
        return try {
            conn = (URL(urlString).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                instanceFollowRedirects = true
                connectTimeout = 10_000
                readTimeout = 12_000
                setRequestProperty("User-Agent", "LeonidaCompanion-Android/1.1.0")
            }
            if (conn.responseCode in 200..299) {
                conn.inputStream.bufferedReader().use { it.readText() }
            } else null
        } catch (_: Exception) {
            null
        } finally {
            conn?.disconnect()
        }
    }

    private fun stripHtml(html: String): String {
        return html
            .replace(Regex("<script[^>]*>[\\s\\S]*?</script>", RegexOption.IGNORE_CASE), " ")
            .replace(Regex("<style[^>]*>[\\s\\S]*?</style>", RegexOption.IGNORE_CASE), " ")
            .replace(Regex("<[^>]+>"), "\n")
            .replace("&amp;", "&")
            .replace("&#39;", "'")
            .replace("&quot;", "\"")
    }
}
