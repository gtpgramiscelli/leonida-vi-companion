package com.leonida.gtavicompanion.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ViceDarkColorScheme = darkColorScheme(
    primary = ViceNeonPink,
    onPrimary = ViceTextPrimary,
    primaryContainer = ViceNeonPinkSoft,
    onPrimaryContainer = ViceTextPrimary,
    secondary = ViceOceanCyan,
    onSecondary = ViceMidnight,
    secondaryContainer = ViceOceanCyanSoft,
    onSecondaryContainer = ViceOceanCyan,
    tertiary = ViceSunsetGold,
    onTertiary = ViceMidnight,
    tertiaryContainer = ViceElevatedSurface,
    onTertiaryContainer = ViceSunsetGold,
    background = ViceMidnight,
    onBackground = ViceTextPrimary,
    surface = ViceDeepSurface,
    onSurface = ViceTextPrimary,
    surfaceVariant = ViceCardSurface,
    onSurfaceVariant = ViceTextSecondary,
    outline = ViceOutline,
    outlineVariant = ViceOutlineSoft,
    error = ViceDangerRed,
    onError = ViceTextPrimary,
    errorContainer = ViceDangerSoft,
    onErrorContainer = ViceDangerRed
)

@Composable
fun LeonidaGuideTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ViceDarkColorScheme,
        typography = Typography,
        content = content
    )
}
