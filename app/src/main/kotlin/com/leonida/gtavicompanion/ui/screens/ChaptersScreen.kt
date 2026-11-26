package com.leonida.gtavicompanion.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.TipsAndUpdates
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.sp
import com.leonida.gtavicompanion.data.GtaViCatalog
import com.leonida.gtavicompanion.model.StoryChapter
import com.leonida.gtavicompanion.model.UserProgressState
import com.leonida.gtavicompanion.ui.CompanionViewModel
import com.leonida.gtavicompanion.ui.components.ChapterProgressSelector
import com.leonida.gtavicompanion.ui.components.ViceCard
import com.leonida.gtavicompanion.ui.components.ViceHeroCard
import com.leonida.gtavicompanion.ui.components.VicePillBadge
import com.leonida.gtavicompanion.ui.theme.LeonidaTheme

@Composable
fun ChaptersScreen(
    state: UserProgressState,
    viewModel: CompanionViewModel
) {
    val colors = LeonidaTheme.colors
    var manuallyPeekedChapters by remember { mutableStateOf(emptySet<Int>()) }
    var pendingPeekChapter by remember { mutableStateOf<Int?>(null) }
    var showStorySoFar by remember { mutableStateOf(false) }

    val completedChapters = remember(state.currentChapter) {
        GtaViCatalog.chapters.filter { it.number < state.currentChapter }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. ANTI-SPOILER HERO HEADER
        item {
            ViceHeroCard {
                VicePillBadge(
                    text = "ESCUDO ANTI-SPOILER ATIVO",
                    color = Color(0xFF3BF0B8),
                    onHero = true
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Resumo dos Capítulos",
                    style = MaterialTheme.typography.headlineSmall,
                    color = colors.heroTextPrimary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Relembre a trama sem tomar spoilers! Capítulos adiante ficam censurados e o capítulo atual mostra apenas a premissa inicial + dicas.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.heroTextSecondary
                )
                Spacer(Modifier.height(14.dp))
                ChapterProgressSelector(
                    currentChapter = state.currentChapter,
                    onSelectChapter = { next ->
                        viewModel.setCurrentChapter(next)
                        manuallyPeekedChapters = manuallyPeekedChapters.filter { it > next }.toSet()
                    },
                    onHero = true
                )
            }
        }

        // 2. "A HISTÓRIA ATÉ AQUI" CUMULATIVE RECAP BUTTON
        if (completedChapters.isNotEmpty()) {
            item {
                ViceCard(
                    accentBorder = colors.cyan.copy(alpha = 0.5f),
                    backgroundColor = colors.card,
                    onClick = { showStorySoFar = !showStorySoFar }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoStories,
                                contentDescription = null,
                                tint = colors.cyan,
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text(
                                    text = "Recapitulando: A História Até Aqui",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = colors.cyan
                                )
                                Text(
                                    text = "Voltou depois de dias sem jogar? Resumo seguro dos ${completedChapters.size} cap. concluídos.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.textSecondary
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = null,
                            tint = colors.cyan
                        )
                    }

                    AnimatedVisibility(visible = showStorySoFar) {
                        Column(
                            modifier = Modifier.padding(top = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            completedChapters.forEach { chap ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(colors.chipBg)
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = "Capítulo ${chap.number} • ${chap.title}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = chap.region.accent(colors)
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = chap.completedRecap,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = colors.textPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. CHAPTER TIMELINE CARDS
        items(GtaViCatalog.chapters, key = { it.number }) { chapter ->
            val isCompleted = chapter.number < state.currentChapter
            val isCurrent = chapter.number == state.currentChapter
            val isLockedFuture = chapter.number > state.currentChapter && chapter.number !in manuallyPeekedChapters

            if (isLockedFuture) {
                LockedChapterCard(
                    chapterNumber = chapter.number,
                    onRequestPeek = { pendingPeekChapter = chapter.number }
                )
            } else {
                UnlockedChapterCard(
                    chapter = chapter,
                    isCompleted = isCompleted,
                    isCurrent = isCurrent,
                    isPeeked = chapter.number in manuallyPeekedChapters && chapter.number > state.currentChapter,
                    onAdvanceToNext = {
                        viewModel.setCurrentChapter((chapter.number + 1).coerceAtMost(6))
                    }
                )
            }
        }

        item { Spacer(Modifier.height(12.dp)) }
    }

    pendingPeekChapter?.let { chapNum ->
        AlertDialog(
            onDismissRequest = { pendingPeekChapter = null },
            containerColor = colors.surface,
            titleContentColor = colors.textPrimary,
            textContentColor = colors.textSecondary,
            title = { Text("Desbloquear Capítulo $chapNum?") },
            text = {
                Text(
                    "Você marcou estar no Capítulo ${state.currentChapter}. Revelar o Capítulo $chapNum exibirá o título, região e premissa inicial dele (ainda sem estragar o final). Deseja espiar?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        manuallyPeekedChapters = manuallyPeekedChapters + chapNum
                        pendingPeekChapter = null
                    }
                ) {
                    Text("Sim, revelar premissa", color = colors.gold, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingPeekChapter = null }) {
                    Text("Manter protegido", color = colors.mint)
                }
            }
        )
    }
}

@Composable
private fun LockedChapterCard(
    chapterNumber: Int,
    onRequestPeek: () -> Unit
) {
    val colors = LeonidaTheme.colors
    ViceCard(
        accentBorder = colors.outline,
        backgroundColor = colors.card
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.chipBg)
                    .border(1.dp, colors.outline, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = "Bloqueado contra spoilers",
                    tint = colors.textMuted,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Capítulo $chapterNumber — Protegido",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textSecondary
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Título, região e eventos ocultos para não dar spoilers da sua jogatina.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(colors.chipBg)
                .border(1.dp, colors.outlineSoft, RoundedCornerShape(10.dp))
                .clickable(onClick = onRequestPeek)
                .padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Visibility,
                contentDescription = null,
                tint = colors.textMuted,
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = "Espiar premissa mesmo assim",
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun UnlockedChapterCard(
    chapter: StoryChapter,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isPeeked: Boolean,
    onAdvanceToNext: () -> Unit
) {
    val colors = LeonidaTheme.colors
    val accent = when {
        isCompleted -> colors.mint
        isCurrent -> colors.pink
        else -> colors.gold
    }
    val regionColor = chapter.region.accent(colors)

    ViceCard(
        accentBorder = accent.copy(alpha = 0.52f),
        backgroundColor = colors.card
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Editorial Chapter Number Medallion
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accent.copy(alpha = if (colors.isDark) 0.16f else 0.12f))
                    .border(1.dp, accent.copy(alpha = 0.55f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "0${chapter.number}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = accent
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    VicePillBadge(
                        text = when {
                            isCompleted -> "CONCLUÍDO"
                            isCurrent -> "JOGANDO AGORA"
                            else -> "ESPIANDO"
                        },
                        color = accent
                    )
                    VicePillBadge(
                        text = chapter.region.displayName,
                        color = regionColor
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "~${chapter.approxMissions} missões principais",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted
                )
            }
        }

        Spacer(Modifier.height(10.dp))
        Text(
            text = chapter.title,
            style = MaterialTheme.typography.titleLarge,
            color = colors.textPrimary
        )
        Text(
            text = chapter.subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = regionColor,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(colors.chipBg)
                .border(1.dp, colors.outlineSoft, RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = if (isCompleted) {
                        "Resumo do Capítulo (Sem Spoilers Futuros)"
                    } else {
                        "Premissa Inicial (Final do Capítulo Protegido)"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = accent
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = if (isCompleted) chapter.completedRecap else chapter.safeEntryPremise,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textPrimary
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Personagens em Destaque",
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted
        )
        Spacer(Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            chapter.keyCharacters.forEach { name ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(colors.chipBg)
                        .border(1.dp, colors.outline, RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(name, style = MaterialTheme.typography.labelSmall, color = colors.textPrimary)
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(colors.cyanSoft)
                .border(1.dp, colors.cyan.copy(alpha = 0.28f), RoundedCornerShape(14.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.TipsAndUpdates,
                    contentDescription = null,
                    tint = colors.cyan,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Dicas para Aproveitar o Cap. ${chapter.number}",
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.cyan
                )
            }
            chapter.gameplayTips.forEach { tip ->
                Text(
                    text = "• $tip",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }
        }

        if (isCurrent) {
            Spacer(Modifier.height(14.dp))
            Button(
                onClick = onAdvanceToNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.pink,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = if (chapter.number < 5) Icons.Rounded.PlayArrow else Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = if (chapter.number < 5) {
                        "Terminei o Cap. ${chapter.number} → Liberar Cap. ${chapter.number + 1}"
                    } else {
                        "Zerei a História! → Ativar Modo Pós-Game"
                    },
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        if (isPeeked) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Modo espiada: apenas a premissa segura foi liberada.",
                style = MaterialTheme.typography.labelSmall,
                color = colors.orange
            )
        }
    }
}
