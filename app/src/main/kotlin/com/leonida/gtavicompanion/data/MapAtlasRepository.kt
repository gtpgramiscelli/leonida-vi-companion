package com.leonida.gtavicompanion.data

import android.content.Context
import com.leonida.gtavicompanion.model.LandmarkKind
import com.leonida.gtavicompanion.model.LeonidaRegion
import com.leonida.gtavicompanion.model.MapAtlasManifest
import com.leonida.gtavicompanion.model.MapLandmark
import com.leonida.gtavicompanion.model.MapLayerAsset
import com.leonida.gtavicompanion.model.MapLayerStyle
import com.leonida.gtavicompanion.model.MapSearchResult
import com.leonida.gtavicompanion.model.SecretLocation
import java.text.Normalizer
import java.util.Locale
import kotlin.math.abs

data class MapFitBounds(
    val baseWidth: Float,
    val baseHeight: Float
)

data class ScaleBarSpec(
    val label: String,
    val barWidthPx: Float
)

/**
 * Pure-Kotlin viewport projection and cartographic telemetry math.
 * Guarantees pixel-accurate mapping between normalized [0..1] raster coordinates
 * and screen coordinates across all aspect ratios and zoom levels (1x..8x).
 */
object MapViewportMath {
    const val MIN_SCALE = 1.0f
    const val MAX_SCALE = 8.0f

    // Stylized Leonida (South Florida analog) coordinate span for HUD GPS display
    private const val LAT_NORTH = 27.65
    private const val LAT_SOUTH = 24.45
    private const val LON_WEST = -82.65
    private const val LON_EAST = -79.95

    // Exact GTA VI Leonida world span in meters (2560px * 6.4 m/px = 16,384 m)
    const val DEFAULT_WORLD_SPAN_METERS = 16_384f

    fun computeFitBounds(
        viewportW: Float,
        viewportH: Float,
        imageW: Int,
        imageH: Int
    ): MapFitBounds {
        if (viewportW <= 0f || viewportH <= 0f || imageW <= 0 || imageH <= 0) {
            return MapFitBounds(
                baseWidth = viewportW.coerceAtLeast(1f),
                baseHeight = viewportH.coerceAtLeast(1f)
            )
        }
        val imgAspect = imageW.toFloat() / imageH.toFloat()
        val viewAspect = viewportW / viewportH
        return if (viewAspect > imgAspect) {
            MapFitBounds(baseWidth = viewportH * imgAspect, baseHeight = viewportH)
        } else {
            MapFitBounds(baseWidth = viewportW, baseHeight = viewportW / imgAspect)
        }
    }

    fun clampPan(
        panX: Float,
        panY: Float,
        scale: Float,
        viewportW: Float,
        viewportH: Float,
        fit: MapFitBounds
    ): Pair<Float, Float> {
        val scaledW = fit.baseWidth * scale
        val scaledH = fit.baseHeight * scale
        val slackX = if (scale > 1.02f) viewportW * 0.14f else 0f
        val slackY = if (scale > 1.02f) viewportH * 0.14f else 0f
        val maxPanX = maxOf(0f, (scaledW - viewportW) / 2f) + slackX
        val maxPanY = maxOf(0f, (scaledH - viewportH) / 2f) + slackY
        return panX.coerceIn(-maxPanX, maxPanX) to panY.coerceIn(-maxPanY, maxPanY)
    }

    fun mapToScreen(
        nx: Float,
        ny: Float,
        scale: Float,
        panX: Float,
        panY: Float,
        viewportW: Float,
        viewportH: Float,
        fit: MapFitBounds
    ): Pair<Float, Float> {
        val mapW = fit.baseWidth * scale
        val mapH = fit.baseHeight * scale
        val mapLeft = (viewportW - mapW) / 2f + panX
        val mapTop = (viewportH - mapH) / 2f + panY
        return (mapLeft + nx * mapW) to (mapTop + ny * mapH)
    }

    fun screenToMap(
        sx: Float,
        sy: Float,
        scale: Float,
        panX: Float,
        panY: Float,
        viewportW: Float,
        viewportH: Float,
        fit: MapFitBounds
    ): Pair<Float, Float> {
        val mapW = (fit.baseWidth * scale).coerceAtLeast(1f)
        val mapH = (fit.baseHeight * scale).coerceAtLeast(1f)
        val mapLeft = (viewportW - mapW) / 2f + panX
        val mapTop = (viewportH - mapH) / 2f + panY
        return ((sx - mapLeft) / mapW) to ((sy - mapTop) / mapH)
    }

    fun targetPanForPoint(
        nx: Float,
        ny: Float,
        targetScale: Float,
        viewportW: Float,
        viewportH: Float,
        fit: MapFitBounds
    ): Pair<Float, Float> {
        val rawX = (0.5f - nx) * fit.baseWidth * targetScale
        val rawY = (0.5f - ny) * fit.baseHeight * targetScale
        return clampPan(rawX, rawY, targetScale, viewportW, viewportH, fit)
    }

    /**
     * Centroid-anchored pinch zoom + pan step so the map point directly beneath the user's fingers
     * remains anchored smoothly during zoom gestures.
     */
    fun applyTransformGesture(
        currentScale: Float,
        currentPanX: Float,
        currentPanY: Float,
        centroidX: Float,
        centroidY: Float,
        panDeltaX: Float,
        panDeltaY: Float,
        zoomFactor: Float,
        viewportW: Float,
        viewportH: Float,
        fit: MapFitBounds
    ): Triple<Float, Float, Float> {
        val nextScale = (currentScale * zoomFactor).coerceIn(MIN_SCALE, MAX_SCALE)
        val ratio = nextScale / currentScale.coerceAtLeast(0.01f)
        val cx = viewportW / 2f
        val cy = viewportH / 2f
        val rawPanX = (currentPanX + cx - centroidX) * ratio - (cx - centroidX) + panDeltaX
        val rawPanY = (currentPanY + cy - centroidY) * ratio - (cy - centroidY) + panDeltaY
        val (clampedX, clampedY) = clampPan(rawPanX, rawPanY, nextScale, viewportW, viewportH, fit)
        return Triple(nextScale, clampedX, clampedY)
    }

    fun formatGps(nx: Float, ny: Float): String {
        val cx = nx.coerceIn(0f, 1f).toDouble()
        val cy = ny.coerceIn(0f, 1f).toDouble()
        val lat = LAT_NORTH - cy * (LAT_NORTH - LAT_SOUTH)
        val lon = abs(LON_WEST + cx * (LON_EAST - LON_WEST))
        return String.format(Locale.US, "%.2f°N  %.2f°W", lat, lon)
    }

    fun computeScaleBar(
        scale: Float,
        fit: MapFitBounds,
        density: Float,
        worldSpanMeters: Float = DEFAULT_WORLD_SPAN_METERS
    ): ScaleBarSpec {
        val mapPxWidth = (fit.baseWidth * scale).coerceAtLeast(1f)
        val metersPerPx = worldSpanMeters / mapPxWidth
        val targetWidthPx = 76f * density
        val rawMeters = metersPerPx * targetWidthPx
        val niceSteps = floatArrayOf(50f, 100f, 200f, 500f, 1000f, 2000f, 3000f, 5000f)
        val chosen = niceSteps.minByOrNull { abs(it - rawMeters) } ?: 1000f
        val barPx = (chosen / metersPerPx).coerceIn(36f * density, 118f * density)
        val label = if (chosen >= 1000f) {
            val km = chosen / 1000f
            if (km % 1f == 0f) "${km.toInt()} km" else String.format(Locale.US, "%.1f km", km)
        } else {
            "${chosen.toInt()} m"
        }
        return ScaleBarSpec(label = label, barWidthPx = barPx)
    }

    /**
     * Progressive label visibility alpha for zoom levels [1x..8x].
     * Landmarks anchored at the base overview zoom (`minZoom <= 1.01f`) are immediately opaque at 1.0x
     * so all 6 regions and 5 counties render cleanly on initial view.
     */
    fun computeLandmarkAlpha(
        scale: Float,
        minZoom: Float,
        maxZoom: Float?,
        isFocused: Boolean
    ): Float {
        if (isFocused) return 1f
        if (scale < minZoom) return 0f
        if (maxZoom != null && scale > maxZoom) return 0f

        val fadeIn = if (minZoom <= MIN_SCALE + 0.01f) {
            1f
        } else {
            ((scale - minZoom) / 0.35f).coerceIn(0f, 1f)
        }
        val fadeOut = if (maxZoom != null) {
            ((maxZoom - scale) / 0.50f).coerceIn(0f, 1f)
        } else {
            1f
        }
        return (fadeIn * fadeOut).coerceIn(0f, 1f)
    }
}

object MapSearchEngine {

    fun normalizeQuery(text: String): String {
        if (text.isBlank()) return ""
        val decomposed = Normalizer.normalize(text, Normalizer.Form.NFD)
        return buildString(decomposed.length) {
            for (ch in decomposed) {
                if (Character.getType(ch) != Character.NON_SPACING_MARK.toInt()) {
                    append(ch.lowercaseChar())
                }
            }
        }.trim()
    }

    fun search(
        query: String,
        secrets: List<SecretLocation>,
        landmarks: List<MapLandmark>,
        maxResults: Int = 12
    ): List<MapSearchResult> {
        val q = normalizeQuery(query)
        if (q.isEmpty()) return emptyList()

        val scored = mutableListOf<Pair<Int, MapSearchResult>>()

        secrets.forEach { loc ->
            val titleNorm = normalizeQuery(loc.title)
            val subNorm = normalizeQuery(loc.subtitle)
            val areaNorm = normalizeQuery(loc.subArea)
            val regNorm = normalizeQuery(loc.region.displayName)
            val countyNorm = normalizeQuery(loc.region.county)
            val score = when {
                titleNorm.startsWith(q) -> 100
                titleNorm.contains(q) -> 85
                areaNorm.startsWith(q) -> 78
                areaNorm.contains(q) || subNorm.contains(q) -> 68
                regNorm.contains(q) || countyNorm.contains(q) -> 52
                else -> 0
            }
            if (score > 0) {
                scored += score to MapSearchResult.Secret(loc)
            }
        }

        landmarks.forEach { lm ->
            val nameNorm = normalizeQuery(lm.name)
            val countyNorm = normalizeQuery(lm.county.orEmpty())
            val regNorm = normalizeQuery(lm.region?.displayName.orEmpty())
            val subNorm = normalizeQuery(lm.subtitle)
            val kindNorm = normalizeQuery(lm.kind.label)
            val score = when {
                nameNorm.startsWith(q) -> 96
                nameNorm.contains(q) -> 82
                countyNorm.startsWith(q) -> 70
                countyNorm.contains(q) || regNorm.contains(q) || subNorm.contains(q) || kindNorm.contains(q) -> 56
                else -> 0
            }
            if (score > 0) {
                scored += score to MapSearchResult.Place(lm)
            }
        }

        return scored
            .sortedWith(compareByDescending<Pair<Int, MapSearchResult>> { it.first }.thenBy { it.second.title })
            .take(maxResults)
            .map { it.second }
    }
}

/**
 * Loads the offline real Leonida map manifest (`assets/map/leonida_pois.json`) and raster layers,
 * calibrates secret coordinates against the real map image, and provides fallback metadata for previews/tests.
 */
object MapAtlasRepository {

    const val MAP_ASSET_DIR = "map"
    const val MANIFEST_ASSET_PATH = "map/leonida_pois.json"

    @Volatile
    private var cachedManifest: MapAtlasManifest? = null

    fun loadFromAssets(context: Context): MapAtlasManifest {
        cachedManifest?.let { return it }

        val assetFiles = runCatching {
            context.assets.list(MAP_ASSET_DIR)?.toSet().orEmpty()
        }.getOrDefault(emptySet())

        val jsonText = runCatching {
            context.assets.open(MANIFEST_ASSET_PATH).bufferedReader(Charsets.UTF_8).use { it.readText() }
        }.getOrNull()

        val manifest = if (!jsonText.isNullOrBlank()) {
            parseManifestJson(jsonText, availableAssetFiles = assetFiles)
        } else {
            defaultFallbackManifest(availableAssetFiles = assetFiles)
        }

        cachedManifest = manifest
        return manifest
    }

    /**
     * Merges calibrated coordinates from [manifest] onto [baseSecrets] without altering metadata or IDs.
     */
    fun calibrateSecrets(
        baseSecrets: List<SecretLocation>,
        manifest: MapAtlasManifest
    ): List<SecretLocation> {
        if (manifest.secretCoordinates.isEmpty()) return baseSecrets
        return baseSecrets.map { secret ->
            val calibrated = manifest.secretCoordinates[secret.id]
            if (calibrated != null) {
                secret.copy(
                    mapX = calibrated.first.coerceIn(0f, 1f),
                    mapY = calibrated.second.coerceIn(0f, 1f)
                )
            } else {
                secret
            }
        }
    }

    /**
     * Computes a focus coordinate + zoom level for a given [LeonidaRegion] based on calibrated landmarks.
     */
    fun regionFocusTarget(
        region: LeonidaRegion,
        manifest: MapAtlasManifest,
        secrets: List<SecretLocation>
    ): Triple<Float, Float, Float> {
        val regionLandmark = manifest.landmarks.firstOrNull {
            it.kind == LandmarkKind.REGION && it.region == region
        }
        if (regionLandmark != null) {
            val zoom = regionLandmark.defaultZoom ?: when (region) {
                LeonidaRegion.VICE_CITY -> 2.35f
                LeonidaRegion.LEONIDA_KEYS -> 2.40f
                LeonidaRegion.PORT_GELLHORN -> 2.35f
                LeonidaRegion.AMBROSIA -> 2.35f
                LeonidaRegion.GRASSRIVERS -> 2.40f
                LeonidaRegion.MOUNT_KALAGA -> 2.30f
            }
            return Triple(regionLandmark.mapX, regionLandmark.mapY, zoom)
        }
        val pts = secrets.filter { it.region == region }
        if (pts.isNotEmpty()) {
            val avgX = pts.map { it.mapX }.average().toFloat()
            val avgY = pts.map { it.mapY }.average().toFloat()
            return Triple(avgX, avgY, 2.35f)
        }
        return Triple(0.5f, 0.5f, 1.8f)
    }

    /**
     * Pure-Kotlin JSON parser for `leonida_pois.json` — runs identically on Android and JVM unit tests.
     */
    fun parseManifestJson(
        rawJson: String,
        availableAssetFiles: Set<String> = emptySet()
    ): MapAtlasManifest {
        val root = SimpleJsonParser.parseObject(rawJson)
            ?: return defaultFallbackManifest(availableAssetFiles)

        val imageObj = root.obj("image") ?: root.obj("map") ?: root.obj("metadata")
        val sourceObj = root.obj("source")
        val width = root.int("width")
            ?: root.int("imageWidth")
            ?: imageObj?.int("width")
            ?: 2560
        val height = root.int("height")
            ?: root.int("imageHeight")
            ?: imageObj?.int("height")
            ?: 3200
        val metersPerPixel = root.float("metersPerPixel")
            ?: root.float("meters_per_pixel")
            ?: 6.4f

        val attribution = sourceObj?.str("attribution")
            ?: root.str("attribution")
            ?: root.str("source")
            ?: sourceObj?.str("name")
            ?: "GTADB Official GTA VI Map (CC BY 4.0) — map.gtadb.org"

        // 1. Parse layers from JSON + auto-discover known filenames in assets/map/
        val parsedLayers = mutableListOf<MapLayerAsset>()
        val layersNode = root["layers"]
        when (layersNode) {
            is Map<*, *> -> {
                layersNode.forEach { (k, v) ->
                    val style = parseLayerStyle(k.toString())
                    val path = when (v) {
                        is String -> normalizeAssetPath(v)
                        is Map<*, *> -> {
                            @Suppress("UNCHECKED_CAST")
                            val m = v as Map<String, Any?>
                            val file = m.str("assetPath") ?: m.str("file") ?: m.str("path") ?: m.str("asset")
                            file?.let(::normalizeAssetPath)
                        }
                        else -> null
                    }
                    if (style != null && path != null && parsedLayers.none { it.style == style }) {
                        parsedLayers += MapLayerAsset(style = style, assetPath = path)
                    }
                }
            }
            is List<*> -> {
                layersNode.filterIsInstance<Map<*, *>>().forEach { item ->
                    @Suppress("UNCHECKED_CAST")
                    val m = item as Map<String, Any?>
                    val style = parseLayerStyle(
                        m.str("id") ?: m.str("style") ?: m.str("type") ?: m.str("name").orEmpty()
                    )
                    val file = m.str("assetPath") ?: m.str("file") ?: m.str("path") ?: m.str("asset")
                    if (style != null && !file.isNullOrBlank() && parsedLayers.none { it.style == style }) {
                        val themeHint = m.str("themeHint")?.lowercase(Locale.ROOT)
                        parsedLayers += MapLayerAsset(
                            style = style,
                            assetPath = normalizeAssetPath(file),
                            displayName = m.str("name") ?: style.label,
                            oceanColorHex = m.str("oceanColorHex"),
                            isDarkLayer = themeHint == "dark" || style == MapLayerStyle.DARK_ATLAS
                        )
                    }
                }
            }
        }
        val finalLayers = discoverLayers(parsedLayers, availableAssetFiles)

        // 2. Parse calibrated secret coordinates (supports both array of objects and id -> {x,y} map)
        val secretCoords = linkedMapOf<String, Pair<Float, Float>>()
        val secretsNode = root["secrets"] ?: root["secretLocations"] ?: root["secret_locations"]
        when (secretsNode) {
            is List<*> -> {
                secretsNode.filterIsInstance<Map<*, *>>().forEach { raw ->
                    @Suppress("UNCHECKED_CAST")
                    val m = raw as Map<String, Any?>
                    val id = m.str("id") ?: return@forEach
                    val x = m.float("x") ?: m.float("mapX") ?: m.float("nx") ?: return@forEach
                    val y = m.float("y") ?: m.float("mapY") ?: m.float("ny") ?: return@forEach
                    secretCoords[id] = x.coerceIn(0f, 1f) to y.coerceIn(0f, 1f)
                }
            }
            is Map<*, *> -> {
                secretsNode.forEach { (k, v) ->
                    val id = k.toString()
                    if (v is Map<*, *>) {
                        @Suppress("UNCHECKED_CAST")
                        val m = v as Map<String, Any?>
                        val x = m.float("x") ?: m.float("mapX") ?: m.float("nx")
                        val y = m.float("y") ?: m.float("mapY") ?: m.float("ny")
                        if (x != null && y != null) {
                            secretCoords[id] = x.coerceIn(0f, 1f) to y.coerceIn(0f, 1f)
                        }
                    }
                }
            }
        }

        // 3. Parse landmarks / regions / counties / neighborhoods / POIs
        val landmarks = mutableListOf<MapLandmark>()
        val seenIds = mutableSetOf<String>()

        fun addLandmarkList(node: Any?, defaultKind: LandmarkKind, defaultMinZoom: Float) {
            val list = node as? List<*> ?: return
            list.filterIsInstance<Map<*, *>>().forEachIndexed { idx, raw ->
                @Suppress("UNCHECKED_CAST")
                val m = raw as Map<String, Any?>
                val name = m.str("name") ?: m.str("label") ?: m.str("title") ?: return@forEachIndexed
                val x = m.float("x") ?: m.float("mapX") ?: m.float("nx") ?: return@forEachIndexed
                val y = m.float("y") ?: m.float("mapY") ?: m.float("ny") ?: return@forEachIndexed
                val rawId = m.str("id") ?: "${defaultKind.name.lowercase()}_${idx}_${name.lowercase().replace(Regex("[^a-z0-9]+"), "_")}"
                if (!seenIds.add(rawId)) return@forEachIndexed

                val kind = parseLandmarkKind(m.str("kind") ?: m.str("type") ?: m.str("category")) ?: defaultKind
                val region = parseRegion(
                    m.str("region") ?: m.str("regionId") ?: if (kind == LandmarkKind.REGION) m.str("id") ?: name else null
                )
                val county = m.str("county") ?: region?.county
                val minZoom = m.float("minZoom") ?: m.float("min_zoom") ?: defaultMinZoom
                val maxZoom = m.float("maxZoom") ?: m.float("max_zoom")
                val defaultZoom = m.float("defaultZoom") ?: m.float("default_zoom")
                val subtitle = m.str("subtitle") ?: m.str("description") ?: ""

                landmarks += MapLandmark(
                    id = rawId,
                    name = name,
                    region = region,
                    county = county,
                    kind = kind,
                    mapX = x.coerceIn(0f, 1f),
                    mapY = y.coerceIn(0f, 1f),
                    minZoom = minZoom.coerceAtLeast(1.0f),
                    maxZoom = maxZoom,
                    defaultZoom = defaultZoom,
                    subtitle = subtitle
                )
            }
        }

        addLandmarkList(root["regions"], LandmarkKind.REGION, 1.0f)
        addLandmarkList(root["counties"], LandmarkKind.COUNTY, 1.0f)
        addLandmarkList(root["landmarks"], LandmarkKind.LANDMARK, 1.8f)
        addLandmarkList(root["places"], LandmarkKind.NEIGHBORHOOD, 1.9f)
        addLandmarkList(root["pois"], LandmarkKind.LANDMARK, 1.9f)
        addLandmarkList(root["neighborhoods"], LandmarkKind.NEIGHBORHOOD, 2.0f)
        addLandmarkList(root["districts"], LandmarkKind.NEIGHBORHOOD, 2.0f)

        return MapAtlasManifest(
            imageWidth = width.coerceAtLeast(256),
            imageHeight = height.coerceAtLeast(256),
            metersPerPixel = metersPerPixel.coerceAtLeast(0.5f),
            sourceAttribution = attribution,
            layers = finalLayers,
            secretCoordinates = secretCoords,
            landmarks = landmarks
        )
    }

    private fun discoverLayers(
        fromJson: List<MapLayerAsset>,
        availableAssetFiles: Set<String>
    ): List<MapLayerAsset> {
        val result = mutableListOf<MapLayerAsset>()
        // Keep JSON-declared layers if asset directory isn't enumerated (unit tests) or file exists
        fromJson.forEach { layer ->
            val fileName = layer.assetPath.substringAfterLast('/')
            if (availableAssetFiles.isEmpty() || fileName in availableAssetFiles) {
                result += layer
            }
        }
        // Auto-discover standard filenames in assets/map/
        val candidates = listOf(
            MapLayerStyle.ATLAS to listOf("leonida_atlas.webp", "leonida_atlas.png", "leonida_atlas.jpg", "atlas.webp", "atlas.png"),
            MapLayerStyle.DARK_ATLAS to listOf("leonida_night.webp", "leonida_atlas_dark.webp", "leonida_dark.webp", "leonida_night.png"),
            MapLayerStyle.SATELLITE to listOf("leonida_satellite.webp", "leonida_satellite.png", "leonida_satellite.jpg", "satellite.webp", "satellite.png")
        )
        candidates.forEach { (style, names) ->
            if (result.none { it.style == style }) {
                val match = names.firstOrNull { it in availableAssetFiles }
                if (match != null) {
                    result += MapLayerAsset(style = style, assetPath = "$MAP_ASSET_DIR/$match")
                }
            }
        }
        return result
    }

    private fun normalizeAssetPath(path: String): String {
        val clean = path.trim().removePrefix("/").removePrefix("assets/")
        return if (clean.startsWith("$MAP_ASSET_DIR/")) clean else "$MAP_ASSET_DIR/$clean"
    }

    private fun parseLayerStyle(raw: String): MapLayerStyle? {
        val norm = raw.trim().lowercase(Locale.ROOT)
        return when {
            "dark" in norm || "night" in norm || "noite" in norm || "noturn" in norm || "tatic" in norm -> MapLayerStyle.DARK_ATLAS
            "sat" in norm || "terrain" in norm || "terreno" in norm || "aerial" in norm -> MapLayerStyle.SATELLITE
            "atlas" in norm || "day" in norm || "dia" in norm || "street" in norm || "road" in norm || "default" in norm || "main" in norm -> MapLayerStyle.ATLAS
            else -> null
        }
    }

    private fun parseLandmarkKind(raw: String?): LandmarkKind? {
        val norm = raw?.trim()?.lowercase(Locale.ROOT) ?: return null
        return when {
            "region" in norm || "regiao" in norm || "região" in norm -> LandmarkKind.REGION
            "county" in norm || "condado" in norm -> LandmarkKind.COUNTY
            "town" in norm || "city" in norm || "cidade" in norm -> LandmarkKind.TOWN
            "airport" in norm || "aeroporto" in norm || "airbase" in norm || "airfield" in norm -> LandmarkKind.AIRPORT
            "island" in norm || "ilha" in norm || "key" in norm -> LandmarkKind.ISLAND
            "water" in norm || "lake" in norm || "lago" in norm || "river" in norm || "bay" in norm || "marsh" in norm || "swamp" in norm -> LandmarkKind.WATER
            "highway" in norm || "road" in norm || "bridge" in norm || "rodovia" in norm -> LandmarkKind.HIGHWAY
            "neighborhood" in norm || "district" in norm || "bairro" in norm -> LandmarkKind.NEIGHBORHOOD
            "landmark" in norm || "marco" in norm || "poi" in norm || "port" in norm || "raceway" in norm || "park" in norm -> LandmarkKind.LANDMARK
            else -> null
        }
    }

    private fun parseRegion(raw: String?): LeonidaRegion? {
        val trimmed = raw?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        LeonidaRegion.entries.firstOrNull { it.name.equals(trimmed, ignoreCase = true) }?.let { return it }
        val norm = trimmed.lowercase(Locale.ROOT)
        return when {
            "vice" in norm -> LeonidaRegion.VICE_CITY
            "key" in norm -> LeonidaRegion.LEONIDA_KEYS
            "grass" in norm || "everglade" in norm -> LeonidaRegion.GRASSRIVERS
            "gellhorn" in norm || "kelly" in norm -> LeonidaRegion.PORT_GELLHORN
            "ambrosia" in norm || "leonard" in norm -> LeonidaRegion.AMBROSIA
            "kalaga" in norm || "lummox" in norm -> LeonidaRegion.MOUNT_KALAGA
            else -> null
        }
    }

    fun defaultFallbackManifest(availableAssetFiles: Set<String> = emptySet()): MapAtlasManifest {
        val fallbackLandmarks = listOf(
            MapLandmark("MOUNT_KALAGA", "Mount Kalaga", LeonidaRegion.MOUNT_KALAGA, "Lummox County", LandmarkKind.REGION, 0.48120f, 0.21025f, 1.0f, 3.0f, 2.3f),
            MapLandmark("AMBROSIA", "Ambrosia", LeonidaRegion.AMBROSIA, "Leonard County", LandmarkKind.REGION, 0.54407f, 0.33721f, 1.0f, 3.0f, 2.3f),
            MapLandmark("PORT_GELLHORN", "Port Gellhorn", LeonidaRegion.PORT_GELLHORN, "Kelly County", LandmarkKind.REGION, 0.29993f, 0.38604f, 1.0f, 3.0f, 2.3f),
            MapLandmark("GRASSRIVERS", "Grassrivers", LeonidaRegion.GRASSRIVERS, "Mariana County", LandmarkKind.REGION, 0.46899f, 0.73760f, 1.0f, 3.0f, 2.4f),
            MapLandmark("VICE_CITY", "Vice City", LeonidaRegion.VICE_CITY, "Vice-Dale County", LandmarkKind.REGION, 0.67834f, 0.51445f, 1.0f, 3.0f, 2.2f),
            MapLandmark("LEONIDA_KEYS", "Leonida Keys", LeonidaRegion.LEONIDA_KEYS, "Mariana County", LandmarkKind.REGION, 0.43420f, 0.85723f, 1.0f, 3.0f, 2.4f)
        )
        return MapAtlasManifest(
            imageWidth = 2560,
            imageHeight = 3200,
            metersPerPixel = 6.4f,
            sourceAttribution = "GTADB Official GTA VI Map (CC BY 4.0) — map.gtadb.org",
            layers = discoverLayers(emptyList(), availableAssetFiles),
            secretCoordinates = emptyMap(),
            landmarks = fallbackLandmarks
        )
    }
}

// =========================================================================
// Minimal Pure-Kotlin JSON Parser (Zero Android / External Dependencies)
// =========================================================================

private fun Map<String, Any?>.str(key: String): String? =
    (this[key] as? String)?.trim()?.takeIf { it.isNotEmpty() }

private fun Map<String, Any?>.int(key: String): Int? =
    (this[key] as? Number)?.toInt()

private fun Map<String, Any?>.float(key: String): Float? =
    (this[key] as? Number)?.toFloat()

@Suppress("UNCHECKED_CAST")
private fun Map<String, Any?>.obj(key: String): Map<String, Any?>? =
    this[key] as? Map<String, Any?>

private class SimpleJsonParser(private val src: String) {
    private var pos = 0

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun parseObject(json: String): Map<String, Any?>? {
            return runCatching {
                val parser = SimpleJsonParser(json)
                parser.skipWhitespace()
                parser.parseValue() as? Map<String, Any?>
            }.getOrNull()
        }
    }

    private fun parseValue(): Any? {
        skipWhitespace()
        if (pos >= src.length) return null
        return when (src[pos]) {
            '{' -> parseObj()
            '[' -> parseArray()
            '"' -> parseString()
            't' -> { pos += 4; true }
            'f' -> { pos += 5; false }
            'n' -> { pos += 4; null }
            else -> parseNumber()
        }
    }

    private fun parseObj(): Map<String, Any?> {
        val map = linkedMapOf<String, Any?>()
        pos++ // '{'
        skipWhitespace()
        if (pos < src.length && src[pos] == '}') {
            pos++
            return map
        }
        while (pos < src.length) {
            skipWhitespace()
            val key = parseString()
            skipWhitespace()
            if (pos < src.length && src[pos] == ':') pos++
            val value = parseValue()
            map[key] = value
            skipWhitespace()
            if (pos < src.length && src[pos] == ',') {
                pos++
            } else {
                if (pos < src.length && src[pos] == '}') pos++
                break
            }
        }
        return map
    }

    private fun parseArray(): List<Any?> {
        val list = mutableListOf<Any?>()
        pos++ // '['
        skipWhitespace()
        if (pos < src.length && src[pos] == ']') {
            pos++
            return list
        }
        while (pos < src.length) {
            list += parseValue()
            skipWhitespace()
            if (pos < src.length && src[pos] == ',') {
                pos++
            } else {
                if (pos < src.length && src[pos] == ']') pos++
                break
            }
        }
        return list
    }

    private fun parseString(): String {
        val sb = StringBuilder()
        pos++ // opening '"'
        while (pos < src.length) {
            val c = src[pos++]
            if (c == '"') break
            if (c == '\\' && pos < src.length) {
                when (val esc = src[pos++]) {
                    '"' -> sb.append('"')
                    '\\' -> sb.append('\\')
                    '/' -> sb.append('/')
                    'b' -> sb.append('\b')
                    'f' -> sb.append('\u000C')
                    'n' -> sb.append('\n')
                    'r' -> sb.append('\r')
                    't' -> sb.append('\t')
                    'u' -> {
                        if (pos + 4 <= src.length) {
                            val hex = src.substring(pos, pos + 4)
                            sb.append(hex.toInt(16).toChar())
                            pos += 4
                        }
                    }
                    else -> sb.append(esc)
                }
            } else {
                sb.append(c)
            }
        }
        return sb.toString()
    }

    private fun parseNumber(): Number {
        val start = pos
        while (pos < src.length && src[pos] in "-+0123456789.eE") {
            pos++
        }
        val token = src.substring(start, pos)
        return if ('.' in token || 'e' in token || 'E' in token) {
            token.toDouble()
        } else {
            token.toLongOrNull() ?: token.toDouble()
        }
    }

    private fun skipWhitespace() {
        while (pos < src.length && src[pos] <= ' ') {
            pos++
        }
    }
}
