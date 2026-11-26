package com.leonida.gtavicompanion.ui

import com.leonida.gtavicompanion.data.GameScreenOcrParser
import com.leonida.gtavicompanion.data.GtaViCatalog
import com.leonida.gtavicompanion.data.ProgressMerger
import com.leonida.gtavicompanion.data.ProgressRepository
import com.leonida.gtavicompanion.data.VersionComparator
import com.leonida.gtavicompanion.model.PlatformType
import com.leonida.gtavicompanion.model.SyncDetection
import com.leonida.gtavicompanion.model.UserProgressState
import com.leonida.gtavicompanion.ui.theme.DarkLeonidaColors
import com.leonida.gtavicompanion.ui.theme.LightLeonidaColors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

private class InMemoryProgressRepository(
    private var current: UserProgressState = UserProgressState()
) : ProgressRepository {
    var saveCount: Int = 0
        private set

    override fun load(): UserProgressState = current

    override fun save(state: UserProgressState) {
        current = state
        saveCount++
    }
}

class CompanionViewModelTest {

    @Test
    fun catalogHasAll35OfficialCompletionRequirements() {
        assertEquals(35, GtaViCatalog.completionChecklist.size)
        assertEquals(5, GtaViCatalog.chapters.size)
        assertTrue(GtaViCatalog.secretLocations.size >= 14)
        assertTrue(GtaViCatalog.cheats.size >= 20)
    }

    @Test
    fun allSecretMapCoordinatesAreWithinNormalizedBounds() {
        GtaViCatalog.secretLocations.forEach { secret ->
            assertTrue("${secret.id} mapX out of bounds", secret.mapX in 0f..1f)
            assertTrue("${secret.id} mapY out of bounds", secret.mapY in 0f..1f)
        }
    }

    @Test
    fun chapterProgressClampsBetweenPrologueAndPostGame() {
        val repo = InMemoryProgressRepository()
        val vm = CompanionViewModel(repo)

        vm.setCurrentChapter(10)
        assertEquals(6, vm.state.value.currentChapter)

        vm.setCurrentChapter(-3)
        assertEquals(0, vm.state.value.currentChapter)

        vm.setCurrentChapter(3)
        assertEquals(3, vm.state.value.currentChapter)
        assertEquals(3, repo.saveCount)
    }

    @Test
    fun countersAutoCompleteCorresponding100PercentChecklistItems() {
        val repo = InMemoryProgressRepository()
        val vm = CompanionViewModel(repo)

        vm.adjustWainkCount(200)
        assertEquals(200, vm.state.value.wainkScannedCount)
        assertTrue("disc_waink" in vm.state.value.completedChecklistIds)

        vm.adjustAnimalsCount(100)
        assertEquals(100, vm.state.value.animalsStudiedCount)
        assertTrue("disc_animals" in vm.state.value.completedChecklistIds)

        vm.adjustStickUpsCount(50)
        assertEquals(50, vm.state.value.stickUpsCount)
        assertTrue("story_stickups" in vm.state.value.completedChecklistIds)

        vm.adjustShootingGoldCount(42)
        assertEquals(42, vm.state.value.shootingGoldCount)
        assertTrue("act_shooting" in vm.state.value.completedChecklistIds)

        // Clamping above max
        vm.adjustShootingGoldCount(10)
        assertEquals(42, vm.state.value.shootingGoldCount)
    }

    @Test
    fun togglesPersistSecretsCheatsFavoritesAndPlatform() {
        val repo = InMemoryProgressRepository()
        val vm = CompanionViewModel(repo)

        vm.toggleSecretFound("sec_michaels_boat")
        assertTrue("sec_michaels_boat" in vm.state.value.foundSecretIds)
        vm.toggleSecretFound("sec_michaels_boat")
        assertFalse("sec_michaels_boat" in vm.state.value.foundSecretIds)

        vm.toggleCheatFavorite("cheat_cash_250k")
        vm.toggleCheatUnlocked("cheat_cash_250k")
        assertTrue("cheat_cash_250k" in vm.state.value.favoriteCheatIds)
        assertTrue("cheat_cash_250k" in vm.state.value.unlockedCheatIds)

        vm.setPlatform(PlatformType.XBOX)
        assertEquals(PlatformType.XBOX, vm.state.value.platform)
    }

    @Test
    fun versionComparatorHandlesSemverTagsAndGithubUrls() {
        assertTrue(VersionComparator.isNewerVersion("v1.0.1", "1.0.0"))
        assertTrue(VersionComparator.isNewerVersion("1.2.0", "1.1.9"))
        assertTrue(VersionComparator.isNewerVersion("v2.0", "1.9.9"))
        assertFalse(VersionComparator.isNewerVersion("v1.0.0", "1.0.0"))
        assertFalse(VersionComparator.isNewerVersion("0.9.9", "1.0.0"))

        assertEquals("gabriel/leonida-vi", VersionComparator.normalizeRepo("gabriel/leonida-vi"))
        assertEquals("gabriel/leonida-vi", VersionComparator.normalizeRepo("https://github.com/gabriel/leonida-vi.git"))
        assertEquals("gabriel/leonida-vi", VersionComparator.normalizeRepo("https://github.com/gabriel/leonida-vi/"))
        assertNull(VersionComparator.normalizeRepo("not a valid repo"))
    }

    @Test
    fun saveGithubRepoNormalizesAndPersists() {
        val repo = InMemoryProgressRepository()
        val vm = CompanionViewModel(repo)

        assertFalse(vm.saveGithubRepo("invalido"))
        assertTrue(vm.saveGithubRepo("https://github.com/gabriel/leonida-vi/"))
        assertEquals("gabriel/leonida-vi", vm.state.value.githubRepo)
    }

    @Test
    fun ocrParserReadsBilingualPs5StatsAndTrophiesAccurately() {
        val sampleTvScreen = """
            PROGRESSO > ESTATÍSTICAS
            Capítulo 3
            Veículos Escaneados (WAiNK) 134 / 200
            Animais Estudados 72 / 100
            Stick Ups 50 / 50
            Estande de Tiro 30 / 42
            Operações de Gangues 39 / 39
            The Truth Is Out There
        """.trimIndent()

        val detected = GameScreenOcrParser.parse(sampleTvScreen, "Câmera na TV (OCR)")
        assertTrue(detected.hasAnySignal)
        assertEquals(3, detected.detectedChapter)
        assertEquals(134, detected.detectedWaink)
        assertEquals(72, detected.detectedAnimals)
        assertEquals(50, detected.detectedStickUps)
        assertEquals(30, detected.detectedShootingGold)
        assertTrue("story_gangs" in detected.detectedChecklistIds)
        assertTrue("trophy_truth_out_there" in detected.detectedTrophyIds)
    }

    @Test
    fun cumulativeMergerCombinesPsnTrophiesAndCameraOcrWithoutEverDowngrading() {
        val initial = UserProgressState(
            currentChapter = 2,
            wainkScannedCount = 120,
            animalsStudiedCount = 60,
            foundSecretIds = setOf("sec_michaels_boat")
        )

        // 1. PSN Trophy sync brings Vice City, Baby! (Cap. 4), The Shootist (42/42), and UFO trophy
        val psnDetection = SyncDetection(
            sourceLabel = "PSN",
            detectedTrophyIds = setOf("trophy_vice_city_baby", "trophy_shootist", "trophy_truth_out_there")
        )
        val afterPsn = ProgressMerger.merge(initial, psnDetection).updatedState
        assertEquals(4, afterPsn.currentChapter)
        assertEquals(42, afterPsn.shootingGoldCount)
        assertTrue("act_shooting" in afterPsn.completedChecklistIds)
        assertTrue("cheat_no_reload" in afterPsn.unlockedCheatIds)
        assertTrue("sec_kelly_ufo" in afterPsn.foundSecretIds)
        assertTrue("sec_michaels_boat" in afterPsn.foundSecretIds)

        // 2. Subsequent Camera scan sees older Chapter 2 on recap screen + higher WAiNK 185 & Animals 100
        val ocrDetection = SyncDetection(
            sourceLabel = "Câmera na TV",
            detectedChapter = 2,
            detectedWaink = 185,
            detectedAnimals = 100
        )
        val afterBoth = ProgressMerger.merge(afterPsn, ocrDetection, incrementCameraScanCount = true).updatedState
        // Never downgrades chapter from 4 to 2!
        assertEquals(4, afterBoth.currentChapter)
        // Monotonically upgrades WAiNK and Animals, and auto-checks disc_animals!
        assertEquals(185, afterBoth.wainkScannedCount)
        assertEquals(100, afterBoth.animalsStudiedCount)
        assertTrue("disc_animals" in afterBoth.completedChecklistIds)
        assertEquals(1, afterBoth.cameraScansCount)
    }

    @Test
    fun lightAndDarkThemePalettesHaveDistinctAccessibleSurfaces() {
        assertFalse(LightLeonidaColors.isDark)
        assertTrue(DarkLeonidaColors.isDark)
        assertNotEquals(LightLeonidaColors.background, DarkLeonidaColors.background)
        assertNotEquals(LightLeonidaColors.card, DarkLeonidaColors.card)
        assertNotEquals(LightLeonidaColors.textPrimary, DarkLeonidaColors.textPrimary)
        assertNotEquals(LightLeonidaColors.silver, DarkLeonidaColors.silver)
        assertNotEquals(LightLeonidaColors.mapOceanTop, DarkLeonidaColors.mapOceanTop)
    }
}
