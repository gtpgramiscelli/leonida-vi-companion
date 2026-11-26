package com.leonida.gtavicompanion.model

import androidx.compose.ui.graphics.Color
import com.leonida.gtavicompanion.ui.theme.RegionAmbrosia
import com.leonida.gtavicompanion.ui.theme.RegionGrassrivers
import com.leonida.gtavicompanion.ui.theme.RegionLeonidaKeys
import com.leonida.gtavicompanion.ui.theme.RegionMountKalaga
import com.leonida.gtavicompanion.ui.theme.RegionPortGellhorn
import com.leonida.gtavicompanion.ui.theme.RegionViceCity
import com.leonida.gtavicompanion.ui.theme.ViceNeonPink
import com.leonida.gtavicompanion.ui.theme.ViceOceanCyan
import com.leonida.gtavicompanion.ui.theme.VicePalmMint
import com.leonida.gtavicompanion.ui.theme.VicePurpleGlow
import com.leonida.gtavicompanion.ui.theme.ViceSunsetGold
import com.leonida.gtavicompanion.ui.theme.ViceSunsetOrange

enum class PlatformType(val label: String, val holdButton: String, val tapButton: String) {
    PS5("PS5", "L1", "R1"),
    XBOX("Xbox Series", "LB", "RB")
}

enum class LeonidaRegion(
    val displayName: String,
    val county: String,
    val cardinal: String,
    val vibe: String,
    val accent: Color
) {
    VICE_CITY(
        displayName = "Vice City",
        county = "Vice-Dale County",
        cardinal = "Costa Leste",
        vibe = "Neon, praias Art Déco, arranha-céus e submundo urbano inspirado em Miami.",
        accent = RegionViceCity
    ),
    LEONIDA_KEYS(
        displayName = "Leonida Keys",
        county = "Mariana County",
        cardinal = "Extremo Sul",
        vibe = "Arquipélago tropical ligado por pontes oceânicas, contrabando e vida náutica.",
        accent = RegionLeonidaKeys
    ),
    GRASSRIVERS(
        displayName = "Grassrivers",
        county = "Mariana County",
        cardinal = "Centro-Sul",
        vibe = "Pântanos densos estilo Everglades, aerobarcos, jacarés e trilhas enlameadas.",
        accent = RegionGrassrivers
    ),
    PORT_GELLHORN(
        displayName = "Port Gellhorn",
        county = "Kelly County",
        cardinal = "Costa Oeste",
        vibe = "Cidade portuária industrial na costa do Golfo, motéis decadentes e corridas.",
        accent = RegionPortGellhorn
    ),
    AMBROSIA(
        displayName = "Ambrosia",
        county = "Leonard County",
        cardinal = "Interior Central",
        vibe = "Coração rural e industrial ao redor do Lago Leonida, canaviais e refinarias.",
        accent = RegionAmbrosia
    ),
    MOUNT_KALAGA(
        displayName = "Mount Kalaga",
        county = "Lummox County",
        cardinal = "Extremo Norte",
        vibe = "Montanhas florestadas, Parque Nacional, cânions, trilhas e exploração off-road.",
        accent = RegionMountKalaga
    )
}

enum class SecretCategory(val label: String, val color: Color) {
    PARANORMAL("Paranormal & Criptídeos", VicePurpleGlow),
    EASTER_EGG("Easter Eggs & TV", ViceNeonPink),
    VEHICLE_MILITARY("Veículos & Militar", ViceSunsetOrange),
    COLLECTIBLE("Segredos & Rádio", ViceOceanCyan),
    ROCKSTAR_NOD("Clássicos Rockstar", ViceSunsetGold)
}

data class SecretLocation(
    val id: String,
    val title: String,
    val subtitle: String,
    val region: LeonidaRegion,
    val subArea: String,
    /** Normalized [0f..1f] coordinates on the Leonida vector map */
    val mapX: Float,
    val mapY: Float,
    val category: SecretCategory,
    val minChapter: Int,
    val timeWindow: String,
    val howToFind: String,
    val reward: String
)

enum class CheatCategory(val label: String, val color: Color) {
    PLAYER("Jogador", ViceOceanCyan),
    COMBAT("Combate", ViceNeonPink),
    VEHICLE("Veículos", ViceSunsetOrange),
    ENVIRONMENT("Mundo & Clima", VicePalmMint),
    FILTER("Filtros de Tela", VicePurpleGlow)
}

enum class CheatDirection(val arrow: String, val ptName: String) {
    UP("↑", "Cima"),
    DOWN("↓", "Baixo"),
    LEFT("←", "Esq"),
    RIGHT("→", "Dir")
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

enum class ChecklistCategory(val label: String, val subtitle: String, val color: Color) {
    STORY_TASKS(
        "História & Empreitadas",
        "12 requisitos de campanha, golpes e receptação",
        ViceNeonPink
    ),
    EXPLORATION_ACTIVITIES(
        "Exploração & Atividades",
        "20 requisitos de minigames, corridas e desafios",
        ViceOceanCyan
    ),
    DISCOVERY(
        "Descoberta & Catálogo",
        "3 requisitos de mapa, WAiNK e fauna",
        VicePalmMint
    )
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
    val githubRepo: String = ""
)
