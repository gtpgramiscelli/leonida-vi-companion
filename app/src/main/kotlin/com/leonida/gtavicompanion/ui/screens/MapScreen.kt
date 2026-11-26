package com.leonida.gtavicompanion.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.LockClock
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.leonida.gtavicompanion.data.GtaViCatalog
import com.leonida.gtavicompanion.model.LeonidaRegion
import com.leonida.gtavicompanion.model.SecretCategory
import com.leonida.gtavicompanion.model.SecretLocation
import com.leonida.gtavicompanion.model.UserProgressState
import com.leonida.gtavicompanion.ui.CompanionViewModel
import com.leonida.gtavicompanion.ui.components.VicePillBadge
import com.leonida.gtavicompanion.ui.theme.LeonidaColors
import com.leonida.gtavicompanion.ui.theme.LeonidaTheme
import kotlin.math.hypot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    state: UserProgressState,
    viewModel: CompanionViewModel
) {
    val colors = LeonidaTheme.colors
    var selectedRegion by remember { mutableStateOf<LeonidaRegion?>(null) }
    var selectedCategory by remember { mutableStateOf<SecretCategory?>(null) }
    var focusedSecret by remember { mutableStateOf<SecretLocation?>(GtaViCatalog.secretLocations.first()) }
    var sheetSecret by remember { mutableStateOf<SecretLocation?>(null) }

    var scale by remember { mutableFloatStateOf(1f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    val filteredSecrets = remember(selectedRegion, selectedCategory) {
        GtaViCatalog.secretLocations.filter { loc ->
            (selectedRegion == null || loc.region == selectedRegion) &&
                (selectedCategory == null || loc.category == selectedCategory)
        }
    }

    val pulseTransition = rememberInfiniteTransition(label = "pinPulse")
    val pulseFactor by pulseTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseFactor"
    )

    fun focusOnSecret(loc: SecretLocation) {
        focusedSecret = loc
        if (canvasSize.width > 0 && canvasSize.height > 0) {
            val targetScale = maxOf(scale, 1.85f)
            scale = targetScale
            val cx = canvasSize.width / 2f
            val cy = canvasSize.height / 2f
            val pinX = loc.mapX * canvasSize.width
            val pinY = loc.mapY * canvasSize.height
            val rawX = (cx - pinX) * targetScale
            val rawY = (cy - pinY) * targetScale
            val maxPanX = (canvasSize.width * (targetScale - 1f)) / 2f + 120f
            val maxPanY = (canvasSize.height * (targetScale - 1f)) / 2f + 120f
            panOffset = Offset(
                x = rawX.coerceIn(-maxPanX, maxPanX),
                y = rawY.coerceIn(-maxPanY, maxPanY)
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Mapa de Leonida",
                    style = MaterialTheme.typography.headlineSmall,
                    color = colors.textPrimary
                )
                Text(
                    text = "Pinça para zoom • Toque nos pinos para ver segredos",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }
            VicePillBadge(
                text = "${state.foundSecretIds.size}/${GtaViCatalog.secretLocations.size} achados",
                color = colors.mint
            )
        }

        // Region filter pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RegionFilterPill(
                label = "Toda Leonida",
                accent = colors.cyan,
                selected = selectedRegion == null,
                onClick = {
                    selectedRegion = null
                    scale = 1f
                    panOffset = Offset.Zero
                }
            )
            LeonidaRegion.entries.forEach { region ->
                RegionFilterPill(
                    label = region.displayName,
                    accent = region.accent(colors),
                    selected = selectedRegion == region,
                    onClick = {
                        selectedRegion = if (selectedRegion == region) null else region
                    }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Category filter pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CategorySmallPill(
                label = "Todas categorias",
                accent = colors.textPrimary,
                selected = selectedCategory == null,
                onClick = { selectedCategory = null }
            )
            SecretCategory.entries.forEach { cat ->
                CategorySmallPill(
                    label = cat.label,
                    accent = cat.accent(colors),
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = if (selectedCategory == cat) null else cat }
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // ADAPTIVE INTERACTIVE VECTOR MAP CANVAS
        val mapShape = RoundedCornerShape(24.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 14.dp)
                .then(if (colors.isDark) Modifier else Modifier.shadow(4.dp, mapShape))
                .clip(mapShape)
                .background(colors.mapOceanTop)
                .border(1.2.dp, colors.cyan.copy(alpha = 0.42f), mapShape)
                .onSizeChanged { canvasSize = it }
                .pointerInput(filteredSecrets) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        val nextScale = (scale * zoom).coerceIn(1f, 3.2f)
                        scale = nextScale
                        val maxX = (canvasSize.width * (nextScale - 1f)) / 2f + 120f
                        val maxY = (canvasSize.height * (nextScale - 1f)) / 2f + 120f
                        panOffset = Offset(
                            x = (panOffset.x + pan.x).coerceIn(-maxX, maxX),
                            y = (panOffset.y + pan.y).coerceIn(-maxY, maxY)
                        )
                    }
                }
                .pointerInput(filteredSecrets, scale, panOffset, canvasSize) {
                    detectTapGestures(
                        onDoubleTap = {
                            if (scale > 1.2f) {
                                scale = 1f
                                panOffset = Offset.Zero
                            } else {
                                scale = 2.1f
                            }
                        },
                        onTap = { tapPos ->
                            if (canvasSize.width == 0 || canvasSize.height == 0) return@detectTapGestures
                            val cx = canvasSize.width / 2f
                            val cy = canvasSize.height / 2f
                            var bestMatch: SecretLocation? = null
                            var bestDist = 72f
                            filteredSecrets.forEach { loc ->
                                val screenX = cx + (loc.mapX * canvasSize.width - cx) * scale + panOffset.x
                                val screenY = cy + (loc.mapY * canvasSize.height - cy) * scale + panOffset.y
                                val d = hypot(tapPos.x - screenX, tapPos.y - screenY)
                                if (d < bestDist) {
                                    bestDist = d
                                    bestMatch = loc
                                }
                            }
                            bestMatch?.let { tapped ->
                                focusedSecret = tapped
                                sheetSecret = tapped
                            }
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val cx = w / 2f
                val cy = h / 2f

                fun toScreen(nx: Float, ny: Float): Offset {
                    return Offset(
                        x = cx + (nx * w - cx) * scale + panOffset.x,
                        y = cy + (ny * h - cy) * scale + panOffset.y
                    )
                }

                // 1. Ocean Gradient & Radar/Cartographic Grid
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(colors.mapOceanTop, colors.mapOceanMid, colors.mapOceanBottom)
                    )
                )
                val gridLines = 10
                for (i in 1 until gridLines) {
                    val gx = toScreen(i / gridLines.toFloat(), 0f).x
                    val gy = toScreen(0f, i / gridLines.toFloat()).y
                    drawLine(
                        color = colors.mapGrid,
                        start = Offset(gx, 0f),
                        end = Offset(gx, h),
                        strokeWidth = 1f
                    )
                    drawLine(
                        color = colors.mapGrid,
                        start = Offset(0f, gy),
                        end = Offset(w, gy),
                        strokeWidth = 1f
                    )
                }

                // 2. Main Leonida Peninsula Silhouette
                drawLeonidaTerrain(
                    toScreen = ::toScreen,
                    selectedRegion = selectedRegion,
                    scale = scale,
                    colors = colors
                )

                // 3. Draw Secret Location Pins
                filteredSecrets.forEach { loc ->
                    val pos = toScreen(loc.mapX, loc.mapY)
                    val isFound = loc.id in state.foundSecretIds
                    val isFocused = focusedSecret?.id == loc.id
                    val pinColor = if (isFound) colors.mint else loc.category.accent(colors)

                    if (isFocused) {
                        val ringRadius = (16.dp.toPx() + pulseFactor * 20.dp.toPx())
                        drawCircle(
                            color = pinColor.copy(alpha = (1f - pulseFactor) * 0.7f),
                            radius = ringRadius,
                            center = pos,
                            style = Stroke(width = 2.2.dp.toPx())
                        )
                    }

                    drawCircle(
                        color = pinColor.copy(alpha = if (isFocused) 0.36f else 0.22f),
                        radius = if (isFocused) 15.dp.toPx() else 11.dp.toPx(),
                        center = pos
                    )
                    drawCircle(
                        color = colors.mapPinCenter,
                        radius = if (isFocused) 9.5.dp.toPx() else 7.5.dp.toPx(),
                        center = pos
                    )
                    drawCircle(
                        color = pinColor,
                        radius = if (isFocused) 7.dp.toPx() else 5.2.dp.toPx(),
                        center = pos
                    )
                    if (isFound) {
                        drawCircle(
                            color = Color.White,
                            radius = 2.4.dp.toPx(),
                            center = pos
                        )
                    }
                }
            }

            // Top-left Compass Rose Badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
                    .then(if (colors.isDark) Modifier else Modifier.shadow(3.dp, RoundedCornerShape(12.dp)))
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.mapControlBg.copy(alpha = 0.90f))
                    .border(1.dp, colors.outline, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Explore,
                        contentDescription = null,
                        tint = colors.cyan,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "N ↑  •  ${filteredSecrets.size} pontos",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textPrimary
                    )
                }
            }

            // Right-side Zoom Controls
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MapControlFab(
                    icon = Icons.Rounded.Add,
                    contentDescription = "Aumentar zoom",
                    onClick = { scale = (scale + 0.45f).coerceAtMost(3.2f) }
                )
                MapControlFab(
                    icon = Icons.Rounded.Remove,
                    contentDescription = "Diminuir zoom",
                    onClick = {
                        val next = (scale - 0.45f).coerceAtLeast(1f)
                        scale = next
                        if (next <= 1.02f) panOffset = Offset.Zero
                    }
                )
                MapControlFab(
                    icon = Icons.Rounded.MyLocation,
                    contentDescription = "Centralizar mapa",
                    onClick = {
                        scale = 1f
                        panOffset = Offset.Zero
                    }
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // BOTTOM QUICK-JUMP CAROUSEL
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            items(filteredSecrets, key = { it.id }) { loc ->
                val isFocused = focusedSecret?.id == loc.id
                val isFound = loc.id in state.foundSecretIds
                val isFutureChapter = loc.minChapter > state.currentChapter && state.currentChapter < 6
                val catColor = loc.category.accent(colors)
                val regColor = loc.region.accent(colors)
                val cardShape = RoundedCornerShape(18.dp)

                Column(
                    modifier = Modifier
                        .width(250.dp)
                        .then(if (colors.isDark) Modifier else Modifier.shadow(2.5.dp, cardShape))
                        .clip(cardShape)
                        .background(if (isFocused) colors.cardElevated else colors.card)
                        .border(
                            width = if (isFocused) 1.5.dp else 1.dp,
                            color = if (isFocused) catColor else colors.outlineSoft,
                            shape = cardShape
                        )
                        .clickable {
                            if (isFocused) {
                                sheetSecret = loc
                            } else {
                                focusOnSecret(loc)
                            }
                        }
                        .padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .clip(RoundedCornerShape(50))
                            .background(catColor.copy(alpha = if (isFocused) 1f else 0.45f))
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        VicePillBadge(
                            text = loc.region.displayName,
                            color = regColor
                        )
                        if (isFound) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = "Encontrado",
                                tint = colors.mint,
                                modifier = Modifier.size(18.dp)
                            )
                        } else if (isFutureChapter) {
                            VicePillBadge(
                                text = "Cap. ${loc.minChapter}+",
                                color = colors.gold
                            )
                        }
                    }
                    Spacer(Modifier.height(7.dp))
                    Text(
                        text = loc.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = loc.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = loc.timeWindow,
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (isFocused) "Ver guia →" else "Localizar",
                            style = MaterialTheme.typography.labelSmall,
                            color = catColor,
                            modifier = Modifier.clickable {
                                focusOnSecret(loc)
                                sheetSecret = loc
                            }
                        )
                    }
                }
            }
        }
    }

    // DETAIL BOTTOM SHEET
    sheetSecret?.let { loc ->
        val isFound = loc.id in state.foundSecretIds
        val isFutureChapter = loc.minChapter > state.currentChapter && state.currentChapter < 6
        val catColor = loc.category.accent(colors)
        val regColor = loc.region.accent(colors)

        ModalBottomSheet(
            onDismissRequest = { sheetSecret = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = colors.surface,
            contentColor = colors.textPrimary
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    VicePillBadge(text = loc.category.label, color = catColor)
                    VicePillBadge(text = loc.region.displayName, color = regColor)
                }

                Spacer(Modifier.height(10.dp))
                Text(
                    text = loc.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = colors.textPrimary
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = null,
                        tint = regColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${loc.subArea} (${loc.region.county})",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                }

                Spacer(Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    InfoMiniBox(
                        icon = Icons.Rounded.Schedule,
                        title = "Horário ideal",
                        value = loc.timeWindow,
                        accent = colors.cyan,
                        modifier = Modifier.weight(1f)
                    )
                    InfoMiniBox(
                        icon = Icons.Rounded.LockClock,
                        title = "Disponibilidade",
                        value = if (loc.minChapter <= 1) "Desde o Capítulo 1" else "A partir do Cap. ${loc.minChapter}",
                        accent = if (isFutureChapter) colors.gold else colors.mint,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (isFutureChapter) {
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(colors.gold.copy(alpha = 0.14f))
                            .border(1.dp, colors.gold.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Aviso: você marcou estar no Capítulo ${state.currentChapter}. Este segredo só ativa a partir do Capítulo ${loc.minChapter}.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.gold
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Como Encontrar",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textPrimary
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = loc.howToFind,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )

                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.card)
                        .border(1.dp, colors.gold.copy(alpha = 0.38f), RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Stars,
                        contentDescription = null,
                        tint = colors.gold,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "O que você encontra / Recompensa",
                            style = MaterialTheme.typography.titleSmall,
                            color = colors.gold
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = loc.reward,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textPrimary
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))
                Button(
                    onClick = { viewModel.toggleSecretFound(loc.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFound) colors.mint else colors.pink,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (isFound) "Encontrado! (Toque para desmarcar)" else "Marcar como Encontrado",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawLeonidaTerrain(
    toScreen: (Float, Float) -> Offset,
    selectedRegion: LeonidaRegion?,
    scale: Float,
    colors: LeonidaColors
) {
    // 1. Main Peninsula
    val mainland = Path().apply {
        val pts = listOf(
            0.22f to 0.08f,
            0.72f to 0.08f,
            0.77f to 0.24f,
            0.82f to 0.42f,
            0.80f to 0.65f,
            0.72f to 0.77f,
            0.55f to 0.80f,
            0.38f to 0.76f,
            0.25f to 0.65f,
            0.16f to 0.54f,
            0.19f to 0.36f,
            0.21f to 0.19f
        )
        pts.forEachIndexed { idx, (x, y) ->
            val p = toScreen(x, y)
            if (idx == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
        }
        close()
    }
    drawPath(
        path = mainland,
        brush = Brush.verticalGradient(
            listOf(colors.mapLandTop, colors.mapLandMid, colors.mapLandBottom)
        )
    )
    drawPath(
        path = mainland,
        color = colors.cyan.copy(alpha = if (colors.isDark) 0.5f else 0.65f),
        style = Stroke(width = 2.dp.toPx())
    )

    // 2. Regional Biome Tints
    fun drawZoneBlob(centerX: Float, centerY: Float, radiusNorm: Float, region: LeonidaRegion) {
        val isHighlighted = selectedRegion == null || selectedRegion == region
        val accent = region.accent(colors)
        val c = toScreen(centerX, centerY)
        val r = radiusNorm * size.minDimension * scale
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    accent.copy(alpha = if (isHighlighted) (if (colors.isDark) 0.26f else 0.18f) else 0.05f),
                    Color.Transparent
                ),
                center = c,
                radius = r
            ),
            radius = r,
            center = c
        )
    }

    drawZoneBlob(0.49f, 0.16f, 0.20f, LeonidaRegion.MOUNT_KALAGA)
    drawZoneBlob(0.50f, 0.38f, 0.18f, LeonidaRegion.AMBROSIA)
    drawZoneBlob(0.25f, 0.50f, 0.16f, LeonidaRegion.PORT_GELLHORN)
    drawZoneBlob(0.48f, 0.66f, 0.19f, LeonidaRegion.GRASSRIVERS)
    drawZoneBlob(0.78f, 0.55f, 0.19f, LeonidaRegion.VICE_CITY)

    // 3. Lake Leonida (Inland lake in Ambrosia)
    val lake = Path().apply {
        val pts = listOf(
            0.47f to 0.40f,
            0.57f to 0.39f,
            0.60f to 0.46f,
            0.55f to 0.51f,
            0.46f to 0.49f,
            0.44f to 0.44f
        )
        pts.forEachIndexed { idx, (x, y) ->
            val p = toScreen(x, y)
            if (idx == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
        }
        close()
    }
    drawPath(lake, color = colors.mapLake)
    drawPath(lake, color = colors.cyan.copy(alpha = 0.55f), style = Stroke(1.3.dp.toPx()))

    // 4. Vice Beach & Barrier Islands
    val viceBeach = Path().apply {
        val pts = listOf(
            0.85f to 0.42f,
            0.90f to 0.46f,
            0.88f to 0.63f,
            0.84f to 0.65f,
            0.83f to 0.51f
        )
        pts.forEachIndexed { idx, (x, y) ->
            val p = toScreen(x, y)
            if (idx == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
        }
        close()
    }
    drawPath(viceBeach, color = colors.mapBarrierIsland)
    drawPath(
        viceBeach,
        color = LeonidaRegion.VICE_CITY.accent(colors).copy(alpha = 0.85f),
        style = Stroke(1.6.dp.toPx())
    )

    // 5. Leonida Keys Archipelago
    val keysAccent = LeonidaRegion.LEONIDA_KEYS.accent(colors)
    val keysIslands = listOf(
        Triple(0.70f, 0.82f, 0.045f),
        Triple(0.61f, 0.85f, 0.055f),
        Triple(0.51f, 0.87f, 0.045f),
        Triple(0.41f, 0.89f, 0.042f)
    )
    val bridgePath = Path().apply {
        val start = toScreen(0.74f, 0.74f)
        moveTo(start.x, start.y)
        keysIslands.forEach { (kx, ky, _) ->
            val pt = toScreen(kx, ky)
            lineTo(pt.x, pt.y)
        }
    }
    drawPath(
        path = bridgePath,
        color = keysAccent.copy(alpha = 0.78f),
        style = Stroke(
            width = 2.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 6f))
        )
    )
    keysIslands.forEach { (kx, ky, rNorm) ->
        val c = toScreen(kx, ky)
        val r = rNorm * size.minDimension * scale * 0.6f
        drawCircle(color = colors.mapKeyIsland, radius = r, center = c)
        drawCircle(color = keysAccent.copy(alpha = 0.85f), radius = r, center = c, style = Stroke(1.5.dp.toPx()))
    }

    // 6. Major Highways
    val highways = listOf(
        listOf(0.50f to 0.14f, 0.52f to 0.34f, 0.75f to 0.50f, 0.74f to 0.74f),
        listOf(0.24f to 0.49f, 0.44f to 0.48f, 0.76f to 0.54f),
        listOf(0.24f to 0.49f, 0.38f to 0.66f, 0.56f to 0.75f, 0.74f to 0.74f),
        listOf(0.80f to 0.53f, 0.86f to 0.55f)
    )
    highways.forEach { route ->
        val p = Path()
        route.forEachIndexed { idx, (rx, ry) ->
            val pt = toScreen(rx, ry)
            if (idx == 0) p.moveTo(pt.x, pt.y) else p.lineTo(pt.x, pt.y)
        }
        drawPath(p, color = colors.mapHighway, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
    }

    // 7. Mount Kalaga mountain peaks
    val kalagaAccent = LeonidaRegion.MOUNT_KALAGA.accent(colors)
    val peaks = listOf(0.41f to 0.17f, 0.53f to 0.13f, 0.61f to 0.18f)
    peaks.forEach { (px, py) ->
        val tip = toScreen(px, py - 0.025f)
        val left = toScreen(px - 0.03f, py + 0.02f)
        val right = toScreen(px + 0.03f, py + 0.02f)
        val tri = Path().apply {
            moveTo(tip.x, tip.y)
            lineTo(right.x, right.y)
            lineTo(left.x, left.y)
            close()
        }
        drawPath(tri, color = kalagaAccent.copy(alpha = 0.55f), style = Stroke(1.4.dp.toPx()))
    }

    // 8. Region Labels with halo for instant legibility in both Light and Dark maps
    val textSizePx = 11.5f * density * (0.85f + scale * 0.15f)
    val haloPaint = android.graphics.Paint().apply {
        isAntiAlias = true
        textAlign = android.graphics.Paint.Align.CENTER
        typeface = android.graphics.Typeface.create(android.graphics.Typeface.SANS_SERIF, android.graphics.Typeface.BOLD)
        textSize = textSizePx
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = 3.5f * density
        color = if (colors.isDark) {
            android.graphics.Color.argb(185, 8, 7, 18)
        } else {
            android.graphics.Color.argb(230, 255, 255, 255)
        }
    }
    val textPaint = android.graphics.Paint().apply {
        isAntiAlias = true
        textAlign = android.graphics.Paint.Align.CENTER
        typeface = android.graphics.Typeface.create(android.graphics.Typeface.SANS_SERIF, android.graphics.Typeface.BOLD)
        textSize = textSizePx
        style = android.graphics.Paint.Style.FILL
    }
    val labels = listOf(
        Triple("MOUNT KALAGA", 0.49f to 0.22f, LeonidaRegion.MOUNT_KALAGA),
        Triple("AMBROSIA", 0.52f to 0.33f, LeonidaRegion.AMBROSIA),
        Triple("PORT GELLHORN", 0.26f to 0.42f, LeonidaRegion.PORT_GELLHORN),
        Triple("GRASSRIVERS", 0.48f to 0.70f, LeonidaRegion.GRASSRIVERS),
        Triple("VICE CITY", 0.78f to 0.42f, LeonidaRegion.VICE_CITY),
        Triple("LEONIDA KEYS", 0.58f to 0.94f, LeonidaRegion.LEONIDA_KEYS)
    )
    labels.forEach { (name, coord, region) ->
        val pt = toScreen(coord.first, coord.second)
        val color = region.accent(colors)
        textPaint.color = android.graphics.Color.argb(
            245,
            (color.red * 255).toInt(),
            (color.green * 255).toInt(),
            (color.blue * 255).toInt()
        )
        drawContext.canvas.nativeCanvas.drawText(name, pt.x, pt.y, haloPaint)
        drawContext.canvas.nativeCanvas.drawText(name, pt.x, pt.y, textPaint)
    }
}

@Composable
private fun RegionFilterPill(
    label: String,
    accent: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = LeonidaTheme.colors
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) accent.copy(alpha = if (colors.isDark) 0.22f else 0.14f) else colors.card)
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
private fun CategorySmallPill(
    label: String,
    accent: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = LeonidaTheme.colors
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) accent.copy(alpha = if (colors.isDark) 0.18f else 0.12f) else Color.Transparent)
            .border(1.dp, if (selected) accent else colors.outlineSoft, RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) accent else colors.textMuted
        )
    }
}

@Composable
private fun MapControlFab(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    val colors = LeonidaTheme.colors
    Box(
        modifier = Modifier
            .size(38.dp)
            .then(if (colors.isDark) Modifier else Modifier.shadow(4.dp, CircleShape))
            .clip(CircleShape)
            .background(colors.mapControlBg.copy(alpha = 0.92f))
            .border(1.dp, colors.cyan.copy(alpha = 0.45f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = colors.textPrimary,
            modifier = Modifier.size(19.dp)
        )
    }
}

@Composable
private fun InfoMiniBox(
    icon: ImageVector,
    title: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    val colors = LeonidaTheme.colors
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(colors.card)
            .border(1.dp, colors.outlineSoft, RoundedCornerShape(14.dp))
            .padding(11.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(15.dp))
            Text(title, style = MaterialTheme.typography.labelSmall, color = colors.textMuted)
        }
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.bodySmall, color = colors.textPrimary, fontWeight = FontWeight.SemiBold)
    }
}
