package com.leonida.gtavicompanion.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.GpsFixed
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material.icons.rounded.TipsAndUpdates
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leonida.gtavicompanion.data.GtaViCatalog
import com.leonida.gtavicompanion.model.ChecklistCategory
import com.leonida.gtavicompanion.model.ChecklistItem
import com.leonida.gtavicompanion.model.UserProgressState
import com.leonida.gtavicompanion.ui.CompanionViewModel
import com.leonida.gtavicompanion.ui.components.ChapterProgressSelector
import com.leonida.gtavicompanion.ui.components.ViceCard
import com.leonida.gtavicompanion.ui.components.ViceHeroCard
import com.leonida.gtavicompanion.ui.components.VicePillBadge
import com.leonida.gtavicompanion.ui.components.ViceSectionHeader
import com.leonida.gtavicompanion.ui.theme.ViceCardSurface
import com.leonida.gtavicompanion.ui.theme.ViceDeepSurface
import com.leonida.gtavicompanion.ui.theme.ViceElevatedSurface
import com.leonida.gtavicompanion.ui.theme.ViceMidnight
import com.leonida.gtavicompanion.ui.theme.ViceNeonPink
import com.leonida.gtavicompanion.ui.theme.ViceOceanCyan
import com.leonida.gtavicompanion.ui.theme.ViceOutline
import com.leonida.gtavicompanion.ui.theme.ViceOutlineSoft
import com.leonida.gtavicompanion.ui.theme.VicePalmMint
import com.leonida.gtavicompanion.ui.theme.ViceSunsetGold
import com.leonida.gtavicompanion.ui.theme.ViceSunsetOrange
import com.leonida.gtavicompanion.ui.theme.ViceTextMuted
import com.leonida.gtavicompanion.ui.theme.ViceTextPrimary
import com.leonida.gtavicompanion.ui.theme.ViceTextSecondary
import kotlin.math.roundToInt

private enum class ChecklistFilter(val label: String) {
    ALL("Todos"),
    PENDING("Pendentes"),
    DONE("Concluídos")
}

@Composable
fun HomeScreen(
    state: UserProgressState,
    viewModel: CompanionViewModel,
    onOpenMap: () -> Unit,
    onOpenChapters: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf(ChecklistFilter.ALL) }
    var expandedCategories by remember {
        mutableStateOf(setOf(ChecklistCategory.STORY_TASKS, ChecklistCategory.DISCOVERY))
    }

    val completionFraction = viewModel.completionFraction(state)
    val animatedProgress by animateFloatAsState(
        targetValue = completionFraction,
        animationSpec = tween(700),
        label = "completionProgress"
    )
    val percentText = "${(completionFraction * 100).roundToInt()}%"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ViceMidnight),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. HERO DASHBOARD
        item {
            ViceHeroCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        VicePillBadge(text = "GUIA OFICIAL 100% • LEONIDA", color = ViceOceanCyan)
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "Rumo à Platina\nem Vice City",
                            style = MaterialTheme.typography.headlineMedium,
                            color = ViceTextPrimary
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "${state.completedChecklistIds.size} de ${viewModel.totalChecklistCount} metas de \"A Long Road\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = ViceTextSecondary
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Box(
                        modifier = Modifier.size(92.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val stroke = 9.dp.toPx()
                            drawArc(
                                color = ViceOutline.copy(alpha = 0.45f),
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = stroke, cap = StrokeCap.Round)
                            )
                            drawArc(
                                brush = Brush.sweepGradient(
                                    listOf(ViceNeonPink, ViceSunsetOrange, ViceSunsetGold, ViceOceanCyan, ViceNeonPink)
                                ),
                                startAngle = -90f,
                                sweepAngle = (animatedProgress * 360f).coerceAtLeast(if (animatedProgress > 0f) 10f else 0f),
                                useCenter = false,
                                style = Stroke(width = stroke, cap = StrokeCap.Round)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = percentText,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = ViceTextPrimary
                            )
                            Text(
                                text = "100%",
                                style = MaterialTheme.typography.labelSmall,
                                color = ViceSunsetGold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Quick summary strip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MiniStatChip(
                        label = "Segredos",
                        value = "${state.foundSecretIds.size}/${viewModel.totalSecretsCount}",
                        color = ViceNeonPink,
                        modifier = Modifier.weight(1f),
                        onClick = onOpenMap
                    )
                    MiniStatChip(
                        label = "Cheats",
                        value = "${state.unlockedCheatIds.size}/${viewModel.totalCheatsTrackedCount}",
                        color = ViceSunsetGold,
                        modifier = Modifier.weight(1f),
                        onClick = null
                    )
                    MiniStatChip(
                        label = "História",
                        value = when (state.currentChapter) {
                            0 -> "Prólogo"
                            6 -> "Zerado"
                            else -> "Cap. ${state.currentChapter}"
                        },
                        color = VicePalmMint,
                        modifier = Modifier.weight(1f),
                        onClick = onOpenChapters
                    )
                }

                Spacer(Modifier.height(16.dp))

                ChapterProgressSelector(
                    currentChapter = state.currentChapter,
                    onSelectChapter = viewModel::setCurrentChapter
                )
            }
        }

        // 2. CONTADORES RÁPIDOS DE CAMPO
        item {
            ViceSectionHeader(
                title = "Contadores de Campo",
                subtitle = "Registre enquanto joga — zera a meta automaticamente no 100%"
            )
            Spacer(Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FieldCounterCard(
                        title = "App WAiNK",
                        subtitle = "Veículos únicos",
                        count = state.wainkScannedCount,
                        target = 200,
                        max = 277,
                        icon = Icons.Rounded.DirectionsCar,
                        accent = ViceOceanCyan,
                        onAdjust = viewModel::adjustWainkCount,
                        modifier = Modifier.weight(1f)
                    )
                    FieldCounterCard(
                        title = "Binóculos",
                        subtitle = "Espécies vistas",
                        count = state.animalsStudiedCount,
                        target = 100,
                        max = 255,
                        icon = Icons.Rounded.Pets,
                        accent = VicePalmMint,
                        onAdjust = viewModel::adjustAnimalsCount,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FieldCounterCard(
                        title = "Stick Ups",
                        subtitle = "Lojas assaltadas",
                        count = state.stickUpsCount,
                        target = 50,
                        max = 91,
                        icon = Icons.Rounded.Storefront,
                        accent = ViceSunsetOrange,
                        onAdjust = viewModel::adjustStickUpsCount,
                        modifier = Modifier.weight(1f)
                    )
                    FieldCounterCard(
                        title = "Estande Ouro",
                        subtitle = "Gun Club",
                        count = state.shootingGoldCount,
                        target = 42,
                        max = 42,
                        icon = Icons.Rounded.GpsFixed,
                        accent = ViceNeonPink,
                        onAdjust = viewModel::adjustShootingGoldCount,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 3. NO MISSABLES REASSURANCE TIP
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(VicePalmMint.copy(alpha = 0.11f))
                    .border(1.dp, VicePalmMint.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.TipsAndUpdates,
                    contentDescription = null,
                    tint = VicePalmMint,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = "Zero estresse: nada do 100% é perdível!",
                        style = MaterialTheme.typography.titleSmall,
                        color = VicePalmMint
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "O rastreador oficial aparece no jogo após zerar, mas tudo que você fizer agora já conta retroativamente. Evite ativar cheats no seu save principal!",
                        style = MaterialTheme.typography.bodySmall,
                        color = ViceTextSecondary
                    )
                }
            }
        }

        // 4. CHECKLIST OFICIAL DE 35 REQUISITOS
        item {
            ViceSectionHeader(
                title = "Checklist Oficial do 100%",
                subtitle = "35 requisitos oficiais do troféu \"A Long Road\"",
                trailing = {
                    VicePillBadge(
                        text = "${state.completedChecklistIds.size}/35",
                        color = ViceSunsetGold
                    )
                }
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChecklistFilter.entries.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter.label, style = MaterialTheme.typography.labelMedium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ViceNeonPink.copy(alpha = 0.22f),
                            selectedLabelColor = ViceNeonPink,
                            containerColor = ViceDeepSurface,
                            labelColor = ViceTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedFilter == filter,
                            borderColor = ViceOutline,
                            selectedBorderColor = ViceNeonPink
                        )
                    )
                }
            }
        }

        items(ChecklistCategory.entries, key = { it.name }) { category ->
            val allInCat = GtaViCatalog.completionChecklist.filter { it.category == category }
            val doneInCat = allInCat.count { it.id in state.completedChecklistIds }
            val filteredItems = allInCat.filter { item ->
                val done = item.id in state.completedChecklistIds
                when (selectedFilter) {
                    ChecklistFilter.ALL -> true
                    ChecklistFilter.PENDING -> !done
                    ChecklistFilter.DONE -> done
                }
            }
            val isExpanded = category in expandedCategories

            ViceCard(
                accentBorder = category.color.copy(alpha = 0.4f),
                backgroundColor = ViceCardSurface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            expandedCategories = if (isExpanded) {
                                expandedCategories - category
                            } else {
                                expandedCategories + category
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = category.label,
                                style = MaterialTheme.typography.titleMedium,
                                color = ViceTextPrimary
                            )
                            VicePillBadge(
                                text = "$doneInCat/${allInCat.size}",
                                color = category.color
                            )
                        }
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = category.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = ViceTextMuted
                        )
                    }
                    Icon(
                        imageVector = if (isExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = null,
                        tint = ViceTextSecondary
                    )
                }

                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { if (allInCat.isEmpty()) 0f else doneInCat.toFloat() / allInCat.size },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50)),
                    color = category.color,
                    trackColor = ViceMidnight
                )

                AnimatedVisibility(visible = isExpanded) {
                    Column(
                        modifier = Modifier.padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (filteredItems.isEmpty()) {
                            Text(
                                text = "Nenhum item neste filtro.",
                                style = MaterialTheme.typography.bodySmall,
                                color = ViceTextMuted,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        } else {
                            filteredItems.forEach { item ->
                                ChecklistRow(
                                    item = item,
                                    checked = item.id in state.completedChecklistIds,
                                    accent = category.color,
                                    onToggle = { viewModel.toggleChecklistItem(item.id) }
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
private fun MiniStatChip(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)?
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(ViceMidnight.copy(alpha = 0.72f))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 10.dp, vertical = 9.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = color,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = ViceTextSecondary
        )
    }
}

@Composable
private fun FieldCounterCard(
    title: String,
    subtitle: String,
    count: Int,
    target: Int,
    max: Int,
    icon: ImageVector,
    accent: Color,
    onAdjust: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val reachedGoal = count >= target
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(ViceCardSurface)
            .border(
                width = 1.dp,
                color = if (reachedGoal) VicePalmMint.copy(alpha = 0.7f) else ViceOutlineSoft,
                shape = RoundedCornerShape(18.dp)
            )
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (reachedGoal) VicePalmMint else accent,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = if (reachedGoal) "META OK!" else "$count / $target",
                style = MaterialTheme.typography.labelMedium,
                color = if (reachedGoal) VicePalmMint else accent
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = ViceTextPrimary
        )
        Text(
            text = "$subtitle (máx. $max)",
            style = MaterialTheme.typography.bodySmall,
            color = ViceTextMuted
        )
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { (count.toFloat() / target).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(50)),
            color = if (reachedGoal) VicePalmMint else accent,
            trackColor = ViceMidnight
        )
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CounterStepButton(
                onClick = { onAdjust(-1) },
                accent = ViceTextSecondary
            ) {
                Icon(Icons.Rounded.Remove, contentDescription = "Diminuir", modifier = Modifier.size(16.dp))
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(ViceElevatedSurface)
                    .clickable { onAdjust(5) }
                    .padding(horizontal = 9.dp, vertical = 5.dp)
            ) {
                Text("+5", style = MaterialTheme.typography.labelSmall, color = accent)
            }
            CounterStepButton(
                onClick = { onAdjust(1) },
                accent = accent
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Aumentar", modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun CounterStepButton(
    onClick: () -> Unit,
    accent: Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(accent.copy(alpha = 0.16f))
            .border(1.dp, accent.copy(alpha = 0.45f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.LocalContentColor.let {
            androidx.compose.runtime.CompositionLocalProvider(it provides accent) {
                content()
            }
        }
    }
}

@Composable
private fun ChecklistRow(
    item: ChecklistItem,
    checked: Boolean,
    accent: Color,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (checked) VicePalmMint.copy(alpha = 0.08f) else ViceDeepSurface)
            .border(
                1.dp,
                if (checked) VicePalmMint.copy(alpha = 0.35f) else ViceOutlineSoft,
                RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onToggle)
            .padding(12.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = if (checked) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (checked) VicePalmMint else accent.copy(alpha = 0.7f),
            modifier = Modifier
                .padding(top = 2.dp)
                .size(21.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                color = if (checked) ViceTextMuted else ViceTextPrimary,
                textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = item.requirement,
                style = MaterialTheme.typography.bodySmall,
                color = if (checked) ViceTextMuted else ViceTextSecondary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Dica: ${item.tip}",
                style = MaterialTheme.typography.bodySmall,
                color = accent.copy(alpha = 0.85f)
            )
        }
    }
}
