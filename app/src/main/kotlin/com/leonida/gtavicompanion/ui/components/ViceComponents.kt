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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leonida.gtavicompanion.model.CheatDirection
import com.leonida.gtavicompanion.model.PlatformType
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

@Composable
fun ViceCard(
    modifier: Modifier = Modifier,
    accentBorder: Color = ViceOutlineSoft,
    backgroundColor: Color = ViceCardSurface,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clip(shape).clickable(onClick = onClick) else Modifier),
        shape = shape,
        color = backgroundColor,
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
    val shape = RoundedCornerShape(24.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF2B1046),
                        Color(0xFF1A1136),
                        Color(0xFF0E1F38)
                    )
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        ViceNeonPink.copy(alpha = 0.7f),
                        ViceSunsetOrange.copy(alpha = 0.45f),
                        ViceOceanCyan.copy(alpha = 0.7f)
                    )
                ),
                shape = shape
            )
            .padding(18.dp)
    ) {
        Column(content = content)
    }
}

@Composable
fun VicePillBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.16f))
            .border(1.dp, color.copy(alpha = 0.48f), RoundedCornerShape(50))
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
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = ViceTextPrimary
            )
            if (subtitle != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = ViceTextSecondary
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
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        directions.forEach { dir ->
            val accent = when (dir) {
                CheatDirection.UP -> ViceOceanCyan
                CheatDirection.DOWN -> ViceNeonPink
                CheatDirection.LEFT -> ViceSunsetGold
                CheatDirection.RIGHT -> VicePalmMint
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(ViceMidnight)
                    .border(1.2.dp, accent.copy(alpha = 0.65f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 9.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
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
                        color = ViceTextPrimary.copy(alpha = 0.85f)
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
    modifier: Modifier = Modifier
) {
    val options = listOf(
        0 to "Prólogo",
        1 to "Cap. 1",
        2 to "Cap. 2",
        3 to "Cap. 3",
        4 to "Cap. 4",
        5 to "Cap. 5",
        6 to "Pós-Game"
    )
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Shield,
                contentDescription = null,
                tint = VicePalmMint,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Escudo Anti-Spoiler — Onde você está na história:",
                style = MaterialTheme.typography.labelMedium,
                color = ViceTextSecondary
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
                val bg = if (selected) {
                    Brush.horizontalGradient(listOf(ViceNeonPink, ViceSunsetOrange))
                } else {
                    Brush.horizontalGradient(listOf(ViceDeepSurface, ViceDeepSurface))
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(bg)
                        .border(
                            width = 1.dp,
                            color = if (selected) Color.Transparent else ViceOutline,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onSelectChapter(value) }
                        .padding(horizontal = 13.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (selected) Color.White else ViceTextSecondary
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
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(ViceMidnight)
            .border(1.dp, ViceOutline, RoundedCornerShape(14.dp))
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        PlatformType.entries.forEach { platform ->
            val isSelected = platform == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(11.dp))
                    .background(if (isSelected) ViceElevatedSurface else Color.Transparent)
                    .border(
                        width = if (isSelected) 1.dp else 0.dp,
                        color = if (isSelected) ViceOceanCyan.copy(alpha = 0.7f) else Color.Transparent,
                        shape = RoundedCornerShape(11.dp)
                    )
                    .clickable { onSelect(platform) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = platform.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) ViceOceanCyan else ViceTextMuted
                )
            }
        }
    }
}

@Composable
fun NeonDot(color: Color, sizeDp: Int = 8) {
    Box(
        modifier = Modifier
            .size(sizeDp.dp)
            .clip(CircleShape)
            .background(color)
    )
}
