package com.leonida.gtavicompanion.data

import android.content.Context
import android.content.SharedPreferences
import com.leonida.gtavicompanion.BuildConfig
import com.leonida.gtavicompanion.model.PlatformType
import com.leonida.gtavicompanion.model.UserProgressState

interface ProgressRepository {
    fun load(): UserProgressState
    fun save(state: UserProgressState)
}

class SharedPrefsProgressRepository(context: Context) : ProgressRepository {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun load(): UserProgressState {
        val platformName = prefs.getString(KEY_PLATFORM, PlatformType.PS5.name)
        val platform = runCatching { PlatformType.valueOf(platformName ?: PlatformType.PS5.name) }
            .getOrDefault(PlatformType.PS5)

        return UserProgressState(
            currentChapter = prefs.getInt(KEY_CURRENT_CHAPTER, 1).coerceIn(0, 6),
            platform = platform,
            foundSecretIds = prefs.getStringSet(KEY_FOUND_SECRETS, emptySet()).orEmpty().toSet(),
            unlockedCheatIds = prefs.getStringSet(KEY_UNLOCKED_CHEATS, emptySet()).orEmpty().toSet(),
            favoriteCheatIds = prefs.getStringSet(KEY_FAVORITE_CHEATS, emptySet()).orEmpty().toSet(),
            completedChecklistIds = prefs.getStringSet(KEY_CHECKLIST, emptySet()).orEmpty().toSet(),
            seenFunFactIds = prefs.getStringSet(KEY_SEEN_FACTS, emptySet()).orEmpty().toSet(),
            wainkScannedCount = prefs.getInt(KEY_WAINK_COUNT, 0).coerceIn(0, 277),
            animalsStudiedCount = prefs.getInt(KEY_ANIMALS_COUNT, 0).coerceIn(0, 255),
            stickUpsCount = prefs.getInt(KEY_STICKUPS_COUNT, 0).coerceIn(0, 91),
            shootingGoldCount = prefs.getInt(KEY_SHOOTING_GOLD_COUNT, 0).coerceIn(0, 42),
            githubRepo = prefs.getString(KEY_GITHUB_REPO, null)
                ?.takeIf { it.isNotBlank() }
                ?: BuildConfig.DEFAULT_GITHUB_REPO
        )
    }

    override fun save(state: UserProgressState) {
        prefs.edit()
            .putInt(KEY_CURRENT_CHAPTER, state.currentChapter)
            .putString(KEY_PLATFORM, state.platform.name)
            .putStringSet(KEY_FOUND_SECRETS, state.foundSecretIds.toSet())
            .putStringSet(KEY_UNLOCKED_CHEATS, state.unlockedCheatIds.toSet())
            .putStringSet(KEY_FAVORITE_CHEATS, state.favoriteCheatIds.toSet())
            .putStringSet(KEY_CHECKLIST, state.completedChecklistIds.toSet())
            .putStringSet(KEY_SEEN_FACTS, state.seenFunFactIds.toSet())
            .putInt(KEY_WAINK_COUNT, state.wainkScannedCount)
            .putInt(KEY_ANIMALS_COUNT, state.animalsStudiedCount)
            .putInt(KEY_STICKUPS_COUNT, state.stickUpsCount)
            .putInt(KEY_SHOOTING_GOLD_COUNT, state.shootingGoldCount)
            .putString(KEY_GITHUB_REPO, state.githubRepo)
            .apply()
    }

    private companion object {
        const val PREFS_NAME = "leonida_vi_companion_prefs"
        const val KEY_CURRENT_CHAPTER = "current_chapter"
        const val KEY_PLATFORM = "platform"
        const val KEY_FOUND_SECRETS = "found_secrets"
        const val KEY_UNLOCKED_CHEATS = "unlocked_cheats"
        const val KEY_FAVORITE_CHEATS = "favorite_cheats"
        const val KEY_CHECKLIST = "completed_checklist"
        const val KEY_SEEN_FACTS = "seen_facts"
        const val KEY_WAINK_COUNT = "waink_count"
        const val KEY_ANIMALS_COUNT = "animals_count"
        const val KEY_STICKUPS_COUNT = "stickups_count"
        const val KEY_SHOOTING_GOLD_COUNT = "shooting_gold_count"
        const val KEY_GITHUB_REPO = "github_repo"
    }
}
