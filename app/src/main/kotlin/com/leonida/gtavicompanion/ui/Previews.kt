package com.leonida.gtavicompanion.ui

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.leonida.gtavicompanion.data.ProgressRepository
import com.leonida.gtavicompanion.model.UserProgressState
import com.leonida.gtavicompanion.ui.screens.ChaptersScreen
import com.leonida.gtavicompanion.ui.screens.CheatsScreen
import com.leonida.gtavicompanion.ui.screens.ExtrasScreen
import com.leonida.gtavicompanion.ui.screens.HomeScreen
import com.leonida.gtavicompanion.ui.screens.MapScreen
import com.leonida.gtavicompanion.ui.theme.LeonidaGuideTheme

private class PreviewProgressRepository(
    private var state: UserProgressState = UserProgressState(
        currentChapter = 2,
        foundSecretIds = setOf("sec_michaels_boat", "sec_dexter_apt", "sec_mile_zero"),
        unlockedCheatIds = setOf("cheat_cash_250k"),
        favoriteCheatIds = setOf("cheat_cash_250k", "cheat_infinite_ammo"),
        completedChecklistIds = setOf("story_main", "disc_waink", "act_meditation", "story_stickups"),
        wainkScannedCount = 142,
        animalsStudiedCount = 64,
        stickUpsCount = 31,
        shootingGoldCount = 18
    )
) : ProgressRepository {
    override fun load(): UserProgressState = state
    override fun save(state: UserProgressState) {
        this.state = state
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915, uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Home — Claro")
@Preview(showBackground = true, widthDp = 412, heightDp = 915, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Home — Escuro")
@Composable
fun HomeScreenPreview() {
    val vm = CompanionViewModel(PreviewProgressRepository())
    LeonidaGuideTheme {
        HomeScreen(
            state = vm.state.value,
            viewModel = vm,
            onOpenMap = {},
            onOpenChapters = {},
            onOpenSync = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915, uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Mapa — Claro")
@Preview(showBackground = true, widthDp = 412, heightDp = 915, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Mapa — Escuro")
@Composable
fun MapScreenPreview() {
    val vm = CompanionViewModel(PreviewProgressRepository())
    LeonidaGuideTheme {
        MapScreen(state = vm.state.value, viewModel = vm)
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915, uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Cheats — Claro")
@Preview(showBackground = true, widthDp = 412, heightDp = 915, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Cheats — Escuro")
@Composable
fun CheatsScreenPreview() {
    val vm = CompanionViewModel(PreviewProgressRepository())
    LeonidaGuideTheme {
        CheatsScreen(state = vm.state.value, viewModel = vm)
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915, uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Capítulos — Claro")
@Preview(showBackground = true, widthDp = 412, heightDp = 915, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Capítulos — Escuro")
@Composable
fun ChaptersScreenPreview() {
    val vm = CompanionViewModel(PreviewProgressRepository())
    LeonidaGuideTheme {
        ChaptersScreen(state = vm.state.value, viewModel = vm)
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915, uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Extras — Claro")
@Preview(showBackground = true, widthDp = 412, heightDp = 915, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Extras — Escuro")
@Composable
fun ExtrasScreenPreview() {
    val vm = CompanionViewModel(PreviewProgressRepository())
    LeonidaGuideTheme {
        ExtrasScreen(state = vm.state.value, viewModel = vm)
    }
}
