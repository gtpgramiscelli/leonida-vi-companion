package com.leonida.gtavicompanion.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leonida.gtavicompanion.model.CheatDirection
import com.leonida.gtavicompanion.model.PlatformType
import com.leonida.gtavicompanion.ui.theme.LeonidaTheme

@Composable
fun ViceCard(
    modifier: Modifier = Modifier,
    accentBorder: Color = LeonidaTheme.colors.outlineSoft,
    backgroundColor: Color = LeonidaTheme.colors.card,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = LeonidaTheme.colors
    val shape = RoundedCornerShape(22.dp)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clip(shape).clickable(onClick = onClick) else Modifier),
        shape = shape,
        color = backgroundColor,
        shadowElevation = if (colors.isDark) 0.dp else 3.dp,
        border = BorderStroke(1.dp, accentBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun ViceHeroCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = LeonidaTheme.colors
    val shape = RoundedCornerShape(26.dp)
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        color = Color.Transparent,
        shadowElevation = if (colors.isDark) 4.dp else 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(colors.heroStart, colors.heroMid, colors.heroEnd)
                    )
                )
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x55FF8A3D),
                                Color(0x28FF2E93),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.92f, size.height * 0.12f),
                            radius = size.maxDimension * 0.65f
                        ),
                        radius = size.maxDimension * 0.65f,
                        center = Offset(size.width * 0.92f, size.height * 0.12f)
                    )
                }
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0x99FF2E93),
                            Color(0x66FF8A3D),
                            Color(0x8800E5FF)
                        )
                    ),
                    shape = shape
                )
                .padding(18.dp)
        ) {
            Column(content = content)
        }
    }
}

@Composable
fun VicePillBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    onHero: Boolean = false
) {
    val colors = LeonidaTheme.colors
    val bg = when {
        onHero -> colors.heroOverlayChipBg
        colors.isDark -> color.copy(alpha = 0.16f)
        else -> color.copy(alpha = 0.12f)
    }
    val borderCol = when {
        onHero -> color.copy(alpha = 0.72f)
        colors.isDark -> color.copy(alpha = 0.45f)
        else -> color.copy(alpha = 0.36f)
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .border(1.dp, borderCol, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
fun ViceSectionHeader(
    title: String,
    subtitle: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colors = LeonidaTheme.colors
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = colors.textPrimary
            )
            if (subtitle != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }
        }
        if (trailing != null) {
            Spacer(Modifier.width(12.dp))
            trailing()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DirectionSequenceRow(
    directions: List<CheatDirection>,
    modifier: Modifier = Modifier
) {
    val colors = LeonidaTheme.colors
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        directions.forEach { dir ->
            val accent = dir.accent(colors)
            val keyShape = RoundedCornerShape(11.dp)
            Box(
                modifier = Modifier
                    .then(
                        if (colors.isDark) Modifier else Modifier.shadow(1.5.dp, keyShape)
                    )
                    .clip(keyShape)
                    .background(colors.chipBg)
                    .border(
                        width = 1.2.dp,
                        color = accent.copy(alpha = if (colors.isDark) 0.65f else 0.55f),
                        shape = keyShape
                    )
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = dir.arrow,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = accent
                    )
                    Text(
                        text = dir.ptName.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textPrimary.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

@Composable
fun ChapterProgressSelector(
    currentChapter: Int,
    onSelectChapter: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onHero: Boolean = false
) {
    val colors = LeonidaTheme.colors
    val options = listOf(
        0 to "Prólogo",
        1 to "Cap. 1",
        2 to "Cap. 2",
        3 to "Cap. 3",
        4 to "Cap. 4",
        5 to "Cap. 5",
        6 to "Pós-Game"
    )
    val shieldTint = if (onHero) Color(0xFF3BF0B8) else colors.mint
    val labelTint = if (onHero) colors.heroTextSecondary else colors.textSecondary

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Shield,
                contentDescription = null,
                tint = shieldTint,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Escudo Anti-Spoiler — Onde você está na história:",
                style = MaterialTheme.typography.labelMedium,
                color = labelTint
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { (value, label) ->
                val selected = value == currentChapter
                val unselectedBg = if (onHero) colors.heroOverlayChipBg else colors.chipBg
                val unselectedBorder = if (onHero) Color.White.copy(alpha = 0.24f) else colors.outline
                val unselectedText = if (onHero) colors.heroTextSecondary else colors.textSecondary

                val bg = if (selected) {
                    Brush.horizontalGradient(listOf(Color(0xFFFF2E93), Color(0xFFFF8A3D)))
                } else {
                    Brush.horizontalGradient(listOf(unselectedBg, unselectedBg))
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(bg)
                        .border(
                            width = 1.dp,
                            color = if (selected) Color.White.copy(alpha = 0.35f) else unselectedBorder,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onSelectChapter(value) }
                        .padding(horizontal = 13.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (selected) Color.White else unselectedText
                    )
                }
            }
        }
    }
}

@Composable
fun PlatformSwitcher(
    selected: PlatformType,
    onSelect: (PlatformType) -> Unit,
    modifier: Modifier = Modifier,
    onHero: Boolean = false
) {
    val colors = LeonidaTheme.colors
    val containerBg = if (onHero) colors.heroOverlayChipBg else colors.chipBg
    val containerBorder = if (onHero) Color.White.copy(alpha = 0.24f) else colors.outline
    val activeText = if (onHero) Color(0xFF00E5FF) else colors.cyan
    val inactiveText = if (onHero) colors.heroTextSecondary else colors.textMuted
    val activeBg = if (onHero) Color.White.copy(alpha = 0.16f) else colors.card

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(containerBg)
            .border(1.dp, containerBorder, RoundedCornerShape(14.dp))
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        PlatformType.entries.forEach { platform ->
            val isSelected = platform == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(11.dp))
                    .background(if (isSelected) activeBg else Color.Transparent)
                    .border(
                        width = if (isSelected) 1.dp else 0.dp,
                        color = if (isSelected) activeText.copy(alpha = 0.7f) else Color.Transparent,
                        shape = RoundedCornerShape(11.dp)
                    )
                    .clickable { onSelect(platform) }
                    .padding(horizontal = 11.dp, vertical = 6.dp)
            ) {
                Text(
                    text = platform.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) activeText else inactiveText
                )
            }
        }
    }
}
