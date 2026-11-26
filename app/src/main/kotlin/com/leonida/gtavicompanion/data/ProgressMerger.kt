package com.leonida.gtavicompanion.data

import com.leonida.gtavicompanion.model.SyncDetection
import com.leonida.gtavicompanion.model.UserProgressState

data class MergeResult(
    val updatedState: UserProgressState,
    val changesSummary: List<String>
)

/**
 * Combines progress cumulatively and monotonically across ALL sources:
 *  - Manual checks
 *  - PSN PS5 Trophies
 *  - TV Camera / PS App Screenshot OCR
 *
 * Guarantees nothing previously recorded is ever lost or downgraded.
 */
object ProgressMerger {

    fun merge(
        current: UserProgressState,
        detection: SyncDetection,
        nowMs: Long = System.currentTimeMillis(),
        incrementCameraScanCount: Boolean = false
    ): MergeResult {
        val changes = mutableListOf<String>()

        // 1. Expand all implications from newly/already detected PS5 trophies
        val mergedTrophies = current.unlockedTrophyIds + detection.detectedTrophyIds
        val newTrophiesCount = mergedTrophies.size - current.unlockedTrophyIds.size

        var impliedChapter = detection.detectedChapter ?: current.currentChapter
        var impliedWaink = detection.detectedWaink ?: current.wainkScannedCount
        var impliedShootingGold = detection.detectedShootingGold ?: current.shootingGoldCount
        val impliedChecklist = detection.detectedChecklistIds.toMutableSet()
        val impliedSecrets = detection.detectedSecretIds.toMutableSet()
        val impliedCheats = detection.detectedCheatIds.toMutableSet()

        val trophyMap = GtaViCatalog.ps5Trophies.associateBy { it.id }
        detection.detectedTrophyIds.forEach { tid ->
            val trophy = trophyMap[tid] ?: return@forEach
            trophy.grantsChapter?.let { ch -> impliedChapter = maxOf(impliedChapter, ch) }
            trophy.minWainkCount?.let { w -> impliedWaink = maxOf(impliedWaink, w) }
            trophy.minShootingGoldCount?.let { g -> impliedShootingGold = maxOf(impliedShootingGold, g) }
            impliedChecklist.addAll(trophy.grantsChecklistIds)
            impliedSecrets.addAll(trophy.grantsSecretIds)
            impliedCheats.addAll(trophy.grantsCheatIds)
        }

        // 2. Monotonic counters (always take highest verified value)
        val nextChapter = maxOf(current.currentChapter, impliedChapter).coerceIn(0, 6)
        val nextWaink = maxOf(current.wainkScannedCount, impliedWaink).coerceIn(0, 277)
        val nextAnimals = maxOf(current.animalsStudiedCount, detection.detectedAnimals ?: 0).coerceIn(0, 255)
        val nextStickUps = maxOf(current.stickUpsCount, detection.detectedStickUps ?: 0).coerceIn(0, 91)
        val nextShootingGold = maxOf(current.shootingGoldCount, impliedShootingGold).coerceIn(0, 42)

        // Auto-complete checklist thresholds whenever counters reach official goals
        if (nextChapter >= 6) impliedChecklist.add("story_main")
        if (nextWaink >= 200) impliedChecklist.add("disc_waink")
        if (nextAnimals >= 100) impliedChecklist.add("disc_animals")
        if (nextStickUps >= 50) impliedChecklist.add("story_stickups")
        if (nextShootingGold >= 42) impliedChecklist.add("act_shooting")

        val mergedChecklist = current.completedChecklistIds + impliedChecklist
        val mergedSecrets = current.foundSecretIds + impliedSecrets
        val mergedCheats = current.unlockedCheatIds + impliedCheats

        // 3. Build transparent human-readable diff summary
        if (nextChapter > current.currentChapter) {
            val label = if (nextChapter >= 6) "Pós-Game (Zerado!)" else "Capítulo $nextChapter"
            changes.add("História avançada para $label")
        }
        if (newTrophiesCount > 0) {
            changes.add("+$newTrophiesCount troféu(s) PS5 sincronizado(s)")
        }
        if (nextWaink > current.wainkScannedCount) {
            changes.add("WAiNK: ${current.wainkScannedCount} → $nextWaink/200")
        }
        if (nextAnimals > current.animalsStudiedCount) {
            changes.add("Fauna (Binóculos): ${current.animalsStudiedCount} → $nextAnimals/100")
        }
        if (nextStickUps > current.stickUpsCount) {
            changes.add("Stick Ups: ${current.stickUpsCount} → $nextStickUps/50")
        }
        if (nextShootingGold > current.shootingGoldCount) {
            changes.add("Estande Ouro: ${current.shootingGoldCount} → $nextShootingGold/42")
        }
        val addedChecklist = mergedChecklist.size - current.completedChecklistIds.size
        if (addedChecklist > 0) {
            changes.add("+$addedChecklist requisito(s) do 100% marcados")
        }
        val addedSecrets = mergedSecrets.size - current.foundSecretIds.size
        if (addedSecrets > 0) {
            changes.add("+$addedSecrets lugar(es) secreto(s) no mapa")
        }
        val addedCheats = mergedCheats.size - current.unlockedCheatIds.size
        if (addedCheats > 0) {
            changes.add("+$addedCheats cheat(s) desbloqueado(s)")
        }

        val summaryLine = if (changes.isEmpty()) {
            "${detection.sourceLabel}: tudo já estava atualizado"
        } else {
            "${detection.sourceLabel}: ${changes.joinToString(" • ")}"
        }

        val updated = current.copy(
            currentChapter = nextChapter,
            wainkScannedCount = nextWaink,
            animalsStudiedCount = nextAnimals,
            stickUpsCount = nextStickUps,
            shootingGoldCount = nextShootingGold,
            completedChecklistIds = mergedChecklist,
            foundSecretIds = mergedSecrets,
            unlockedCheatIds = mergedCheats,
            unlockedTrophyIds = mergedTrophies,
            lastSyncSummary = summaryLine,
            lastSyncTimestampMs = nowMs,
            cameraScansCount = current.cameraScansCount + if (incrementCameraScanCount) 1 else 0
        )

        return MergeResult(updatedState = updated, changesSummary = changes)
    }
}
