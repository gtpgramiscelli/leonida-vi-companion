package com.leonida.gtavicompanion.model

import androidx.compose.ui.graphics.Color
import com.leonida.gtavicompanion.ui.theme.LeonidaColors

enum class PlatformType(val label: String, val holdButton: String, val tapButton: String) {
    PS5("PS5", "L1", "R1"),
    XBOX("Xbox Series", "LB", "RB")
}

enum class LeonidaRegion(
    val displayName: String,
    val county: String,
    val cardinal: String,
    val vibe: String
) {
    VICE_CITY(
        displayName = "Vice City",
        county = "Vice-Dale County",
        cardinal = "Costa Leste",
        vibe = "Neon, praias Art Déco, arranha-céus e submundo urbano inspirado em Miami."
    ),
    LEONIDA_KEYS(
        displayName = "Leonida Keys",
        county = "Mariana County",
        cardinal = "Extremo Sul",
        vibe = "Arquipélago tropical ligado por pontes oceânicas, contrabando e vida náutica."
    ),
    GRASSRIVERS(
        displayName = "Grassrivers",
        county = "Mariana County",
        cardinal = "Centro-Sul",
        vibe = "Pântanos densos estilo Everglades, aerobarcos, jacarés e trilhas enlameadas."
    ),
    PORT_GELLHORN(
        displayName = "Port Gellhorn",
        county = "Kelly County",
        cardinal = "Costa Oeste",
        vibe = "Cidade portuária industrial na costa do Golfo, motéis decadentes e corridas."
    ),
    AMBROSIA(
        displayName = "Ambrosia",
        county = "Leonard County",
        cardinal = "Interior Central",
        vibe = "Coração rural e industrial ao redor do Lago Leonida, canaviais e refinarias."
    ),
    MOUNT_KALAGA(
        displayName = "Mount Kalaga",
        county = "Lummox County",
        cardinal = "Extremo Norte",
        vibe = "Montanhas florestadas, Parque Nacional, cânions, trilhas e exploração off-road."
    );

    fun accent(colors: LeonidaColors): Color = when (this) {
        VICE_CITY -> colors.pink
        LEONIDA_KEYS -> colors.cyan
        GRASSRIVERS -> colors.mint
        PORT_GELLHORN -> colors.orange
        AMBROSIA -> colors.gold
        MOUNT_KALAGA -> colors.purple
    }
}

enum class SecretCategory(val label: String) {
    PARANORMAL("Paranormal & Criptídeos"),
    EASTER_EGG("Easter Eggs & TV"),
    VEHICLE_MILITARY("Veículos & Militar"),
    COLLECTIBLE("Segredos & Rádio"),
    ROCKSTAR_NOD("Clássicos Rockstar");

    fun accent(colors: LeonidaColors): Color = when (this) {
        PARANORMAL -> colors.purple
        EASTER_EGG -> colors.pink
        VEHICLE_MILITARY -> colors.orange
        COLLECTIBLE -> colors.cyan
        ROCKSTAR_NOD -> colors.gold
    }
}

data class SecretLocation(
    val id: String,
    val title: String,
    val subtitle: String,
    val region: LeonidaRegion,
    val subArea: String,
    /** Normalized [0f..1f] coordinates calibrated on the real Leonida atlas map */
    val mapX: Float,
    val mapY: Float,
    val category: SecretCategory,
    val minChapter: Int,
    val timeWindow: String,
    val howToFind: String,
    val reward: String
)

enum class MapLayerStyle(val label: String, val shortLabel: String) {
    ATLAS("Atlas (Dia)", "Dia"),
    DARK_ATLAS("Tático (Noite)", "Noite"),
    SATELLITE("Satélite / Terreno", "Satélite")
}

enum class LandmarkKind(val label: String) {
    REGION("Região"),
    COUNTY("Condado"),
    TOWN("Cidade"),
    NEIGHBORHOOD("Bairro"),
    ISLAND("Ilha / Key"),
    AIRPORT("Aeroporto"),
    WATER("Lago / Baía"),
    LANDMARK("Marco"),
    HIGHWAY("Rodovia")
}

data class MapLandmark(
    val id: String,
    val name: String,
    val region: LeonidaRegion?,
    val county: String? = null,
    val kind: LandmarkKind,
    val mapX: Float,
    val mapY: Float,
    val minZoom: Float = 1.0f,
    val maxZoom: Float? = null,
    val defaultZoom: Float? = null,
    val subtitle: String = ""
)

data class MapLayerAsset(
    val style: MapLayerStyle,
    val assetPath: String,
    val displayName: String = style.label,
    val oceanColorHex: String? = null,
    val isDarkLayer: Boolean = style == MapLayerStyle.DARK_ATLAS
)

data class MapAtlasManifest(
    val imageWidth: Int,
    val imageHeight: Int,
    val metersPerPixel: Float = 6.4f,
    val sourceAttribution: String,
    val layers: List<MapLayerAsset>,
    val secretCoordinates: Map<String, Pair<Float, Float>>,
    val landmarks: List<MapLandmark>
)

sealed interface MapSearchResult {
    val id: String
    val title: String
    val subtitle: String
    val mapX: Float
    val mapY: Float
    val region: LeonidaRegion?

    data class Secret(val location: SecretLocation) : MapSearchResult {
        override val id: String get() = "secret_${location.id}"
        override val title: String get() = location.title
        override val subtitle: String get() = "${location.subArea} • ${location.region.displayName}"
        override val mapX: Float get() = location.mapX
        override val mapY: Float get() = location.mapY
        override val region: LeonidaRegion get() = location.region
    }

    data class Place(val landmark: MapLandmark) : MapSearchResult {
        override val id: String get() = "place_${landmark.id}"
        override val title: String get() = landmark.name
        override val subtitle: String
            get() = listOfNotNull(
                landmark.kind.label,
                landmark.region?.displayName,
                landmark.county
            ).distinct().joinToString(" • ")
        override val mapX: Float get() = landmark.mapX
        override val mapY: Float get() = landmark.mapY
        override val region: LeonidaRegion? get() = landmark.region
    }
}

enum class CheatCategory(val label: String) {
    PLAYER("Jogador"),
    COMBAT("Combate"),
    VEHICLE("Veículos"),
    ENVIRONMENT("Mundo & Clima"),
    FILTER("Filtros de Tela");

    fun accent(colors: LeonidaColors): Color = when (this) {
        PLAYER -> colors.cyan
        COMBAT -> colors.pink
        VEHICLE -> colors.orange
        ENVIRONMENT -> colors.mint
        FILTER -> colors.purple
    }
}

enum class CheatDirection(val arrow: String, val ptName: String) {
    UP("↑", "Cima"),
    DOWN("↓", "Baixo"),
    LEFT("←", "Esq"),
    RIGHT("→", "Dir");

    fun accent(colors: LeonidaColors): Color = when (this) {
        UP -> colors.cyan
        DOWN -> colors.pink
        LEFT -> colors.gold
        RIGHT -> colors.mint
    }
}

data class CheatCode(
    val id: String,
    val title: String,
    val description: String,
    val category: CheatCategory,
    /** Null if directional sequence is viewed directly in Progress > Cheats after unlock */
    val sequence: List<CheatDirection>?,
    val unlockRequirement: String,
    val extraNote: String? = null
)

data class StoryChapter(
    val number: Int,
    val title: String,
    val subtitle: String,
    val region: LeonidaRegion,
    val approxMissions: Int,
    /** Safe to read as soon as the player ENTERS this chapter (zero end-of-chapter spoilers) */
    val safeEntryPremise: String,
    /** Safe to read once the player has FINISHED this chapter (recap without spoiling future chapters) */
    val completedRecap: String,
    val keyCharacters: List<String>,
    val gameplayTips: List<String>
)

enum class ChecklistCategory(val label: String, val subtitle: String) {
    STORY_TASKS(
        "História & Empreitadas",
        "12 requisitos de campanha, golpes e receptação"
    ),
    EXPLORATION_ACTIVITIES(
        "Exploração & Atividades",
        "20 requisitos de minigames, corridas e desafios"
    ),
    DISCOVERY(
        "Descoberta & Catálogo",
        "3 requisitos de mapa, WAiNK e fauna"
    );

    fun accent(colors: LeonidaColors): Color = when (this) {
        STORY_TASKS -> colors.pink
        EXPLORATION_ACTIVITIES -> colors.cyan
        DISCOVERY -> colors.mint
    }
}

data class ChecklistItem(
    val id: String,
    val title: String,
    val requirement: String,
    val category: ChecklistCategory,
    val tip: String
)

data class FunFact(
    val id: String,
    val title: String,
    val tag: String,
    val summary: String,
    val whereToSpot: String,
    val minChapter: Int = 0
)

data class GameplayTip(
    val id: String,
    val title: String,
    val badge: String,
    val body: String
)

data class CharacterProfile(
    val id: String,
    val name: String,
    val role: String,
    val region: LeonidaRegion,
    val bio: String,
    val minChapter: Int
)

enum class TrophyTier(val label: String) {
    PLATINUM("Platina"),
    GOLD("Ouro"),
    SILVER("Prata"),
    BRONZE("Bronze");

    fun accent(colors: LeonidaColors): Color = when (this) {
        PLATINUM -> colors.cyan
        GOLD -> colors.gold
        SILVER -> colors.silver
        BRONZE -> colors.orange
    }
}

data class Ps5Trophy(
    val id: String,
    val nameEn: String,
    val namePt: String,
    val description: String,
    val tier: TrophyTier,
    val isHiddenStory: Boolean = false,
    val minChapterVisible: Int = 0,
    val grantsChapter: Int? = null,
    val grantsChecklistIds: List<String> = emptyList(),
    val grantsSecretIds: List<String> = emptyList(),
    val grantsCheatIds: List<String> = emptyList(),
    val minWainkCount: Int? = null,
    val minShootingGoldCount: Int? = null
)

data class SyncDetection(
    val sourceLabel: String,
    val detectedChapter: Int? = null,
    val detectedWaink: Int? = null,
    val detectedAnimals: Int? = null,
    val detectedStickUps: Int? = null,
    val detectedShootingGold: Int? = null,
    val detectedChecklistIds: Set<String> = emptySet(),
    val detectedSecretIds: Set<String> = emptySet(),
    val detectedCheatIds: Set<String> = emptySet(),
    val detectedTrophyIds: Set<String> = emptySet(),
    val highlights: List<String> = emptyList(),
    val rawOcrText: String? = null
) {
    val hasAnySignal: Boolean
        get() = detectedChapter != null ||
            detectedWaink != null ||
            detectedAnimals != null ||
            detectedStickUps != null ||
            detectedShootingGold != null ||
            detectedChecklistIds.isNotEmpty() ||
            detectedSecretIds.isNotEmpty() ||
            detectedCheatIds.isNotEmpty() ||
            detectedTrophyIds.isNotEmpty()
}

/**
 * Story progress level:
 * 0 = Ainda não comecei (Prólogo)
 * 1..5 = Jogando o Capítulo 1..5 (Capítulos futuros bloqueados!)
 * 6 = História Concluída (Pós-Game / Rumo ao 100%)
 */
data class UserProgressState(
    val currentChapter: Int = 1,
    val platform: PlatformType = PlatformType.PS5,
    val foundSecretIds: Set<String> = emptySet(),
    val unlockedCheatIds: Set<String> = emptySet(),
    val favoriteCheatIds: Set<String> = emptySet(),
    val completedChecklistIds: Set<String> = emptySet(),
    val seenFunFactIds: Set<String> = emptySet(),
    val wainkScannedCount: Int = 0,
    val animalsStudiedCount: Int = 0,
    val stickUpsCount: Int = 0,
    val shootingGoldCount: Int = 0,
    val githubRepo: String = "",
    val psnOnlineId: String = "",
    val unlockedTrophyIds: Set<String> = emptySet(),
    val lastSyncSummary: String = "",
    val lastSyncTimestampMs: Long = 0L,
    val cameraScansCount: Int = 0
)
