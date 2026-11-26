package com.leonida.gtavicompanion.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Label
import androidx.compose.material.icons.automirrored.rounded.LabelOff
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.FullscreenExit
import androidx.compose.material.icons.rounded.GpsFixed
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.LocationCity
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.LockClock
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.SatelliteAlt
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Stars
import androidx.compose.material.icons.rounded.Terrain
import androidx.compose.material.icons.rounded.Water
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.leonida.gtavicompanion.data.GtaViCatalog
import com.leonida.gtavicompanion.data.MapAtlasRepository
import com.leonida.gtavicompanion.data.MapFitBounds
import com.leonida.gtavicompanion.data.MapSearchEngine
import com.leonida.gtavicompanion.data.MapViewportMath
import com.leonida.gtavicompanion.model.LandmarkKind
import com.leonida.gtavicompanion.model.LeonidaRegion
import com.leonida.gtavicompanion.model.MapAtlasManifest
import com.leonida.gtavicompanion.model.MapLandmark
import com.leonida.gtavicompanion.model.MapLayerAsset
import com.leonida.gtavicompanion.model.MapLayerStyle
import com.leonida.gtavicompanion.model.MapSearchResult
import com.leonida.gtavicompanion.model.SecretCategory
import com.leonida.gtavicompanion.model.SecretLocation
import com.leonida.gtavicompanion.model.UserProgressState
import com.leonida.gtavicompanion.ui.CompanionViewModel
import com.leonida.gtavicompanion.ui.components.VicePillBadge
import com.leonida.gtavicompanion.ui.theme.DarkLeonidaColors
import com.leonida.gtavicompanion.ui.theme.LeonidaColors
import com.leonida.gtavicompanion.ui.theme.LeonidaTheme
import com.leonida.gtavicompanion.ui.theme.LightLeonidaColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.hypot
import kotlin.math.roundToInt

private data class DecodedBaseMap(
    val bitmap: ImageBitmap,
    val sourceWidth: Int,
    val sourceHeight: Int,
    val baseSampleSize: Int,
    val oceanColor: Color,
    val isDarkImagery: Boolean
)

private data class DecodedDetailTile(
    val bitmap: ImageBitmap,
    val srcRectPx: Rect,
    val normLeft: Float,
    val normTop: Float,
    val normRight: Float,
    val normBottom: Float,
    val sampleSize: Int
)

private class RegionDecoderHolder {
    private val lock = Any()
    private var decoder: BitmapRegionDecoder? = null
    private var closed = false

    fun open(context: Context, assetPath: String) {
        val opened = runCatching {
            context.assets.open(assetPath).use { stream ->
                @Suppress("DEPRECATION")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    BitmapRegionDecoder.newInstance(stream)
                } else {
                    BitmapRegionDecoder.newInstance(stream, false)
                }
            }
        }.getOrNull()

        synchronized(lock) {
            decoder?.recycle()
            if (closed) {
                opened?.recycle()
                decoder = null
            } else {
                decoder = opened
            }
        }
    }

    fun decodeRegion(rect: Rect, sampleSize: Int): Bitmap? {
        synchronized(lock) {
            val active = decoder ?: return null
            if (closed || active.isRecycled) return null
            return runCatching {
                val opts = BitmapFactory.Options().apply {
                    inSampleSize = sampleSize.coerceAtLeast(1)
                    inPreferredConfig = Bitmap.Config.RGB_565
                }
                active.decodeRegion(rect, opts)
            }.getOrNull()
        }
    }

    fun close() {
        synchronized(lock) {
            closed = true
            decoder?.recycle()
            decoder = null
        }
    }
}

private fun decodeOverviewBitmap(context: Context, assetPath: String): DecodedBaseMap? {
    return runCatching {
        val boundsOpts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.assets.open(assetPath).use { BitmapFactory.decodeStream(it, null, boundsOpts) }
        val srcW = boundsOpts.outWidth
        val srcH = boundsOpts.outHeight
        if (srcW <= 0 || srcH <= 0) return null

        // Keep overview bitmap <= 1600px (1280x1600 = ~4.1 MB RGB_565 for opaque 2560x3200 WebP)
        // for silky 60fps gestures & zero OOM on Android API 26+, while BitmapRegionDecoder supplies 1:1 tiles
        var sample = 1
        while (maxOf(srcW, srcH) / sample > 1600) {
            sample *= 2
        }
        val decodeOpts = BitmapFactory.Options().apply {
            inSampleSize = sample
            inPreferredConfig = Bitmap.Config.RGB_565
        }
        val bmp = context.assets.open(assetPath).use {
            BitmapFactory.decodeStream(it, null, decodeOpts)
        } ?: return null

        // Sample corner ocean pixel for seamless surround without letterbox seams
        val pxX = 4.coerceAtMost(bmp.width - 1)
        val pxY = 4.coerceAtMost(bmp.height - 1)
        val cornerPixel = bmp.getPixel(pxX, pxY)
        val oceanColor = Color(cornerPixel)

        // Estimate overall map brightness on a sparse 8x8 grid so overlay labels adapt contrast cleanly
        var lumSum = 0.0
        var lumCount = 0
        for (gx in 1..7) {
            for (gy in 1..7) {
                val c = bmp.getPixel((bmp.width * gx) / 8, (bmp.height * gy) / 8)
                val r = android.graphics.Color.red(c)
                val g = android.graphics.Color.green(c)
                val b = android.graphics.Color.blue(c)
                lumSum += (0.299 * r + 0.587 * g + 0.114 * b)
                lumCount++
            }
        }
        val avgLum = if (lumCount > 0) lumSum / lumCount else 128.0

        DecodedBaseMap(
            bitmap = bmp.asImageBitmap(),
            sourceWidth = srcW,
            sourceHeight = srcH,
            baseSampleSize = sample,
            oceanColor = oceanColor,
            isDarkImagery = avgLum < 126.0
        )
    }.getOrNull()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    state: UserProgressState,
    viewModel: CompanionViewModel
) {
    val context = LocalContext.current
    val density = LocalDensity.current.density
    val colors = LeonidaTheme.colors
    val coroutineScope = rememberCoroutineScope()

    val manifest: MapAtlasManifest = remember(context) {
        MapAtlasRepository.loadFromAssets(context)
    }

    val calibratedSecrets: List<SecretLocation> = remember(manifest) {
        MapAtlasRepository.calibrateSecrets(GtaViCatalog.secretLocations, manifest)
    }

    val availableLayers = manifest.layers
    val preferredDefaultStyle = remember(availableLayers, colors.isDark) {
        if (colors.isDark && availableLayers.any { it.style == MapLayerStyle.DARK_ATLAS }) {
            MapLayerStyle.DARK_ATLAS
        } else {
            availableLayers.firstOrNull { it.style == MapLayerStyle.ATLAS }?.style
                ?: availableLayers.firstOrNull()?.style
                ?: MapLayerStyle.ATLAS
        }
    }

    var activeLayerStyle by remember(preferredDefaultStyle) { mutableStateOf(preferredDefaultStyle) }
    val activeLayerAsset: MapLayerAsset? = remember(availableLayers, activeLayerStyle) {
        availableLayers.firstOrNull { it.style == activeLayerStyle } ?: availableLayers.firstOrNull()
    }

    var selectedRegion by remember { mutableStateOf<LeonidaRegion?>(null) }
    var selectedCategory by remember { mutableStateOf<SecretCategory?>(null) }
    var focusedSecret by remember(calibratedSecrets) { mutableStateOf(calibratedSecrets.firstOrNull()) }
    var focusedLandmark by remember { mutableStateOf<MapLandmark?>(null) }
    var sheetSecret by remember { mutableStateOf<SecretLocation?>(null) }

    var showPlaceLabels by remember { mutableStateOf(true) }
    var isFullscreen by remember { mutableStateOf(false) }
    var isSearchOpen by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Camera state backed by Animatable for buttery 60fps gestures AND smooth animateTo flights
    val scaleAnim = remember { Animatable(1f) }
    val panXAnim = remember { Animatable(0f) }
    val panYAnim = remember { Animatable(0f) }
    var cameraAnimJob by remember { mutableStateOf<Job?>(null) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    // High-resolution map raster + RegionDecoder state
    var baseMap by remember { mutableStateOf<DecodedBaseMap?>(null) }
    var detailTile by remember { mutableStateOf<DecodedDetailTile?>(null) }
    val decoderHolder = remember(activeLayerAsset?.assetPath) { RegionDecoderHolder() }

    DisposableEffect(activeLayerAsset?.assetPath) {
        onDispose {
            decoderHolder.close()
        }
    }

    LaunchedEffect(activeLayerAsset?.assetPath) {
        val path = activeLayerAsset?.assetPath ?: return@LaunchedEffect
        detailTile = null
        val loaded = withContext(Dispatchers.IO) {
            decoderHolder.open(context, path)
            decodeOverviewBitmap(context, path)
        }
        baseMap = loaded
    }

    val imageWidth = baseMap?.sourceWidth ?: manifest.imageWidth
    val imageHeight = baseMap?.sourceHeight ?: manifest.imageHeight

    val fitBounds: MapFitBounds = remember(canvasSize, imageWidth, imageHeight) {
        MapViewportMath.computeFitBounds(
            viewportW = canvasSize.width.toFloat(),
            viewportH = canvasSize.height.toFloat(),
            imageW = imageWidth,
            imageH = imageHeight
        )
    }

    // Debounced high-res detail tile decoder using BitmapRegionDecoder when zoomed >= 1.35x
    LaunchedEffect(baseMap, canvasSize, fitBounds) {
        val currentBase = baseMap ?: return@LaunchedEffect
        if (currentBase.baseSampleSize <= 1 || canvasSize.width <= 0 || canvasSize.height <= 0) {
            return@LaunchedEffect
        }
        snapshotFlow {
            Triple(scaleAnim.value, panXAnim.value, panYAnim.value)
        }
            .distinctUntilChanged()
            .collectLatest { (sc, px, py) ->
                if (sc < 1.35f) {
                    if (detailTile != null) detailTile = null
                    return@collectLatest
                }
                delay(85)
                val vw = canvasSize.width.toFloat()
                val vh = canvasSize.height.toFloat()
                val (visLeftN, visTopN) = MapViewportMath.screenToMap(0f, 0f, sc, px, py, vw, vh, fitBounds)
                val (visRightN, visBottomN) = MapViewportMath.screenToMap(vw, vh, sc, px, py, vw, vh, fitBounds)

                val srcW = currentBase.sourceWidth
                val srcH = currentBase.sourceHeight
                val visLeftPx = (visLeftN * srcW).toInt().coerceIn(0, srcW - 1)
                val visTopPx = (visTopN * srcH).toInt().coerceIn(0, srcH - 1)
                val visRightPx = (visRightN * srcW).toInt().coerceIn(visLeftPx + 1, srcW)
                val visBottomPx = (visBottomN * srcH).toInt().coerceIn(visTopPx + 1, srcH)

                val spanX = (visRightPx - visLeftPx).coerceAtLeast(64)
                val spanY = (visBottomPx - visTopPx).coerceAtLeast(64)
                val padX = (spanX * 0.15f).toInt()
                val padY = (spanY * 0.15f).toInt()
                val reqRect = Rect(
                    (visLeftPx - padX).coerceIn(0, srcW - 1),
                    (visTopPx - padY).coerceIn(0, srcH - 1),
                    (visRightPx + padX).coerceIn(1, srcW),
                    (visBottomPx + padY).coerceIn(1, srcH)
                )
                if (reqRect.width() < 32 || reqRect.height() < 32) return@collectLatest

                // Choose sample size so decoded RGB_565 patch <= 1800px longest edge (~5.5 MB max),
                // unlocking native 1:1 2560x3200 sharpness already around ~2.2x zoom!
                var desiredSample = 1
                val maxEdge = maxOf(reqRect.width(), reqRect.height())
                while (maxEdge / desiredSample > 1800) {
                    desiredSample *= 2
                }
                if (desiredSample >= currentBase.baseSampleSize) return@collectLatest

                val existing = detailTile
                val alreadyCovered = existing != null &&
                    existing.sampleSize <= desiredSample &&
                    existing.srcRectPx.left <= visLeftPx &&
                    existing.srcRectPx.top <= visTopPx &&
                    existing.srcRectPx.right >= visRightPx &&
                    existing.srcRectPx.bottom >= visBottomPx
                if (alreadyCovered) return@collectLatest

                val decoded = withContext(Dispatchers.IO) {
                    decoderHolder.decodeRegion(reqRect, desiredSample)
                }
                if (decoded != null) {
                    detailTile = DecodedDetailTile(
                        bitmap = decoded.asImageBitmap(),
                        srcRectPx = reqRect,
                        normLeft = reqRect.left.toFloat() / srcW,
                        normTop = reqRect.top.toFloat() / srcH,
                        normRight = reqRect.right.toFloat() / srcW,
                        normBottom = reqRect.bottom.toFloat() / srcH,
                        sampleSize = desiredSample
                    )
                }
            }
    }

    val filteredSecrets = remember(calibratedSecrets, selectedRegion, selectedCategory) {
        calibratedSecrets.filter { loc ->
            (selectedRegion == null || loc.region == selectedRegion) &&
                (selectedCategory == null || loc.category == selectedCategory)
        }
    }

    val searchResults = remember(searchQuery, calibratedSecrets, manifest.landmarks) {
        MapSearchEngine.search(
            query = searchQuery,
            secrets = calibratedSecrets,
            landmarks = manifest.landmarks
        )
    }

    val carouselState = rememberLazyListState()

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

    fun animateCameraTo(targetScale: Float, targetPanX: Float, targetPanY: Float) {
        cameraAnimJob?.cancel()
        val clampedScale = targetScale.coerceIn(MapViewportMath.MIN_SCALE, MapViewportMath.MAX_SCALE)
        val (clampedX, clampedY) = MapViewportMath.clampPan(
            panX = targetPanX,
            panY = targetPanY,
            scale = clampedScale,
            viewportW = canvasSize.width.toFloat(),
            viewportH = canvasSize.height.toFloat(),
            fit = fitBounds
        )
        cameraAnimJob = coroutineScope.launch {
            val spec = tween<Float>(durationMillis = 480, easing = FastOutSlowInEasing)
            launch { scaleAnim.animateTo(clampedScale, spec) }
            launch { panXAnim.animateTo(clampedX, spec) }
            launch { panYAnim.animateTo(clampedY, spec) }
        }
    }

    fun flyToPoint(nx: Float, ny: Float, targetScale: Float) {
        if (canvasSize.width <= 0 || canvasSize.height <= 0) return
        val clampedScale = targetScale.coerceIn(MapViewportMath.MIN_SCALE, MapViewportMath.MAX_SCALE)
        val (tx, ty) = MapViewportMath.targetPanForPoint(
            nx = nx,
            ny = ny,
            targetScale = clampedScale,
            viewportW = canvasSize.width.toFloat(),
            viewportH = canvasSize.height.toFloat(),
            fit = fitBounds
        )
        animateCameraTo(clampedScale, tx, ty)
    }

    fun focusOnSecret(loc: SecretLocation, minZoom: Float = 3.1f) {
        focusedSecret = loc
        focusedLandmark = null
        flyToPoint(loc.mapX, loc.mapY, maxOf(scaleAnim.value, minZoom))
        val idx = filteredSecrets.indexOfFirst { it.id == loc.id }
        if (idx >= 0) {
            coroutineScope.launch {
                carouselState.animateScrollToItem(idx)
            }
        }
    }

    fun focusOnLandmark(lm: MapLandmark) {
        focusedLandmark = lm
        val targetZoom = when (lm.kind) {
            LandmarkKind.REGION -> lm.defaultZoom ?: 2.35f
            LandmarkKind.COUNTY -> 2.15f
            LandmarkKind.TOWN, LandmarkKind.AIRPORT, LandmarkKind.ISLAND, LandmarkKind.NEIGHBORHOOD ->
                maxOf(lm.minZoom + 0.9f, 3.3f)
            LandmarkKind.WATER, LandmarkKind.LANDMARK, LandmarkKind.HIGHWAY ->
                maxOf(lm.minZoom + 0.7f, 3.1f)
        }
        flyToPoint(lm.mapX, lm.mapY, targetZoom)
    }

    // Center GPS coordinate + exact in-game metric scale bar telemetry
    val currentScale = scaleAnim.value
    val currentPanX = panXAnim.value
    val currentPanY = panYAnim.value
    val worldSpanMeters = remember(manifest) { manifest.imageWidth * manifest.metersPerPixel }
    val centerCoord = remember(currentScale, currentPanX, currentPanY, canvasSize, fitBounds) {
        if (canvasSize.width > 0 && canvasSize.height > 0) {
            MapViewportMath.screenToMap(
                sx = canvasSize.width / 2f,
                sy = canvasSize.height / 2f,
                scale = currentScale,
                panX = currentPanX,
                panY = currentPanY,
                viewportW = canvasSize.width.toFloat(),
                viewportH = canvasSize.height.toFloat(),
                fit = fitBounds
            )
        } else {
            0.5f to 0.5f
        }
    }
    val gpsReadout = remember(centerCoord) {
        MapViewportMath.formatGps(centerCoord.first, centerCoord.second)
    }
    val scaleBarSpec = remember(currentScale, fitBounds, density, worldSpanMeters) {
        MapViewportMath.computeScaleBar(currentScale, fitBounds, density, worldSpanMeters)
    }

    // Pre-sort landmarks once by display importance for clean collision-free rendering
    val sortedLandmarks = remember(manifest.landmarks) {
        manifest.landmarks.sortedBy { landmarkPriority(it.kind) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // HEADER + FILTERS (hidden smoothly in fullscreen map mode)
        AnimatedVisibility(
            visible = !isFullscreen,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mapa de Leonida",
                            style = MaterialTheme.typography.headlineSmall,
                            color = colors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Zoom até 8x • ${filteredSecrets.size} segredos • ${manifest.landmarks.size} locais",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSearchOpen) colors.cyan.copy(alpha = 0.18f) else colors.card
                                )
                                .border(
                                    1.dp,
                                    if (isSearchOpen) colors.cyan else colors.outline,
                                    CircleShape
                                )
                                .clickable {
                                    isSearchOpen = !isSearchOpen
                                    if (!isSearchOpen) searchQuery = ""
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isSearchOpen) Icons.Rounded.Close else Icons.Rounded.Search,
                                contentDescription = "Buscar no mapa",
                                tint = if (isSearchOpen) colors.cyan else colors.textPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        VicePillBadge(
                            text = "${state.foundSecretIds.size}/${calibratedSecrets.size} achados",
                            color = colors.mint
                        )
                    }
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
                            focusedLandmark = null
                            animateCameraTo(1f, 0f, 0f)
                        }
                    )
                    LeonidaRegion.entries.forEach { region ->
                        RegionFilterPill(
                            label = region.displayName,
                            accent = region.accent(colors),
                            selected = selectedRegion == region,
                            onClick = {
                                if (selectedRegion == region) {
                                    selectedRegion = null
                                    animateCameraTo(1f, 0f, 0f)
                                } else {
                                    selectedRegion = region
                                    val (rx, ry, rz) = MapAtlasRepository.regionFocusTarget(
                                        region = region,
                                        manifest = manifest,
                                        secrets = calibratedSecrets
                                    )
                                    flyToPoint(rx, ry, rz)
                                }
                            }
                        )
                    }
                }

                Spacer(Modifier.height(7.dp))

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

                Spacer(Modifier.height(8.dp))
            }
        }

        // INTERACTIVE ULTRA-DETAILED LEONIDA MAP VIEWPORT
        val mapShape = RoundedCornerShape(if (isFullscreen) 18.dp else 24.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(
                    horizontal = if (isFullscreen) 8.dp else 14.dp,
                    vertical = if (isFullscreen) 6.dp else 0.dp
                )
                .then(if (colors.isDark) Modifier else Modifier.shadow(4.dp, mapShape))
                .clip(mapShape)
                .background(baseMap?.oceanColor ?: colors.mapOceanTop)
                .border(1.2.dp, colors.cyan.copy(alpha = 0.42f), mapShape)
                .onSizeChanged { canvasSize = it }
                .pointerInput(fitBounds) {
                    detectTransformGestures { centroid, pan, zoom, _ ->
                        cameraAnimJob?.cancel()
                        val (nextScale, nextPanX, nextPanY) = MapViewportMath.applyTransformGesture(
                            currentScale = scaleAnim.value,
                            currentPanX = panXAnim.value,
                            currentPanY = panYAnim.value,
                            centroidX = centroid.x,
                            centroidY = centroid.y,
                            panDeltaX = pan.x,
                            panDeltaY = pan.y,
                            zoomFactor = zoom,
                            viewportW = canvasSize.width.toFloat(),
                            viewportH = canvasSize.height.toFloat(),
                            fit = fitBounds
                        )
                        coroutineScope.launch {
                            scaleAnim.snapTo(nextScale)
                            panXAnim.snapTo(nextPanX)
                            panYAnim.snapTo(nextPanY)
                        }
                    }
                }
                .pointerInput(filteredSecrets, sortedLandmarks, showPlaceLabels, fitBounds, canvasSize) {
                    detectTapGestures(
                        onDoubleTap = { tapPos ->
                            if (canvasSize.width <= 0 || canvasSize.height <= 0) return@detectTapGestures
                            val vw = canvasSize.width.toFloat()
                            val vh = canvasSize.height.toFloat()
                            val (nx, ny) = MapViewportMath.screenToMap(
                                sx = tapPos.x,
                                sy = tapPos.y,
                                scale = scaleAnim.value,
                                panX = panXAnim.value,
                                panY = panYAnim.value,
                                viewportW = vw,
                                viewportH = vh,
                                fit = fitBounds
                            )
                            when {
                                scaleAnim.value < 2.3f -> flyToPoint(nx.coerceIn(0f, 1f), ny.coerceIn(0f, 1f), 3.1f)
                                scaleAnim.value < 4.8f -> flyToPoint(nx.coerceIn(0f, 1f), ny.coerceIn(0f, 1f), 5.6f)
                                else -> animateCameraTo(1f, 0f, 0f)
                            }
                        },
                        onTap = { tapPos ->
                            if (canvasSize.width <= 0 || canvasSize.height <= 0) return@detectTapGestures
                            val vw = canvasSize.width.toFloat()
                            val vh = canvasSize.height.toFloat()
                            val sc = scaleAnim.value
                            val px = panXAnim.value
                            val py = panYAnim.value

                            // 1. Check Secret pins first (generous 40dp hit radius)
                            val secretHitPx = 40f * density
                            var bestSecret: SecretLocation? = null
                            var bestSecretDist = secretHitPx
                            filteredSecrets.forEach { loc ->
                                val (sx, sy) = MapViewportMath.mapToScreen(
                                    loc.mapX, loc.mapY, sc, px, py, vw, vh, fitBounds
                                )
                                val d = hypot(tapPos.x - sx, tapPos.y - sy)
                                if (d < bestSecretDist) {
                                    bestSecretDist = d
                                    bestSecret = loc
                                }
                            }
                            bestSecret?.let { tapped ->
                                focusedSecret = tapped
                                focusedLandmark = null
                                sheetSecret = tapped
                                val idx = filteredSecrets.indexOfFirst { it.id == tapped.id }
                                if (idx >= 0) {
                                    coroutineScope.launch { carouselState.animateScrollToItem(idx) }
                                }
                                return@detectTapGestures
                            }

                            // 2. Otherwise check visible neighborhoods / landmarks
                            if (showPlaceLabels) {
                                val lmHitPx = 34f * density
                                var bestLm: MapLandmark? = null
                                var bestLmDist = lmHitPx
                                sortedLandmarks.forEach { lm ->
                                    if (sc < lm.minZoom) return@forEach
                                    if (lm.maxZoom != null && sc > lm.maxZoom) return@forEach
                                    val (sx, sy) = MapViewportMath.mapToScreen(
                                        lm.mapX, lm.mapY, sc, px, py, vw, vh, fitBounds
                                    )
                                    val d = hypot(tapPos.x - sx, tapPos.y - sy)
                                    if (d < bestLmDist) {
                                        bestLmDist = d
                                        bestLm = lm
                                    }
                                }
                                focusedLandmark = bestLm
                            }
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val vw = size.width
                val vh = size.height
                val sc = scaleAnim.value
                val px = panXAnim.value
                val py = panYAnim.value

                val mapW = fitBounds.baseWidth * sc
                val mapH = fitBounds.baseHeight * sc
                val mapLeft = (vw - mapW) / 2f + px
                val mapTop = (vh - mapH) / 2f + py

                fun toScreen(nx: Float, ny: Float): Offset =
                    Offset(mapLeft + nx * mapW, mapTop + ny * mapH)

                val loadedBase = baseMap
                if (loadedBase != null) {
                    // 1. Seamless ocean surround matching exact edge pixel of the real map asset
                    drawRect(color = loadedBase.oceanColor)

                    // 2. Overview high-res raster base
                    drawImage(
                        image = loadedBase.bitmap,
                        dstOffset = IntOffset(mapLeft.roundToInt(), mapTop.roundToInt()),
                        dstSize = IntSize(mapW.roundToInt().coerceAtLeast(1), mapH.roundToInt().coerceAtLeast(1)),
                        filterQuality = FilterQuality.Medium
                    )

                    // 3. Sharp native-resolution RegionDecoder detail patch when zoomed in
                    val tile = detailTile
                    if (tile != null && sc >= 1.30f) {
                        val tLeft = mapLeft + tile.normLeft * mapW
                        val tTop = mapTop + tile.normTop * mapH
                        val tRight = mapLeft + tile.normRight * mapW
                        val tBottom = mapTop + tile.normBottom * mapH
                        val tW = (tRight - tLeft).roundToInt()
                        val tH = (tBottom - tTop).roundToInt()
                        if (tW > 0 && tH > 0) {
                            drawImage(
                                image = tile.bitmap,
                                dstOffset = IntOffset(tLeft.roundToInt(), tTop.roundToInt()),
                                dstSize = IntSize(tW, tH),
                                filterQuality = FilterQuality.Medium
                            )
                        }
                    }
                } else {
                    // Graceful preview / loading backdrop before bitmap decode completes
                    drawFallbackOceanBackdrop(toScreen = ::toScreen, colors = colors)
                }

                // 4. Progressive Disclosure Place Labels (Regions, Counties, Neighborhoods, Islands, Airports)
                if (showPlaceLabels && sortedLandmarks.isNotEmpty()) {
                    drawProgressiveLandmarks(
                        landmarks = sortedLandmarks,
                        toScreen = ::toScreen,
                        scale = sc,
                        selectedRegion = selectedRegion,
                        focusedLandmark = focusedLandmark,
                        isDarkMap = loadedBase?.isDarkImagery ?: colors.isDark,
                        hasRasterImagery = loadedBase != null
                    )
                }

                // 5. Pulsing beacon on focused searched/tapped landmark
                focusedLandmark?.let { lm ->
                    val pos = toScreen(lm.mapX, lm.mapY)
                    val lmAccent = lm.region?.accent(colors) ?: colors.cyan
                    val ringR = 12.dp.toPx() + pulseFactor * 18.dp.toPx()
                    drawCircle(
                        color = lmAccent.copy(alpha = (1f - pulseFactor) * 0.75f),
                        radius = ringR,
                        center = pos,
                        style = Stroke(width = 2.dp.toPx())
                    )
                    drawCircle(
                        color = colors.mapPinCenter,
                        radius = 6.dp.toPx(),
                        center = pos
                    )
                    drawCircle(
                        color = lmAccent,
                        radius = 4.dp.toPx(),
                        center = pos
                    )
                }

                // 6. Secret Location Pins (with found state, spoiler badge, focused pulse & label tag)
                drawSecretPins(
                    secrets = filteredSecrets,
                    foundIds = state.foundSecretIds,
                    currentChapter = state.currentChapter,
                    focusedSecret = focusedSecret,
                    pulseFactor = pulseFactor,
                    scale = sc,
                    toScreen = ::toScreen,
                    colors = colors
                )
            }

            // TOP-LEFT COMPASS & ZOOM BADGE
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
                        text = String.format(Locale.US, "N ↑ • %.1fx • %d pinos", currentScale, filteredSecrets.size),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textPrimary
                    )
                }
            }

            // TOP-RIGHT LAYER / LABELS / SEARCH / FULLSCREEN CONTROLS
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isFullscreen) {
                    MapToolPillButton(
                        icon = if (isSearchOpen) Icons.Rounded.Close else Icons.Rounded.Search,
                        label = null,
                        active = isSearchOpen,
                        contentDescription = "Buscar no mapa",
                        onClick = {
                            isSearchOpen = !isSearchOpen
                            if (!isSearchOpen) searchQuery = ""
                        }
                    )
                }

                if (availableLayers.size > 1) {
                    val layerIcon = when (activeLayerStyle) {
                        MapLayerStyle.SATELLITE -> Icons.Rounded.SatelliteAlt
                        MapLayerStyle.DARK_ATLAS -> Icons.Rounded.DarkMode
                        MapLayerStyle.ATLAS -> Icons.Rounded.LightMode
                    }
                    MapToolPillButton(
                        icon = layerIcon,
                        label = activeLayerStyle.shortLabel,
                        active = activeLayerStyle == MapLayerStyle.DARK_ATLAS || activeLayerStyle == MapLayerStyle.SATELLITE,
                        contentDescription = "Alternar camada do mapa (Dia / Noite)",
                        onClick = {
                            val idx = availableLayers.indexOfFirst { it.style == activeLayerStyle }
                            val nextIdx = (idx + 1) % availableLayers.size
                            activeLayerStyle = availableLayers[nextIdx].style
                        }
                    )
                }

                MapToolPillButton(
                    icon = if (showPlaceLabels) Icons.AutoMirrored.Rounded.Label else Icons.AutoMirrored.Rounded.LabelOff,
                    label = null,
                    active = showPlaceLabels,
                    contentDescription = if (showPlaceLabels) "Ocultar rótulos de bairros" else "Exibir rótulos de bairros",
                    onClick = { showPlaceLabels = !showPlaceLabels }
                )

                MapToolPillButton(
                    icon = if (isFullscreen) Icons.Rounded.FullscreenExit else Icons.Rounded.Fullscreen,
                    label = null,
                    active = isFullscreen,
                    contentDescription = if (isFullscreen) "Sair da tela cheia" else "Expandir mapa",
                    onClick = { isFullscreen = !isFullscreen }
                )
            }

            // FLOATING RETRACTABLE SEARCH OVERLAY (works identically in normal & fullscreen!)
            androidx.compose.animation.AnimatedVisibility(
                visible = isSearchOpen,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 52.dp, start = 10.dp, end = 10.dp)
                    .fillMaxWidth()
                    .zIndex(5f)
            ) {
                MapQuickSearchOverlay(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    results = searchResults,
                    foundSecretIds = state.foundSecretIds,
                    onClose = {
                        isSearchOpen = false
                        searchQuery = ""
                    },
                    onSelectResult = { result ->
                        isSearchOpen = false
                        searchQuery = ""
                        when (result) {
                            is MapSearchResult.Secret -> {
                                // Clear restrictive filters if the searched secret is currently hidden
                                if (selectedRegion != null && result.location.region != selectedRegion) {
                                    selectedRegion = null
                                }
                                if (selectedCategory != null && result.location.category != selectedCategory) {
                                    selectedCategory = null
                                }
                                focusOnSecret(result.location, minZoom = 3.8f)
                            }
                            is MapSearchResult.Place -> {
                                focusOnLandmark(result.landmark)
                            }
                        }
                    }
                )
            }

            // FOCUSED LANDMARK MINI-BANNER (when user searches or taps a neighborhood/island/airport)
            androidx.compose.animation.AnimatedVisibility(
                visible = focusedLandmark != null && !isSearchOpen,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 10.dp, end = 58.dp, bottom = 58.dp)
            ) {
                focusedLandmark?.let { lm ->
                    val accent = lm.region?.accent(colors) ?: colors.cyan
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = colors.mapControlBg.copy(alpha = 0.94f),
                        border = BorderStroke(1.dp, accent.copy(alpha = 0.65f)),
                        shadowElevation = if (colors.isDark) 0.dp else 4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = landmarkIcon(lm.kind),
                                contentDescription = null,
                                tint = accent,
                                modifier = Modifier.size(18.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = lm.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = colors.textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = listOfNotNull(
                                        lm.kind.label,
                                        lm.region?.displayName,
                                        lm.county
                                    ).distinct().joinToString(" • "),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.textSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (lm.subtitle.isNotBlank()) {
                                    Text(
                                        text = lm.subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = colors.textMuted,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Fechar destaque",
                                tint = colors.textMuted,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { focusedLandmark = null }
                            )
                        }
                    }
                }
            }

            // BOTTOM-LEFT GPS TELEMETRY & DYNAMIC METRIC SCALE BAR
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
                    .then(if (colors.isDark) Modifier else Modifier.shadow(3.dp, RoundedCornerShape(12.dp)))
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.mapControlBg.copy(alpha = 0.90f))
                    .border(1.dp, colors.outline, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.GpsFixed,
                            contentDescription = null,
                            tint = colors.pink,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = gpsReadout,
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textPrimary
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val barDp = (scaleBarSpec.barWidthPx / density).dp
                        Box(
                            modifier = Modifier
                                .width(barDp)
                                .height(5.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val stroke = 1.6.dp.toPx()
                                drawLine(
                                    color = colors.cyan,
                                    start = Offset(0f, size.height),
                                    end = Offset(size.width, size.height),
                                    strokeWidth = stroke
                                )
                                drawLine(
                                    color = colors.cyan,
                                    start = Offset(0f, 0f),
                                    end = Offset(0f, size.height),
                                    strokeWidth = stroke
                                )
                                drawLine(
                                    color = colors.cyan,
                                    start = Offset(size.width, 0f),
                                    end = Offset(size.width, size.height),
                                    strokeWidth = stroke
                                )
                            }
                        }
                        Text(
                            text = "${scaleBarSpec.label} • GTADB CC-BY",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textMuted
                        )
                    }
                }
            }

            // BOTTOM-RIGHT SMOOTH ZOOM & RESET CONTROLS
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MapControlFab(
                    icon = Icons.Rounded.Add,
                    contentDescription = "Aumentar zoom",
                    onClick = {
                        val nextScale = (scaleAnim.value * 1.48f).coerceAtMost(MapViewportMath.MAX_SCALE)
                        val ratio = nextScale / scaleAnim.value.coerceAtLeast(0.01f)
                        animateCameraTo(nextScale, panXAnim.value * ratio, panYAnim.value * ratio)
                    }
                )
                MapControlFab(
                    icon = Icons.Rounded.Remove,
                    contentDescription = "Diminuir zoom",
                    onClick = {
                        val nextScale = (scaleAnim.value / 1.48f).coerceAtLeast(MapViewportMath.MIN_SCALE)
                        if (nextScale <= 1.05f) {
                            animateCameraTo(1f, 0f, 0f)
                        } else {
                            val ratio = nextScale / scaleAnim.value.coerceAtLeast(0.01f)
                            animateCameraTo(nextScale, panXAnim.value * ratio, panYAnim.value * ratio)
                        }
                    }
                )
                MapControlFab(
                    icon = Icons.Rounded.MyLocation,
                    contentDescription = "Centralizar mapa",
                    onClick = {
                        focusedLandmark = null
                        animateCameraTo(1f, 0f, 0f)
                    }
                )
            }
        }

        // BOTTOM QUICK-JUMP SECRET CAROUSEL (hidden in fullscreen mode)
        AnimatedVisibility(
            visible = !isFullscreen,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column {
                Spacer(Modifier.height(8.dp))
                LazyRow(
                    state = carouselState,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 5.dp),
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
        }
    }

    // DETAIL BOTTOM SHEET (100% existing behavior + fly-to-pin action)
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
                        text = "${loc.subArea} (${loc.region.county}) • ${MapViewportMath.formatGps(loc.mapX, loc.mapY)}",
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

                Spacer(Modifier.height(16.dp))
                OutlinedButton(
                    onClick = {
                        sheetSecret = null
                        focusOnSecret(loc, minZoom = 4.4f)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, colors.cyan.copy(alpha = 0.55f))
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MyLocation,
                        contentDescription = null,
                        tint = colors.cyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Dar Zoom Máximo no Local",
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.cyan
                    )
                }

                Spacer(Modifier.height(10.dp))
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

private fun landmarkPriority(kind: LandmarkKind): Int = when (kind) {
    LandmarkKind.REGION -> 0
    LandmarkKind.COUNTY -> 1
    LandmarkKind.AIRPORT -> 2
    LandmarkKind.TOWN -> 3
    LandmarkKind.ISLAND -> 4
    LandmarkKind.NEIGHBORHOOD -> 5
    LandmarkKind.WATER -> 6
    LandmarkKind.LANDMARK -> 7
    LandmarkKind.HIGHWAY -> 8
}

private fun landmarkIcon(kind: LandmarkKind): ImageVector = when (kind) {
    LandmarkKind.AIRPORT -> Icons.Rounded.Flight
    LandmarkKind.WATER -> Icons.Rounded.Water
    LandmarkKind.ISLAND -> Icons.Rounded.Terrain
    LandmarkKind.TOWN, LandmarkKind.NEIGHBORHOOD -> Icons.Rounded.LocationCity
    LandmarkKind.REGION, LandmarkKind.COUNTY -> Icons.Rounded.Map
    LandmarkKind.LANDMARK, LandmarkKind.HIGHWAY -> Icons.Rounded.Place
}

private fun DrawScope.drawFallbackOceanBackdrop(
    toScreen: (Float, Float) -> Offset,
    colors: LeonidaColors
) {
    drawRect(
        brush = Brush.verticalGradient(
            listOf(colors.mapOceanTop, colors.mapOceanMid, colors.mapOceanBottom)
        )
    )
    val gridLines = 10
    for (i in 1 until gridLines) {
        val gx = toScreen(i / gridLines.toFloat(), 0f).x
        val gy = toScreen(0f, i / gridLines.toFloat()).y
        drawLine(colors.mapGrid, Offset(gx, 0f), Offset(gx, size.height), strokeWidth = 1f)
        drawLine(colors.mapGrid, Offset(0f, gy), Offset(size.width, gy), strokeWidth = 1f)
    }
}

private fun DrawScope.drawProgressiveLandmarks(
    landmarks: List<MapLandmark>,
    toScreen: (Float, Float) -> Offset,
    scale: Float,
    selectedRegion: LeonidaRegion?,
    focusedLandmark: MapLandmark?,
    isDarkMap: Boolean,
    hasRasterImagery: Boolean
) {
    val placedRects = ArrayList<RectF>(32)
    val marginPx = 28f * density

    val haloPaint = Paint().apply {
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
    }
    val textPaint = Paint().apply {
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        style = Paint.Style.FILL
    }

    val boldTypeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
    val italicTypeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC)
    val regularTypeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)

    landmarks.forEach { lm ->
        val isFocused = focusedLandmark?.id == lm.id
        val baseAlpha = MapViewportMath.computeLandmarkAlpha(
            scale = scale,
            minZoom = lm.minZoom,
            maxZoom = lm.maxZoom,
            isFocused = isFocused
        )
        if (baseAlpha < 0.08f) return@forEach

        val pt = toScreen(lm.mapX, lm.mapY)
        if (pt.x < -marginPx || pt.x > size.width + marginPx || pt.y < -marginPx || pt.y > size.height + marginPx) {
            return@forEach
        }

        val regionDim = if (selectedRegion != null && lm.region != null && lm.region != selectedRegion && !isFocused) {
            0.42f
        } else {
            1f
        }
        val finalAlpha = (baseAlpha * regionDim).coerceIn(0f, 1f)
        if (finalAlpha < 0.08f) return@forEach

        val displayText = when (lm.kind) {
            LandmarkKind.REGION, LandmarkKind.COUNTY -> lm.name.uppercase(Locale.ROOT)
            LandmarkKind.AIRPORT -> "✈ ${lm.name}"
            else -> lm.name
        }

        val baseSp = when (lm.kind) {
            LandmarkKind.REGION -> 11.5f + (scale.coerceAtMost(3f) - 1f) * 1.1f
            LandmarkKind.COUNTY -> 9.0f
            LandmarkKind.TOWN -> 10.4f
            LandmarkKind.AIRPORT, LandmarkKind.ISLAND -> 10.2f
            LandmarkKind.NEIGHBORHOOD -> 9.8f
            LandmarkKind.WATER -> 9.6f
            LandmarkKind.LANDMARK -> 9.4f
            LandmarkKind.HIGHWAY -> 8.8f
        }
        val textSizePx = baseSp * density

        val tf = when (lm.kind) {
            LandmarkKind.WATER -> italicTypeface
            LandmarkKind.COUNTY -> regularTypeface
            else -> boldTypeface
        }
        haloPaint.typeface = tf
        haloPaint.textSize = textSizePx
        haloPaint.strokeWidth = (if (lm.kind == LandmarkKind.REGION) 4.0f else 3.2f) * density
        textPaint.typeface = tf
        textPaint.textSize = textSizePx

        // Screen-space collision check so labels stay crisp and never pile up
        val textWidth = textPaint.measureText(displayText)
        val padH = 6f * density
        val padV = 3f * density
        val bounds = RectF(
            pt.x - textWidth / 2f - padH,
            pt.y - textSizePx - padV,
            pt.x + textWidth / 2f + padH,
            pt.y + padV
        )
        if (!isFocused && placedRects.any { RectF.intersects(it, bounds) }) {
            return@forEach
        }
        placedRects += bounds

        val alphaInt = (finalAlpha * 245f).toInt().coerceIn(0, 255)
        val haloAlphaInt = (finalAlpha * if (isDarkMap) 225f else 210f).toInt().coerceIn(0, 255)

        // Both the official Day Atlas (#2F90B0 ocean / forest-green terrain) and Tactical Night map
        // deliver maximum cartographic legibility with luminous crisp fills over a dark charcoal halo.
        val useLightHalo = !isDarkMap && !hasRasterImagery
        haloPaint.color = if (useLightHalo) {
            android.graphics.Color.argb(haloAlphaInt, 252, 252, 255)
        } else {
            android.graphics.Color.argb(haloAlphaInt, 8, 12, 22)
        }

        val fillColor = when (lm.kind) {
            LandmarkKind.REGION -> {
                val mapPalette = if (useLightHalo) LightLeonidaColors else DarkLeonidaColors
                lm.region?.accent(mapPalette) ?: mapPalette.cyan
            }
            LandmarkKind.COUNTY -> if (useLightHalo) Color(0xFF474066) else Color(0xFFE0DCF5)
            LandmarkKind.WATER -> if (useLightHalo) Color(0xFF0A668A) else Color(0xFF9DF0FF)
            LandmarkKind.AIRPORT -> if (useLightHalo) Color(0xFF9C5B00) else Color(0xFFFFDA78)
            LandmarkKind.ISLAND -> if (useLightHalo) Color(0xFF0A7354) else Color(0xFF8CFFE0)
            else -> if (useLightHalo) Color(0xFF1A1530) else Color(0xFFFFFFFF)
        }

        textPaint.color = android.graphics.Color.argb(
            alphaInt,
            (fillColor.red * 255).toInt(),
            (fillColor.green * 255).toInt(),
            (fillColor.blue * 255).toInt()
        )

        // Small crisp anchor dot for zoomed-in POIs (neighborhoods, landmarks, airports)
        if (lm.kind != LandmarkKind.REGION && lm.kind != LandmarkKind.COUNTY) {
            val dotCenter = Offset(pt.x, pt.y + 4.dp.toPx())
            drawCircle(
                color = if (useLightHalo) Color(0xE8FFFFFF) else Color(0xD0080A14),
                radius = 3.2.dp.toPx(),
                center = dotCenter,
                alpha = finalAlpha
            )
            drawCircle(
                color = fillColor,
                radius = 2.0.dp.toPx(),
                center = dotCenter,
                alpha = finalAlpha
            )
        }

        val textY = if (lm.kind == LandmarkKind.REGION || lm.kind == LandmarkKind.COUNTY) {
            pt.y
        } else {
            pt.y - 3.dp.toPx()
        }
        drawContext.canvas.nativeCanvas.drawText(displayText, pt.x, textY, haloPaint)
        drawContext.canvas.nativeCanvas.drawText(displayText, pt.x, textY, textPaint)
    }
}

private fun DrawScope.drawSecretPins(
    secrets: List<SecretLocation>,
    foundIds: Set<String>,
    currentChapter: Int,
    focusedSecret: SecretLocation?,
    pulseFactor: Float,
    scale: Float,
    toScreen: (Float, Float) -> Offset,
    colors: LeonidaColors
) {
    val labelPaint = Paint().apply {
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        textSize = 10.5f * density
        color = android.graphics.Color.argb(
            255,
            (colors.textPrimary.red * 255).toInt(),
            (colors.textPrimary.green * 255).toInt(),
            (colors.textPrimary.blue * 255).toInt()
        )
    }

    // Draw non-focused pins first so the focused pin always renders cleanly on top
    val ordered = secrets.sortedBy { if (it.id == focusedSecret?.id) 1 else 0 }
    ordered.forEach { loc ->
        val pos = toScreen(loc.mapX, loc.mapY)
        if (pos.x < -40f || pos.x > size.width + 40f || pos.y < -40f || pos.y > size.height + 40f) {
            return@forEach
        }
        val isFound = loc.id in foundIds
        val isFocused = focusedSecret?.id == loc.id
        val isFutureChapter = loc.minChapter > currentChapter && currentChapter < 6
        val pinColor = if (isFound) colors.mint else loc.category.accent(colors)

        if (isFocused) {
            val ringRadius = 15.dp.toPx() + pulseFactor * 22.dp.toPx()
            drawCircle(
                color = pinColor.copy(alpha = (1f - pulseFactor) * 0.78f),
                radius = ringRadius,
                center = pos,
                style = Stroke(width = 2.4.dp.toPx())
            )
        }

        // Ground shadow for instant pop over both bright and dark map areas
        drawCircle(
            color = Color.Black.copy(alpha = 0.34f),
            radius = if (isFocused) 12.5.dp.toPx() else 9.5.dp.toPx(),
            center = Offset(pos.x, pos.y + 1.5.dp.toPx())
        )
        drawCircle(
            color = pinColor.copy(alpha = if (isFocused) 0.38f else 0.24f),
            radius = if (isFocused) 15.5.dp.toPx() else 11.dp.toPx(),
            center = pos
        )
        drawCircle(
            color = colors.mapPinCenter,
            radius = if (isFocused) 9.8.dp.toPx() else 7.6.dp.toPx(),
            center = pos
        )
        drawCircle(
            color = pinColor,
            radius = if (isFocused) 7.2.dp.toPx() else 5.4.dp.toPx(),
            center = pos
        )
        if (isFound) {
            drawCircle(
                color = Color.White,
                radius = 2.5.dp.toPx(),
                center = pos
            )
        } else if (isFutureChapter) {
            // Subtle gold ring dot indicating chapter-locked spoiler secret
            drawCircle(
                color = colors.gold,
                radius = 2.3.dp.toPx(),
                center = Offset(pos.x + 5.5.dp.toPx(), pos.y - 5.5.dp.toPx())
            )
        }

        // Compact pill callout beneath the focused pin (or all pins at very deep zoom >= 4.2x)
        if (isFocused || scale >= 4.2f) {
            val shortTitle = if (loc.title.length > 26) loc.title.take(24) + "…" else loc.title
            val textW = labelPaint.measureText(shortTitle)
            val padX = 7.dp.toPx()
            val pillW = textW + padX * 2f
            val pillH = 18.dp.toPx()
            val pillLeft = pos.x - pillW / 2f
            val pillTop = pos.y + (if (isFocused) 13.dp.toPx() else 10.dp.toPx())

            drawRoundRect(
                color = colors.mapControlBg.copy(alpha = 0.93f),
                topLeft = Offset(pillLeft, pillTop),
                size = Size(pillW, pillH),
                cornerRadius = CornerRadius(9.dp.toPx(), 9.dp.toPx())
            )
            drawRoundRect(
                color = pinColor.copy(alpha = 0.85f),
                topLeft = Offset(pillLeft, pillTop),
                size = Size(pillW, pillH),
                cornerRadius = CornerRadius(9.dp.toPx(), 9.dp.toPx()),
                style = Stroke(width = 1.dp.toPx())
            )
            drawContext.canvas.nativeCanvas.drawText(
                shortTitle,
                pos.x,
                pillTop + pillH * 0.72f,
                labelPaint
            )
        }
    }
}

@Composable
private fun MapQuickSearchOverlay(
    query: String,
    onQueryChange: (String) -> Unit,
    results: List<MapSearchResult>,
    foundSecretIds: Set<String>,
    onClose: () -> Unit,
    onSelectResult: (MapSearchResult) -> Unit
) {
    val colors = LeonidaTheme.colors
    val cardShape = RoundedCornerShape(16.dp)

    Surface(
        shape = cardShape,
        color = colors.surface.copy(alpha = 0.97f),
        border = BorderStroke(1.dp, colors.cyan.copy(alpha = 0.55f)),
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = colors.cyan,
                    modifier = Modifier.size(19.dp)
                )
                Box(modifier = Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(
                            text = "Buscar bairro, ilha ou segredo...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = colors.textPrimary),
                        cursorBrush = SolidColor(colors.cyan),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Fechar busca",
                    tint = colors.textMuted,
                    modifier = Modifier
                        .size(19.dp)
                        .clickable(onClick = onClose)
                )
            }

            if (query.isNotBlank()) {
                HorizontalDivider(color = colors.outlineSoft)
                if (results.isEmpty()) {
                    Text(
                        text = "Nenhum bairro, marco ou segredo encontrado para \"$query\".",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted,
                        modifier = Modifier.padding(14.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 230.dp)
                    ) {
                        items(results, key = { it.id }) { item ->
                            val isSecret = item is MapSearchResult.Secret
                            val isFound = item is MapSearchResult.Secret && item.location.id in foundSecretIds
                            val accent = when (item) {
                                is MapSearchResult.Secret -> if (isFound) colors.mint else item.location.category.accent(colors)
                                is MapSearchResult.Place -> item.region?.accent(colors) ?: colors.cyan
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectResult(item) }
                                    .padding(horizontal = 12.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(accent.copy(alpha = 0.16f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (item) {
                                            is MapSearchResult.Secret -> if (isFound) Icons.Rounded.CheckCircle else Icons.Rounded.Stars
                                            is MapSearchResult.Place -> landmarkIcon(item.landmark.kind)
                                        },
                                        contentDescription = null,
                                        tint = accent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = colors.textPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = item.subtitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = colors.textSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                VicePillBadge(
                                    text = if (isSecret) "Segredo" else "Local",
                                    color = accent
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MapToolPillButton(
    icon: ImageVector,
    label: String?,
    active: Boolean,
    contentDescription: String,
    onClick: () -> Unit
) {
    val colors = LeonidaTheme.colors
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .then(if (colors.isDark) Modifier else Modifier.shadow(3.dp, shape))
            .clip(shape)
            .background(
                if (active) colors.mapControlBg.copy(alpha = 0.96f) else colors.mapControlBg.copy(alpha = 0.88f)
            )
            .border(
                width = 1.dp,
                color = if (active) colors.cyan.copy(alpha = 0.72f) else colors.outline,
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = if (label != null) 9.dp else 8.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (active) colors.cyan else colors.textPrimary,
            modifier = Modifier.size(16.dp)
        )
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (active) colors.cyan else colors.textPrimary
            )
        }
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
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
    }
}
