package com.leonida.gtavicompanion.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.leonida.gtavicompanion.data.ProgressRepository
import com.leonida.gtavicompanion.model.UserProgressState
import com.leonida.gtavicompanion.ui.theme.LeonidaGuideTheme
import org.junit.Rule
import org.junit.Test

class LeonidaAppTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun navigatesAcrossCoreTabsAndProtectsFutureChapterSpoilers() {
        val repo = object : ProgressRepository {
            private var s = UserProgressState(currentChapter = 1)
            override fun load() = s
            override fun save(state: UserProgressState) { s = state }
        }
        val vm = CompanionViewModel(repo)

        composeRule.setContent {
            LeonidaGuideTheme {
                LeonidaApp(viewModel = vm)
            }
        }

        composeRule.onNodeWithText("LEONIDA COMPANION").assertIsDisplayed()
        composeRule.onNodeWithText("História").performClick()
        composeRule.onNodeWithText("Capítulo 2 — Protegido").assertIsDisplayed()
        composeRule.onNodeWithText("Cheats").performClick()
        composeRule.onNodeWithText("Códigos de GTA VI").assertIsDisplayed()
        composeRule.onNodeWithText("Mapa").performClick()
        composeRule.onNodeWithText("Mapa de Leonida").assertIsDisplayed()
    }
}
