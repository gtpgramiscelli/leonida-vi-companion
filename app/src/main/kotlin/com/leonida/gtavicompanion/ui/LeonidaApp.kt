package com.leonida.gtavicompanion.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leonida.gtavicompanion.ui.components.UpdateAvailableBanner
import com.leonida.gtavicompanion.ui.components.UpdateBottomSheet
import com.leonida.gtavicompanion.ui.screens.ChaptersScreen
import com.leonida.gtavicompanion.ui.screens.CheatsScreen
import com.leonida.gtavicompanion.ui.screens.ExtrasScreen
import com.leonida.gtavicompanion.ui.screens.HomeScreen
import com.leonida.gtavicompanion.ui.screens.MapScreen
import com.leonida.gtavicompanion.ui.theme.ViceDeepSurface
import com.leonida.gtavicompanion.ui.theme.ViceMidnight
import com.leonida.gtavicompanion.ui.theme.ViceNeonPink
import com.leonida.gtavicompanion.ui.theme.ViceOceanCyan
import com.leonida.gtavicompanion.ui.theme.ViceOutlineSoft
import com.leonida.gtavicompanion.ui.theme.VicePalmMint
import com.leonida.gtavicompanion.ui.theme.ViceSunsetOrange
import com.leonida.gtavicompanion.ui.theme.ViceTextMuted
import com.leonida.gtavicompanion.ui.theme.ViceTextPrimary

enum class CompanionTab(
    val label: String,
    val icon: ImageVector
) {
    HOME("100%", Icons.Rounded.EmojiEvents),
    MAP("Mapa", Icons.Rounded.Map),
    CHEATS("Cheats", Icons.Rounded.SportsEsports),
    CHAPTERS("História", Icons.Rounded.AutoStories),
    EXTRAS("Extras", Icons.Rounded.AutoAwesome)
}

@Composable
fun LeonidaApp(viewModel: CompanionViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val updateState by viewModel.updateState.collectAsStateWithLifecycle()
    var currentTab by rememberSaveable { mutableStateOf(CompanionTab.HOME) }
    var showUpdateSheet by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.checkForUpdateOnStartupIfConfigured()
    }

    val availableTag = when (val u = updateState) {
        is UpdateUiState.UpdateAvailable -> u.release.tagName
        is UpdateUiState.ReadyToInstall -> u.release.tagName
        else -> null
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ViceMidnight,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Column {
                ViceTopHeader(
                    currentChapter = state.currentChapter,
                    hasUpdate = availableTag != null,
                    onOpenUpdateSheet = { showUpdateSheet = true }
                )
                AnimatedVisibility(visible = availableTag != null) {
                    if (availableTag != null) {
                        UpdateAvailableBanner(
                            versionTag = availableTag,
                            onClick = { showUpdateSheet = true }
                        )
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ViceDeepSurface)
                    .navigationBarsPadding()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color.Transparent,
                                    ViceNeonPink.copy(alpha = 0.55f),
                                    ViceOceanCyan.copy(alpha = 0.55f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                NavigationBar(
                    containerColor = ViceDeepSurface,
                    tonalElevation = 0.dp,
                    windowInsets = WindowInsets(0, 0, 0, 0)
                ) {
                    CompanionTab.entries.forEach { tab ->
                        val selected = tab == currentTab
                        NavigationBarItem(
                            selected = selected,
                            onClick = { currentTab = tab },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.label,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = tab.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ViceNeonPink,
                                selectedTextColor = ViceNeonPink,
                                indicatorColor = ViceNeonPink.copy(alpha = 0.16f),
                                unselectedIconColor = ViceTextMuted,
                                unselectedTextColor = ViceTextMuted
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = currentTab,
                animationSpec = tween(220),
                label = "tabCrossfade"
            ) { tab ->
                when (tab) {
                    CompanionTab.HOME -> HomeScreen(
                        state = state,
                        viewModel = viewModel,
                        onOpenMap = { currentTab = CompanionTab.MAP },
                        onOpenChapters = { currentTab = CompanionTab.CHAPTERS }
                    )
                    CompanionTab.MAP -> MapScreen(
                        state = state,
                        viewModel = viewModel
                    )
                    CompanionTab.CHEATS -> CheatsScreen(
                        state = state,
                        viewModel = viewModel
                    )
                    CompanionTab.CHAPTERS -> ChaptersScreen(
                        state = state,
                        viewModel = viewModel
                    )
                    CompanionTab.EXTRAS -> ExtrasScreen(
                        state = state,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    if (showUpdateSheet) {
        UpdateBottomSheet(
            currentRepo = state.githubRepo,
            currentVersion = viewModel.currentAppVersion,
            updateState = updateState,
            viewModel = viewModel,
            onDismiss = { showUpdateSheet = false }
        )
    }
}

@Composable
private fun ViceTopHeader(
    currentChapter: Int,
    hasUpdate: Boolean,
    onOpenUpdateSheet: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ViceDeepSurface)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(listOf(ViceNeonPink, ViceSunsetOrange))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "VI",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "LEONIDA COMPANION",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Black,
                    color = ViceTextPrimary
                )
                Text(
                    text = "Seu guia diário offline de GTA VI",
                    style = MaterialTheme.typography.bodySmall,
                    color = ViceTextMuted
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(ViceMidnight)
                    .border(1.dp, ViceOutlineSoft, RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = when (currentChapter) {
                        0 -> "PRÓLOGO"
                        6 -> "PÓS-GAME"
                        else -> "CAP. $currentChapter"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = ViceOceanCyan
                )
            }
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(ViceMidnight)
                    .border(
                        1.dp,
                        if (hasUpdate) VicePalmMint else ViceOutlineSoft,
                        CircleShape
                    )
                    .clickable(onClick = onOpenUpdateSheet),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.CloudDownload,
                    contentDescription = "Verificar atualizações",
                    tint = if (hasUpdate) VicePalmMint else ViceTextMuted,
                    modifier = Modifier.size(18.dp)
                )
                if (hasUpdate) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(5.dp)
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(ViceNeonPink)
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(ViceOutlineSoft)
        )
    }
}
