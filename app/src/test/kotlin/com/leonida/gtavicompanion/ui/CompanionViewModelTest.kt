package com.leonida.gtavicompanion.ui

import com.leonida.gtavicompanion.data.GameScreenOcrParser
import com.leonida.gtavicompanion.data.GtaViCatalog
import com.leonida.gtavicompanion.data.MapAtlasRepository
import com.leonida.gtavicompanion.data.MapSearchEngine
import com.leonida.gtavicompanion.data.MapViewportMath
import com.leonida.gtavicompanion.data.ProgressMerger
import com.leonida.gtavicompanion.data.ProgressRepository
import com.leonida.gtavicompanion.data.VersionComparator
import com.leonida.gtavicompanion.model.LandmarkKind
import com.leonida.gtavicompanion.model.LeonidaRegion
import com.leonida.gtavicompanion.model.MapLayerStyle
import com.leonida.gtavicompanion.model.MapSearchResult
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
import java.io.File
import kotlin.math.abs

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

    @Test
    fun realLeonidaPoiManifestParsesAndCalibratesAllSecretsAndLayers() {
        val assetsRoot = listOf(
            File("src/main/assets"),
            File("app/src/main/assets")
        ).first { it.exists() }

        val manifestFile = File(assetsRoot, "map/leonida_pois.json")
        assertTrue("leonida_pois.json must exist in assets/map", manifestFile.exists())

        val mapDirFiles = File(assetsRoot, "map").list().orEmpty().toSet()
        val manifest = MapAtlasRepository.parseManifestJson(
            rawJson = manifestFile.readText(Charsets.UTF_8),
            availableAssetFiles = mapDirFiles
        )

        assertEquals(2560, manifest.imageWidth)
        assertEquals(3200, manifest.imageHeight)
        assertEquals(6.4f, manifest.metersPerPixel, 0.01f)
        assertTrue("CC BY attribution missing", manifest.sourceAttribution.contains("CC BY", ignoreCase = true))

        // Both Day Atlas and Tactical Night layers must be discovered and present on disk
        assertEquals(2, manifest.layers.size)
        assertTrue(manifest.layers.any { it.style == MapLayerStyle.ATLAS })
        assertTrue(manifest.layers.any { it.style == MapLayerStyle.DARK_ATLAS })
        manifest.layers.forEach { layer ->
            val f = File(assetsRoot, layer.assetPath)
            assertTrue("Missing layer asset ${layer.assetPath}", f.exists() && f.length() > 50_000L)
        }

        // All 6 regions, 5 counties, and 45+ real Leonida landmarks parsed with valid normalized bounds
        val regions = manifest.landmarks.filter { it.kind == LandmarkKind.REGION }
        val counties = manifest.landmarks.filter { it.kind == LandmarkKind.COUNTY }
        val detailedPlaces = manifest.landmarks.filter {
            it.kind != LandmarkKind.REGION && it.kind != LandmarkKind.COUNTY
        }
        assertEquals(6, regions.size)
        assertEquals(5, counties.size)
        assertTrue("Expected 45+ detailed places, got ${detailedPlaces.size}", detailedPlaces.size >= 45)
        manifest.landmarks.forEach { lm ->
            assertTrue("${lm.id} mapX out of bounds", lm.mapX in 0f..1f)
            assertTrue("${lm.id} mapY out of bounds", lm.mapY in 0f..1f)
            assertTrue("${lm.id} minZoom invalid", lm.minZoom >= 1.0f)
        }

        // Every single secret in GtaViCatalog is present in leonida_pois.json and matches pixel-calibrated coords
        assertEquals(GtaViCatalog.secretLocations.size, manifest.secretCoordinates.size)
        GtaViCatalog.secretLocations.forEach { secret ->
            val calibrated = manifest.secretCoordinates[secret.id]
            assertTrue("Missing secret ${secret.id} in leonida_pois.json", calibrated != null)
            assertTrue(
                "Secret ${secret.id} X mismatch: catalog=${secret.mapX} vs json=${calibrated!!.first}",
                abs(secret.mapX - calibrated.first) < 0.001f
            )
            assertTrue(
                "Secret ${secret.id} Y mismatch: catalog=${secret.mapY} vs json=${calibrated.second}",
                abs(secret.mapY - calibrated.second) < 0.001f
            )
        }

        // Region focus target resolves cleanly for all 6 Leonida regions
        LeonidaRegion.entries.forEach { reg ->
            val (rx, ry, rz) = MapAtlasRepository.regionFocusTarget(reg, manifest, GtaViCatalog.secretLocations)
            assertTrue(rx in 0.05f..0.95f)
            assertTrue(ry in 0.05f..0.95f)
            assertTrue(rz >= 2.0f)
        }
    }

    @Test
    fun viewportMathPreservesAspectRatioAndRoundTripsCoordinatesAccurately() {
        val vw = 1080f
        val vh = 1600f
        val fit = MapViewportMath.computeFitBounds(vw, vh, 2560, 3200)
        // 2560/3200 = 0.8 aspect; viewport is 1080/1600 = 0.675 (narrower), so width fits 1080 -> height 1350
        assertEquals(1080f, fit.baseWidth, 0.01f)
        assertEquals(1350f, fit.baseHeight, 0.01f)

        val nx = 0.76807f
        val ny = 0.43926f
        val scale = 4.2f
        val (targetPanX, targetPanY) = MapViewportMath.targetPanForPoint(nx, ny, scale, vw, vh, fit)
        val (sx, sy) = MapViewportMath.mapToScreen(nx, ny, scale, targetPanX, targetPanY, vw, vh, fit)
        // Focused secret should land right at viewport center!
        assertEquals(vw / 2f, sx, 0.5f)
        assertEquals(vh / 2f, sy, 0.5f)

        // Round-trip screen -> map returns exact original normalized coordinates
        val (rtX, rtY) = MapViewportMath.screenToMap(sx, sy, scale, targetPanX, targetPanY, vw, vh, fit)
        assertEquals(nx, rtX, 0.0005f)
        assertEquals(ny, rtY, 0.0005f)

        // Centroid pinch zoom keeps the exact map point under the gesture centroid anchored
        val centroidX = 720f
        val centroidY = 540f
        val (beforeNx, beforeNy) = MapViewportMath.screenToMap(centroidX, centroidY, 2.0f, 30f, -45f, vw, vh, fit)
        val (newScale, newPanX, newPanY) = MapViewportMath.applyTransformGesture(
            currentScale = 2.0f,
            currentPanX = 30f,
            currentPanY = -45f,
            centroidX = centroidX,
            centroidY = centroidY,
            panDeltaX = 0f,
            panDeltaY = 0f,
            zoomFactor = 1.6f,
            viewportW = vw,
            viewportH = vh,
            fit = fit
        )
        val (afterNx, afterNy) = MapViewportMath.screenToMap(centroidX, centroidY, newScale, newPanX, newPanY, vw, vh, fit)
        assertEquals(3.2f, newScale, 0.01f)
        assertEquals(beforeNx, afterNx, 0.001f)
        assertEquals(beforeNy, afterNy, 0.001f)

        // GPS readout & dynamic scale bar sanity
        val gps = MapViewportMath.formatGps(0.76807f, 0.43926f)
        assertTrue(gps.contains("°N") && gps.contains("°W"))
        val bar1x = MapViewportMath.computeScaleBar(1f, fit, 3f, 16_384f)
        val bar6x = MapViewportMath.computeScaleBar(6f, fit, 3f, 16_384f)
        assertTrue(bar1x.label.contains("km"))
        assertTrue(bar6x.label.contains("m"))
    }

    @Test
    fun mapSearchEngineFindsSecretsAndLandmarksIgnoringAccents() {
        val assetsRoot = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.exists() }
        val manifest = MapAtlasRepository.parseManifestJson(
            File(assetsRoot, "map/leonida_pois.json").readText(Charsets.UTF_8)
        )

        val dexterResults = MapSearchEngine.search("dexter", GtaViCatalog.secretLocations, manifest.landmarks)
        assertTrue(dexterResults.any { it is MapSearchResult.Secret && it.location.id == "sec_dexter_apt" })

        val vciaResults = MapSearchEngine.search("VCIA", GtaViCatalog.secretLocations, manifest.landmarks)
        assertTrue(vciaResults.any { it is MapSearchResult.Place && it.landmark.id == "lm_vcia" })

        // Diacritic-insensitive search ("instalacao" matches "Instalação de Pesquisa Alienígena")
        val accentResults = MapSearchEngine.search("instalacao", GtaViCatalog.secretLocations, manifest.landmarks)
        assertTrue(accentResults.any { it is MapSearchResult.Secret && it.location.id == "sec_alien_facility" })

        // Searching "Key Lento" returns both the island landmark and secrets located there
        val keyLentoResults = MapSearchEngine.search("key lento", GtaViCatalog.secretLocations, manifest.landmarks)
        assertTrue(keyLentoResults.any { it is MapSearchResult.Place && it.landmark.name == "Key Lento" })
        assertTrue(keyLentoResults.any { it is MapSearchResult.Secret })
    }

    @Test
    fun progressiveDisclosureShowsRegionsAndCountiesAt1xAndRevealsLandmarksOnZoom() {
        val assetsRoot = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.exists() }
        val manifest = MapAtlasRepository.parseManifestJson(
            File(assetsRoot, "map/leonida_pois.json").readText(Charsets.UTF_8)
        )

        // All 6 regions and 5 counties must be immediately opaque (1.0f) at initial 1.0x overview zoom
        val baseOverviewLabels = manifest.landmarks.filter {
            it.kind == LandmarkKind.REGION || it.kind == LandmarkKind.COUNTY
        }
        assertEquals(11, baseOverviewLabels.size)
        baseOverviewLabels.forEach { lm ->
            val alphaAt1x = MapViewportMath.computeLandmarkAlpha(
                scale = 1.0f,
                minZoom = lm.minZoom,
                maxZoom = lm.maxZoom,
                isFocused = false
            )
            assertEquals("${lm.id} must be visible at 1.0x", 1.0f, alphaAt1x, 0.001f)
        }

        // Higher-zoom neighborhoods (e.g. Little Cuba, minZoom=2.0) stay hidden at 1.0x and reveal smoothly on zoom
        val littleCuba = manifest.landmarks.first { it.id == "lm_little_cuba" }
        assertEquals(0f, MapViewportMath.computeLandmarkAlpha(1.0f, littleCuba.minZoom, littleCuba.maxZoom, false), 0.001f)
        assertEquals(1f, MapViewportMath.computeLandmarkAlpha(2.5f, littleCuba.minZoom, littleCuba.maxZoom, false), 0.001f)
        // Focused landmark always renders at 1.0f regardless of zoom
        assertEquals(1f, MapViewportMath.computeLandmarkAlpha(1.0f, littleCuba.minZoom, littleCuba.maxZoom, true), 0.001f)
    }
}
