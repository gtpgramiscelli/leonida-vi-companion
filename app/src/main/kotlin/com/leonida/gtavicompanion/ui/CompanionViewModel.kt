package com.leonida.gtavicompanion.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.leonida.gtavicompanion.BuildConfig
import com.leonida.gtavicompanion.data.GameScreenOcrParser
import com.leonida.gtavicompanion.data.GitHubAppUpdater
import com.leonida.gtavicompanion.data.GtaViCatalog
import com.leonida.gtavicompanion.data.ImageOcrScanner
import com.leonida.gtavicompanion.data.ProgressMerger
import com.leonida.gtavicompanion.data.ProgressRepository
import com.leonida.gtavicompanion.data.PsnProfileSyncClient
import com.leonida.gtavicompanion.data.PsnSyncResult
import com.leonida.gtavicompanion.data.ReleaseInfo
import com.leonida.gtavicompanion.data.UpdateCheckResult
import com.leonida.gtavicompanion.data.VersionComparator
import com.leonida.gtavicompanion.model.PlatformType
import com.leonida.gtavicompanion.model.SyncDetection
import com.leonida.gtavicompanion.model.UserProgressState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

sealed interface UpdateUiState {
    data object Idle : UpdateUiState
    data object NotConfigured : UpdateUiState
    data object Checking : UpdateUiState
    data class UpToDate(val version: String) : UpdateUiState
    data class UpdateAvailable(val release: ReleaseInfo) : UpdateUiState
    data class Downloading(val release: ReleaseInfo, val progressPercent: Int) : UpdateUiState
    data class ReadyToInstall(
        val release: ReleaseInfo,
        val apkFile: File,
        val needsUnknownSourcesPermission: Boolean
    ) : UpdateUiState
    data class Error(val message: String) : UpdateUiState
}

sealed interface SyncUiState {
    data object Idle : SyncUiState
    data object ScanningOcr : SyncUiState
    data object SyncingPsn : SyncUiState
    data class PreviewDetection(
        val detection: SyncDetection,
        val isCameraCapture: Boolean
    ) : SyncUiState
    data class AppliedSuccess(val title: String, val changes: List<String>) : SyncUiState
    data class InfoMessage(val title: String, val message: String) : SyncUiState
}

class CompanionViewModel(
    private val repository: ProgressRepository,
    private val updater: GitHubAppUpdater? = null,
    private val psnClient: PsnProfileSyncClient? = null,
    private val ocrScanner: ImageOcrScanner? = null
) : ViewModel() {

    private val _state = MutableStateFlow(repository.load())
    val state: StateFlow<UserProgressState> = _state.asStateFlow()

    private val _updateState = MutableStateFlow<UpdateUiState>(
        if (_state.value.githubRepo.isBlank()) UpdateUiState.NotConfigured else UpdateUiState.Idle
    )
    val updateState: StateFlow<UpdateUiState> = _updateState.asStateFlow()

    private val _syncState = MutableStateFlow<SyncUiState>(SyncUiState.Idle)
    val syncState: StateFlow<SyncUiState> = _syncState.asStateFlow()

    val currentAppVersion: String = BuildConfig.VERSION_NAME
    val totalChecklistCount: Int = GtaViCatalog.completionChecklist.size
    val totalSecretsCount: Int = GtaViCatalog.secretLocations.size
    val totalCheatsTrackedCount: Int = GtaViCatalog.cheats.size
    val totalFunFactsCount: Int = GtaViCatalog.funFacts.size
    val totalMappedTrophiesCount: Int = GtaViCatalog.ps5Trophies.size

    private var didInitialAutoCheck = false

    fun checkForUpdateOnStartupIfConfigured() {
        if (didInitialAutoCheck) return
        didInitialAutoCheck = true
        if (_state.value.githubRepo.isNotBlank()) {
            checkForUpdate(silent = true)
        }
    }

    fun saveGithubRepo(rawInput: String): Boolean {
        val trimmed = rawInput.trim()
        if (trimmed.isEmpty()) {
            updateAndSave { it.copy(githubRepo = "") }
            _updateState.value = UpdateUiState.NotConfigured
            return true
        }
        val normalized = VersionComparator.normalizeRepo(trimmed) ?: return false
        updateAndSave { it.copy(githubRepo = normalized) }
        checkForUpdate(silent = false)
        return true
    }

    fun checkForUpdate(silent: Boolean = false) {
        val repo = _state.value.githubRepo
        if (repo.isBlank()) {
            _updateState.value = UpdateUiState.NotConfigured
            return
        }
        val client = updater ?: return
        if (_updateState.value is UpdateUiState.Downloading) return

        if (!silent) {
            _updateState.value = UpdateUiState.Checking
        }

        viewModelScope.launch {
            when (val result = client.checkForUpdate(repo, currentAppVersion)) {
                is UpdateCheckResult.UpdateAvailable -> {
                    _updateState.value = UpdateUiState.UpdateAvailable(result.release)
                }
                is UpdateCheckResult.UpToDate -> {
                    if (!silent) {
                        _updateState.value = UpdateUiState.UpToDate(currentAppVersion)
                    } else if (_updateState.value !is UpdateUiState.UpdateAvailable) {
                        _updateState.value = UpdateUiState.UpToDate(currentAppVersion)
                    }
                }
                is UpdateCheckResult.Error -> {
                    if (!silent) {
                        _updateState.value = UpdateUiState.Error(result.message)
                    }
                }
            }
        }
    }

    fun downloadUpdate(release: ReleaseInfo) {
        val client = updater ?: return
        _updateState.value = UpdateUiState.Downloading(release, 0)
        viewModelScope.launch {
            val result = client.downloadApk(release) { pct ->
                _updateState.value = UpdateUiState.Downloading(release, pct)
            }
            result.fold(
                onSuccess = { apkFile ->
                    val needsPerm = !client.canRequestPackageInstalls()
                    _updateState.value = UpdateUiState.ReadyToInstall(
                        release = release,
                        apkFile = apkFile,
                        needsUnknownSourcesPermission = needsPerm
                    )
                },
                onFailure = { err ->
                    _updateState.value = UpdateUiState.Error(
                        "Erro ao baixar APK: ${err.message ?: "verifique sua conexão"}"
                    )
                }
            )
        }
    }

    fun launchInstall(context: Context) {
        val client = updater ?: return
        val current = _updateState.value as? UpdateUiState.ReadyToInstall ?: return
        if (!client.canRequestPackageInstalls()) {
            _updateState.value = current.copy(needsUnknownSourcesPermission = true)
            context.startActivity(client.buildInstallUnknownAppsSettingsIntent())
        } else {
            _updateState.value = current.copy(needsUnknownSourcesPermission = false)
            context.startActivity(client.buildInstallApkIntent(current.apkFile))
        }
    }

    fun refreshInstallPermissionState() {
        val client = updater ?: return
        val current = _updateState.value as? UpdateUiState.ReadyToInstall ?: return
        _updateState.value = current.copy(
            needsUnknownSourcesPermission = !client.canRequestPackageInstalls()
        )
    }

    // =========================================================================
    // PS5 SYNC (PSN PUBLIC TROPHIES + TV CAMERA / PS APP OCR — CUMULATIVE)
    // =========================================================================

    fun savePsnIdAndSync(rawPsnId: String) {
        val clean = rawPsnId.trim()
        updateAndSave { it.copy(psnOnlineId = clean) }
        val client = psnClient ?: return
        _syncState.value = SyncUiState.SyncingPsn

        viewModelScope.launch {
            when (val res = client.syncPublicProfile(clean)) {
                is PsnSyncResult.Success -> {
                    applyDetectionInternal(res.detection, isCameraCapture = false)
                }
                is PsnSyncResult.FallbackNeeded -> {
                    _syncState.value = SyncUiState.InfoMessage(res.title, res.message)
                }
            }
        }
    }

    fun scanImageUri(uri: Uri, sourceLabel: String, isCameraCapture: Boolean) {
        val scanner = ocrScanner ?: return
        _syncState.value = SyncUiState.ScanningOcr

        viewModelScope.launch {
            val ocrResult = scanner.recognizeTextFromUri(uri)
            ocrResult.fold(
                onSuccess = { text ->
                    val parsed = GameScreenOcrParser.parse(text, sourceLabel = sourceLabel)
                    if (!parsed.hasAnySignal) {
                        _syncState.value = SyncUiState.InfoMessage(
                            title = "Nenhum dado de GTA VI reconhecido",
                            message = "Não encontramos números de progresso ou nomes de troféus nítidos nessa imagem. Tente chegar um pouco mais perto da TV (60–100 cm), desligue o flash e enquadre o menu de Progresso/Estatísticas ou Troféus."
                        )
                    } else {
                        _syncState.value = SyncUiState.PreviewDetection(
                            detection = parsed,
                            isCameraCapture = isCameraCapture
                        )
                    }
                },
                onFailure = { err ->
                    _syncState.value = SyncUiState.InfoMessage(
                        title = "Erro ao ler imagem",
                        message = "Não foi possível processar a imagem (${err.message ?: "tente novamente"})."
                    )
                }
            )
        }
    }

    fun confirmAndApplyDetection(detection: SyncDetection, isCameraCapture: Boolean) {
        applyDetectionInternal(detection, isCameraCapture)
    }

    private fun applyDetectionInternal(detection: SyncDetection, isCameraCapture: Boolean) {
        var resultChanges: List<String> = emptyList()
        updateAndSave { current ->
            val merged = ProgressMerger.merge(
                current = current,
                detection = detection,
                incrementCameraScanCount = isCameraCapture
            )
            resultChanges = merged.changesSummary
            merged.updatedState
        }
        _syncState.value = SyncUiState.AppliedSuccess(
            title = if (resultChanges.isEmpty()) {
                "Leitura confirmada — você já estava em dia!"
            } else {
                "Progresso somado com sucesso!"
            },
            changes = resultChanges.ifEmpty {
                listOf("Os dados detectados via ${detection.sourceLabel} já constavam no seu progresso atual.")
            }
        )
    }

    fun togglePs5Trophy(trophyId: String) {
        val alreadyUnlocked = trophyId in _state.value.unlockedTrophyIds
        if (alreadyUnlocked) {
            updateAndSave { current ->
                current.copy(unlockedTrophyIds = current.unlockedTrophyIds - trophyId)
            }
        } else {
            val trophyName = GtaViCatalog.ps5Trophies.find { it.id == trophyId }?.nameEn ?: "Troféu PS5"
            val detection = SyncDetection(
                sourceLabel = "Troféu PS5 ($trophyName)",
                detectedTrophyIds = setOf(trophyId)
            )
            updateAndSave { current ->
                ProgressMerger.merge(current, detection).updatedState
            }
        }
    }

    fun dismissSyncFeedback() {
        _syncState.value = SyncUiState.Idle
    }

    // =========================================================================
    // STANDARD PROGRESS TRACKING
    // =========================================================================

    fun completionFraction(current: UserProgressState = _state.value): Float {
        if (totalChecklistCount == 0) return 0f
        return (current.completedChecklistIds.size.toFloat() / totalChecklistCount).coerceIn(0f, 1f)
    }

    fun setCurrentChapter(chapter: Int) {
        updateAndSave { it.copy(currentChapter = chapter.coerceIn(0, 6)) }
    }

    fun setPlatform(platform: PlatformType) {
        updateAndSave { it.copy(platform = platform) }
    }

    fun toggleSecretFound(secretId: String) {
        updateAndSave { current ->
            val updated = current.foundSecretIds.toMutableSet()
            if (!updated.add(secretId)) updated.remove(secretId)
            current.copy(foundSecretIds = updated)
        }
    }

    fun toggleCheatUnlocked(cheatId: String) {
        updateAndSave { current ->
            val updated = current.unlockedCheatIds.toMutableSet()
            if (!updated.add(cheatId)) updated.remove(cheatId)
            current.copy(unlockedCheatIds = updated)
        }
    }

    fun toggleCheatFavorite(cheatId: String) {
        updateAndSave { current ->
            val updated = current.favoriteCheatIds.toMutableSet()
            if (!updated.add(cheatId)) updated.remove(cheatId)
            current.copy(favoriteCheatIds = updated)
        }
    }

    fun toggleChecklistItem(itemId: String) {
        updateAndSave { current ->
            val updated = current.completedChecklistIds.toMutableSet()
            if (!updated.add(itemId)) updated.remove(itemId)
            current.copy(completedChecklistIds = updated)
        }
    }

    fun toggleFunFactSeen(factId: String) {
        updateAndSave { current ->
            val updated = current.seenFunFactIds.toMutableSet()
            if (!updated.add(factId)) updated.remove(factId)
            current.copy(seenFunFactIds = updated)
        }
    }

    fun adjustWainkCount(delta: Int) {
        updateAndSave { current ->
            val next = (current.wainkScannedCount + delta).coerceIn(0, 277)
            val checklist = current.completedChecklistIds.toMutableSet()
            if (next >= 200) checklist.add("disc_waink")
            current.copy(wainkScannedCount = next, completedChecklistIds = checklist)
        }
    }

    fun adjustAnimalsCount(delta: Int) {
        updateAndSave { current ->
            val next = (current.animalsStudiedCount + delta).coerceIn(0, 255)
            val checklist = current.completedChecklistIds.toMutableSet()
            if (next >= 100) checklist.add("disc_animals")
            current.copy(animalsStudiedCount = next, completedChecklistIds = checklist)
        }
    }

    fun adjustStickUpsCount(delta: Int) {
        updateAndSave { current ->
            val next = (current.stickUpsCount + delta).coerceIn(0, 91)
            val checklist = current.completedChecklistIds.toMutableSet()
            if (next >= 50) checklist.add("story_stickups")
            current.copy(stickUpsCount = next, completedChecklistIds = checklist)
        }
    }

    fun adjustShootingGoldCount(delta: Int) {
        updateAndSave { current ->
            val next = (current.shootingGoldCount + delta).coerceIn(0, 42)
            val checklist = current.completedChecklistIds.toMutableSet()
            if (next >= 42) checklist.add("act_shooting")
            current.copy(shootingGoldCount = next, completedChecklistIds = checklist)
        }
    }

    private fun updateAndSave(transform: (UserProgressState) -> UserProgressState) {
        _state.update { old ->
            val next = transform(old)
            repository.save(next)
            next
        }
    }

    companion object {
        fun factory(
            repository: ProgressRepository,
            updater: GitHubAppUpdater? = null,
            psnClient: PsnProfileSyncClient? = null,
            ocrScanner: ImageOcrScanner? = null
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CompanionViewModel(repository, updater, psnClient, ocrScanner) as T
                }
            }
    }
}
