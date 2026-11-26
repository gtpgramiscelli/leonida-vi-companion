package com.leonida.gtavicompanion.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.leonida.gtavicompanion.model.LeonidaRegion
import com.leonida.gtavicompanion.model.UserProgressState
import com.leonida.gtavicompanion.ui.CompanionViewModel
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
import com.leonida.gtavicompanion.ui.theme.VicePurpleGlow
import com.leonida.gtavicompanion.ui.theme.ViceSunsetGold
import com.leonida.gtavicompanion.ui.theme.ViceSunsetOrange
import com.leonida.gtavicompanion.ui.theme.ViceTextMuted
import com.leonida.gtavicompanion.ui.theme.ViceTextPrimary
import com.leonida.gtavicompanion.ui.theme.ViceTextSecondary

private enum class ExtrasTab(val label: String) {
    FUN_FACTS("Fun Facts"),
    DAILY_TIPS("Dicas"),
    DOSSIER("Leonida")
}

@Composable
fun ExtrasScreen(
    state: UserProgressState,
    viewModel: CompanionViewModel
) {
    var selectedTab by remember { mutableStateOf(ExtrasTab.FUN_FACTS) }
    var spotlightIndex by remember { mutableIntStateOf(0) }

    val availableFacts = remember(state.currentChapter) {
        GtaViCatalog.funFacts.filter { it.minChapter <= state.currentChapter || state.currentChapter >= 6 }
    }
    val spotlightFact = availableFacts.getOrNull(spotlightIndex % maxOf(availableFacts.size, 1))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ViceMidnight)
    ) {
        // Segmented top tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(ViceDeepSurface)
                .border(1.dp, ViceOutline, RoundedCornerShape(16.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ExtrasTab.entries.forEach { tab ->
                val isSelected = tab == selectedTab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) ViceElevatedSurface else Color.Transparent)
                        .border(
                            width = if (isSelected) 1.dp else 0.dp,
                            color = if (isSelected) ViceNeonPink.copy(alpha = 0.7f) else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedTab = tab }
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isSelected) ViceTextPrimary else ViceTextMuted
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (selectedTab) {
                ExtrasTab.FUN_FACTS -> {
                    item {
                        ViceHeroCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                VicePillBadge(text = "CURIOSIDADE EM DESTAQUE", color = ViceSunsetGold)
                                Text(
                                    text = "${state.seenFunFactIds.size}/${GtaViCatalog.funFacts.size} vistos",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = VicePalmMint
                                )
                            }
                            Spacer(Modifier.height(10.dp))
                            if (spotlightFact != null) {
                                Text(
                                    text = spotlightFact.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = ViceTextPrimary
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = spotlightFact.summary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ViceTextSecondary
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Onde conferir: ${spotlightFact.whereToSpot}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ViceOceanCyan
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = { spotlightIndex = (spotlightIndex + 1) % maxOf(availableFacts.size, 1) },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ViceNeonPink,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(Icons.Rounded.Casino, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Sortear Outra Curiosidade", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }

                    items(GtaViCatalog.funFacts, key = { it.id }) { fact ->
                        val isSeen = fact.id in state.seenFunFactIds
                        val isLocked = fact.minChapter > state.currentChapter && state.currentChapter < 6

                        if (isLocked) {
                            ViceCard(accentBorder = ViceOutline, backgroundColor = ViceDeepSurface) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(Icons.Rounded.Lock, contentDescription = null, tint = ViceTextMuted)
                                    Column {
                                        Text(
                                            text = "Curiosidade do Capítulo ${fact.minChapter}+",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = ViceTextSecondary
                                        )
                                        Text(
                                            text = "Protegida pelo Escudo Anti-Spoiler até você chegar lá.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = ViceTextMuted
                                        )
                                    }
                                }
                            }
                        } else {
                            ViceCard(
                                accentBorder = if (isSeen) VicePalmMint.copy(alpha = 0.45f) else VicePurpleGlow.copy(alpha = 0.35f)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    VicePillBadge(text = fact.tag, color = VicePurpleGlow)
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(if (isSeen) VicePalmMint.copy(alpha = 0.18f) else ViceMidnight)
                                            .border(
                                                1.dp,
                                                if (isSeen) VicePalmMint else ViceOutline,
                                                RoundedCornerShape(50)
                                            )
                                            .clickable { viewModel.toggleFunFactSeen(fact.id) }
                                            .padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isSeen) Icons.Rounded.CheckCircle else Icons.Rounded.AutoAwesome,
                                            contentDescription = null,
                                            tint = if (isSeen) VicePalmMint else ViceTextMuted,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = if (isSeen) "Já vi no jogo!" else "Marcar visto",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSeen) VicePalmMint else ViceTextSecondary
                                        )
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = fact.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = ViceTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = fact.summary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ViceTextSecondary
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Onde encontrar: ${fact.whereToSpot}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ViceOceanCyan
                                )
                            }
                        }
                    }
                }

                ExtrasTab.DAILY_TIPS -> {
                    item {
                        ViceSectionHeader(
                            title = "Dicas para o Dia a Dia",
                            subtitle = "Hábitos que economizam dezenas de horas no caminho do 100%"
                        )
                    }
                    items(GtaViCatalog.dailyTips, key = { it.id }) { tip ->
                        val color = when (tip.badge) {
                            "Alerta Crítico" -> ViceNeonPink
                            "100% Sem Dor" -> VicePalmMint
                            "Combate" -> ViceSunsetOrange
                            else -> ViceOceanCyan
                        }
                        ViceCard(accentBorder = color.copy(alpha = 0.42f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Rounded.Lightbulb, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                                VicePillBadge(text = tip.badge, color = color)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = tip.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = ViceTextPrimary
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = tip.body,
                                style = MaterialTheme.typography.bodyMedium,
                                color = ViceTextSecondary
                            )
                        }
                    }
                }

                ExtrasTab.DOSSIER -> {
                    item {
                        ViceSectionHeader(
                            title = "Elenco Principal (Sem Spoilers)",
                            subtitle = "Personagens liberados conforme seu capítulo atual"
                        )
                    }
                    items(GtaViCatalog.characters, key = { it.id }) { char ->
                        val locked = char.minChapter > state.currentChapter && state.currentChapter < 6
                        if (locked) {
                            ViceCard(accentBorder = ViceOutline, backgroundColor = ViceDeepSurface) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(Icons.Rounded.Lock, contentDescription = null, tint = ViceTextMuted)
                                    Text(
                                        text = "Personagem apresentado no Capítulo ${char.minChapter} (protegido)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ViceTextMuted
                                    )
                                }
                            }
                        } else {
                            ViceCard(accentBorder = char.region.accent.copy(alpha = 0.45f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Rounded.Person, contentDescription = null, tint = char.region.accent)
                                        Column {
                                            Text(
                                                text = char.name,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = ViceTextPrimary
                                            )
                                            Text(
                                                text = char.role,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = char.region.accent
                                            )
                                        }
                                    }
                                    VicePillBadge(text = char.region.displayName, color = char.region.accent)
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = char.bio,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ViceTextSecondary
                                )
                            }
                        }
                    }

                    item {
                        Spacer(Modifier.height(6.dp))
                        ViceSectionHeader(
                            title = "As 6 Regiões de Leonida",
                            subtitle = "Guia geográfico dos 5 condados do estado"
                        )
                    }
                    items(LeonidaRegion.entries, key = { it.name }) { region ->
                        ViceCard(
                            accentBorder = region.accent.copy(alpha = 0.45f),
                            backgroundColor = ViceCardSurface
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Rounded.Public, contentDescription = null, tint = region.accent)
                                    Column {
                                        Text(
                                            text = region.displayName,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = ViceTextPrimary
                                        )
                                        Text(
                                            text = "${region.county} • ${region.cardinal}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = region.accent
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = region.vibe,
                                style = MaterialTheme.typography.bodyMedium,
                                color = ViceTextSecondary
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}
