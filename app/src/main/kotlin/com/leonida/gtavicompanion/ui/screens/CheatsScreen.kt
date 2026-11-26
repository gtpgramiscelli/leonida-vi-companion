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
import com.leonida.gtavicompanion.ui.theme.LeonidaTheme

@Composable
fun CheatsScreen(
    state: UserProgressState,
    viewModel: CompanionViewModel
) {
    val colors = LeonidaTheme.colors
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
            .background(colors.background),
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
                        VicePillBadge(
                            text = "SISTEMA DE 61 CHEATS",
                            color = Color(0xFFFFD166),
                            onHero = true
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Códigos de GTA VI",
                            style = MaterialTheme.typography.headlineSmall,
                            color = colors.heroTextPrimary
                        )
                    }
                    PlatformSwitcher(
                        selected = state.platform,
                        onSelect = viewModel::setPlatform,
                        onHero = true
                    )
                }

                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Como ativar no ${state.platform.label}:",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFF00E5FF)
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StepPill(
                        text = "1. D-Pad ↑ (Celular)",
                        color = Color(0xFF00E5FF),
                        bg = colors.heroOverlayChipBg,
                        modifier = Modifier.weight(1f)
                    )
                    StepPill(
                        text = "2. ${state.platform.holdButton} + ${state.platform.tapButton}",
                        color = Color(0xFFFF6EB4),
                        bg = colors.heroOverlayChipBg,
                        modifier = Modifier.weight(0.9f)
                    )
                    StepPill(
                        text = "3. Digite Setas",
                        color = Color(0xFFFFD166),
                        bg = colors.heroOverlayChipBg,
                        modifier = Modifier.weight(0.95f)
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Em GTA VI não há números discáveis como em GTA V. Os cheats liberam após terminar a história (91 missões) e cada um exige um desafio específico em Progresso > Cheats.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.heroTextSecondary
                )
            }
        }

        // 2. TROPHY / SAVE WARNING BANNER
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(colors.dangerSoft)
                    .border(1.dp, colors.danger.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                    .padding(13.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.WarningAmber,
                    contentDescription = null,
                    tint = colors.danger,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = "ATENÇÃO: Save e Troféus Desativados!",
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.danger
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "Ativar qualquer cheat bloqueia salvamentos e conquistas/troféus na sessão até reiniciar o jogo. Sempre faça um save manual antes!",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textPrimary
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
                placeholder = { Text("Buscar cheat, veículo ou desafio...", color = colors.textMuted) },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = colors.cyan) },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.cyan,
                    unfocusedBorderColor = colors.outline,
                    focusedContainerColor = colors.surface,
                    unfocusedContainerColor = colors.surface,
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary
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
                    accent = colors.cyan,
                    selected = selectedCategory == null && !onlyFavorites,
                    onClick = {
                        selectedCategory = null
                        onlyFavorites = false
                    }
                )
                CheatFilterChip(
                    label = "★ Favoritos (${state.favoriteCheatIds.size})",
                    accent = colors.gold,
                    selected = onlyFavorites,
                    onClick = { onlyFavorites = !onlyFavorites }
                )
                CheatCategory.entries.forEach { cat ->
                    CheatFilterChip(
                        label = cat.label,
                        accent = cat.accent(colors),
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
                        color = colors.textSecondary
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
private fun StepPill(
    text: String,
    color: Color,
    bg: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(11.dp))
            .background(bg)
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(11.dp))
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
    val colors = LeonidaTheme.colors
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) accent.copy(alpha = if (colors.isDark) 0.2f else 0.13f) else colors.card)
            .border(1.dp, if (selected) accent else colors.outline, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) accent else colors.textSecondary
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
    val colors = LeonidaTheme.colors
    val catColor = cheat.category.accent(colors)

    ViceCard(
        accentBorder = if (isUnlocked) colors.mint.copy(alpha = 0.55f) else catColor.copy(alpha = 0.35f),
        backgroundColor = colors.card
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
                VicePillBadge(text = cheat.category.label, color = catColor)
                if (isUnlocked) {
                    VicePillBadge(text = "DESBLOQUEADO", color = colors.mint)
                }
            }
            IconButton(onClick = onToggleFavorite, modifier = Modifier.size(34.dp)) {
                Icon(
                    imageVector = if (isFavorite) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                    contentDescription = "Favoritar cheat",
                    tint = if (isFavorite) colors.gold else colors.textMuted
                )
            }
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text = cheat.title,
            style = MaterialTheme.typography.titleMedium,
            color = colors.textPrimary,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = cheat.description,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary
        )

        Spacer(Modifier.height(12.dp))

        if (cheat.sequence != null) {
            DirectionSequenceRow(directions = cheat.sequence)
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.chipBg)
                    .border(1.dp, colors.outlineSoft, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 9.dp)
            ) {
                Text(
                    text = "Sequência exibida em Progresso > Cheats assim que você concluir o requisito abaixo.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(colors.chipBg)
                .border(1.dp, colors.outlineSoft, RoundedCornerShape(14.dp))
                .padding(11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.LockOpen,
                contentDescription = null,
                tint = colors.gold,
                modifier = Modifier.size(18.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Requisito de Desbloqueio",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.gold
                )
                Text(
                    text = cheat.unlockRequirement,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textPrimary
                )
                cheat.extraNote?.let { note ->
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = note,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted
                    )
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isUnlocked) colors.mintSoft else colors.card)
                    .border(
                        1.dp,
                        if (isUnlocked) colors.mint else colors.outline,
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
                        tint = if (isUnlocked) colors.mint else colors.textMuted,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = if (isUnlocked) "Feito" else "Marcar",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isUnlocked) colors.mint else colors.textSecondary
                    )
                }
            }
        }
    }
}
