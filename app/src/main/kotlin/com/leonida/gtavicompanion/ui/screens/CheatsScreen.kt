package com.leonida.gtavicompanion.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.leonida.gtavicompanion.data.GtaViCatalog
import com.leonida.gtavicompanion.model.CheatCategory
import com.leonida.gtavicompanion.model.CheatCode
import com.leonida.gtavicompanion.model.UserProgressState
import com.leonida.gtavicompanion.ui.CompanionViewModel
import com.leonida.gtavicompanion.ui.components.DirectionSequenceRow
import com.leonida.gtavicompanion.ui.components.PlatformSwitcher
import com.leonida.gtavicompanion.ui.components.ViceCard
import com.leonida.gtavicompanion.ui.components.ViceHeroCard
import com.leonida.gtavicompanion.ui.components.VicePillBadge
import com.leonida.gtavicompanion.ui.theme.ViceCardSurface
import com.leonida.gtavicompanion.ui.theme.ViceDangerRed
import com.leonida.gtavicompanion.ui.theme.ViceDangerSoft
import com.leonida.gtavicompanion.ui.theme.ViceDeepSurface
import com.leonida.gtavicompanion.ui.theme.ViceMidnight
import com.leonida.gtavicompanion.ui.theme.ViceNeonPink
import com.leonida.gtavicompanion.ui.theme.ViceOceanCyan
import com.leonida.gtavicompanion.ui.theme.ViceOutline
import com.leonida.gtavicompanion.ui.theme.ViceOutlineSoft
import com.leonida.gtavicompanion.ui.theme.VicePalmMint
import com.leonida.gtavicompanion.ui.theme.ViceSunsetGold
import com.leonida.gtavicompanion.ui.theme.ViceTextMuted
import com.leonida.gtavicompanion.ui.theme.ViceTextPrimary
import com.leonida.gtavicompanion.ui.theme.ViceTextSecondary

@Composable
fun CheatsScreen(
    state: UserProgressState,
    viewModel: CompanionViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<CheatCategory?>(null) }
    var onlyFavorites by remember { mutableStateOf(false) }

    val filteredCheats = remember(searchQuery, selectedCategory, onlyFavorites, state.favoriteCheatIds) {
        GtaViCatalog.cheats.filter { cheat ->
            val matchesQuery = searchQuery.isBlank() ||
                cheat.title.contains(searchQuery, ignoreCase = true) ||
                cheat.description.contains(searchQuery, ignoreCase = true) ||
                cheat.unlockRequirement.contains(searchQuery, ignoreCase = true)
            val matchesCat = selectedCategory == null || cheat.category == selectedCategory
            val matchesFav = !onlyFavorites || cheat.id in state.favoriteCheatIds
            matchesQuery && matchesCat && matchesFav
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ViceMidnight),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. HOW TO ENTER + PLATFORM SWITCHER
        item {
            ViceHeroCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        VicePillBadge(text = "SISTEMA DE 61 CHEATS", color = ViceSunsetGold)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Códigos de GTA VI",
                            style = MaterialTheme.typography.headlineSmall,
                            color = ViceTextPrimary
                        )
                    }
                    PlatformSwitcher(
                        selected = state.platform,
                        onSelect = viewModel::setPlatform
                    )
                }

                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Como ativar no ${state.platform.label}:",
                    style = MaterialTheme.typography.titleSmall,
                    color = ViceOceanCyan
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StepPill("1. D-Pad ↑ (Celular)", ViceOceanCyan, Modifier.weight(1f))
                    StepPill("2. ${state.platform.holdButton} + ${state.platform.tapButton}", ViceNeonPink, Modifier.weight(0.9f))
                    StepPill("3. Digite Setas", ViceSunsetGold, Modifier.weight(0.95f))
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Em GTA VI não há números discáveis como em GTA V. Os cheats liberam após terminar a história (91 missões) e cada um exige um desafio específico em Progresso > Cheats.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ViceTextSecondary
                )
            }
        }

        // 2. TROPHY / SAVE WARNING BANNER
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ViceDangerSoft)
                    .border(1.dp, ViceDangerRed.copy(alpha = 0.55f), RoundedCornerShape(16.dp))
                    .padding(13.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.WarningAmber,
                    contentDescription = null,
                    tint = ViceDangerRed,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = "ATENÇÃO: Save e Troféus Desativados!",
                        style = MaterialTheme.typography.titleSmall,
                        color = ViceDangerRed
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "Ativar qualquer cheat bloqueia salvamentos e conquistas/troféus na sessão até reiniciar o jogo. Sempre faça um save manual antes!",
                        style = MaterialTheme.typography.bodySmall,
                        color = ViceTextPrimary.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // 3. SEARCH + FILTERS
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar cheat, veículo ou desafio...", color = ViceTextMuted) },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = ViceOceanCyan) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ViceOceanCyan,
                    unfocusedBorderColor = ViceOutline,
                    focusedContainerColor = ViceDeepSurface,
                    unfocusedContainerColor = ViceDeepSurface,
                    focusedTextColor = ViceTextPrimary,
                    unfocusedTextColor = ViceTextPrimary
                )
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CheatFilterChip(
                    label = "Todos (${GtaViCatalog.cheats.size})",
                    accent = ViceOceanCyan,
                    selected = selectedCategory == null && !onlyFavorites,
                    onClick = {
                        selectedCategory = null
                        onlyFavorites = false
                    }
                )
                CheatFilterChip(
                    label = "★ Favoritos (${state.favoriteCheatIds.size})",
                    accent = ViceSunsetGold,
                    selected = onlyFavorites,
                    onClick = { onlyFavorites = !onlyFavorites }
                )
                CheatCategory.entries.forEach { cat ->
                    CheatFilterChip(
                        label = cat.label,
                        accent = cat.color,
                        selected = selectedCategory == cat,
                        onClick = {
                            selectedCategory = if (selectedCategory == cat) null else cat
                        }
                    )
                }
            }
        }

        // 4. CHEAT CARDS
        if (filteredCheats.isEmpty()) {
            item {
                ViceCard {
                    Text(
                        text = "Nenhum cheat encontrado para este filtro.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ViceTextSecondary
                    )
                }
            }
        } else {
            items(filteredCheats, key = { it.id }) { cheat ->
                CheatCodeCard(
                    cheat = cheat,
                    isUnlocked = cheat.id in state.unlockedCheatIds,
                    isFavorite = cheat.id in state.favoriteCheatIds,
                    onToggleUnlocked = { viewModel.toggleCheatUnlocked(cheat.id) },
                    onToggleFavorite = { viewModel.toggleCheatFavorite(cheat.id) }
                )
            }
        }

        item { Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
private fun StepPill(text: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(11.dp))
            .background(color.copy(alpha = 0.14f))
            .border(1.dp, color.copy(alpha = 0.45f), RoundedCornerShape(11.dp))
            .padding(horizontal = 8.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            maxLines = 1
        )
    }
}

@Composable
private fun CheatFilterChip(
    label: String,
    accent: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) accent.copy(alpha = 0.2f) else ViceDeepSurface)
            .border(1.dp, if (selected) accent else ViceOutline, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) accent else ViceTextSecondary
        )
    }
}

@Composable
private fun CheatCodeCard(
    cheat: CheatCode,
    isUnlocked: Boolean,
    isFavorite: Boolean,
    onToggleUnlocked: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    ViceCard(
        accentBorder = if (isUnlocked) VicePalmMint.copy(alpha = 0.5f) else cheat.category.color.copy(alpha = 0.35f),
        backgroundColor = ViceCardSurface
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                VicePillBadge(text = cheat.category.label, color = cheat.category.color)
                if (isUnlocked) {
                    VicePillBadge(text = "DESBLOQUEADO", color = VicePalmMint)
                }
            }
            IconButton(onClick = onToggleFavorite, modifier = Modifier.size(34.dp)) {
                Icon(
                    imageVector = if (isFavorite) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                    contentDescription = "Favoritar cheat",
                    tint = if (isFavorite) ViceSunsetGold else ViceTextMuted
                )
            }
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text = cheat.title,
            style = MaterialTheme.typography.titleMedium,
            color = ViceTextPrimary,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = cheat.description,
            style = MaterialTheme.typography.bodySmall,
            color = ViceTextSecondary
        )

        Spacer(Modifier.height(12.dp))

        if (cheat.sequence != null) {
            DirectionSequenceRow(directions = cheat.sequence)
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ViceMidnight)
                    .border(1.dp, ViceOutlineSoft, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 9.dp)
            ) {
                Text(
                    text = "Sequência exibida em Progresso > Cheats assim que você concluir o requisito abaixo.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ViceTextMuted
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Unlock challenge box + toggle button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(ViceDeepSurface)
                .border(1.dp, ViceOutlineSoft, RoundedCornerShape(14.dp))
                .padding(11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.LockOpen,
                contentDescription = null,
                tint = ViceSunsetGold,
                modifier = Modifier.size(18.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Requisito de Desbloqueio",
                    style = MaterialTheme.typography.labelSmall,
                    color = ViceSunsetGold
                )
                Text(
                    text = cheat.unlockRequirement,
                    style = MaterialTheme.typography.bodySmall,
                    color = ViceTextPrimary
                )
                cheat.extraNote?.let { note ->
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = note,
                        style = MaterialTheme.typography.bodySmall,
                        color = ViceTextMuted
                    )
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isUnlocked) VicePalmMint.copy(alpha = 0.2f) else ViceMidnight)
                    .border(
                        1.dp,
                        if (isUnlocked) VicePalmMint else ViceOutline,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable(onClick = onToggleUnlocked)
                    .padding(horizontal = 10.dp, vertical = 7.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = if (isUnlocked) VicePalmMint else ViceTextMuted,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = if (isUnlocked) "Feito" else "Marcar",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isUnlocked) VicePalmMint else ViceTextSecondary
                    )
                }
            }
        }
    }
}
