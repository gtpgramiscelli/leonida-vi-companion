package com.leonida.gtavicompanion.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalLeonidaColors = staticCompositionLocalOf { DarkLeonidaColors }

object LeonidaTheme {
    val colors: LeonidaColors
        @Composable
        @ReadOnlyComposable
        get() = LocalLeonidaColors.current
}

private val ViceDarkColorScheme = darkColorScheme(
    primary = DarkLeonidaColors.pink,
    onPrimary = Color.White,
    primaryContainer = DarkLeonidaColors.pinkSoft,
    onPrimaryContainer = DarkLeonidaColors.textPrimary,
    secondary = DarkLeonidaColors.cyan,
    onSecondary = DarkLeonidaColors.background,
    secondaryContainer = DarkLeonidaColors.cyanSoft,
    onSecondaryContainer = DarkLeonidaColors.cyan,
    tertiary = DarkLeonidaColors.gold,
    onTertiary = DarkLeonidaColors.background,
    tertiaryContainer = DarkLeonidaColors.cardElevated,
    onTertiaryContainer = DarkLeonidaColors.gold,
    background = DarkLeonidaColors.background,
    onBackground = DarkLeonidaColors.textPrimary,
    surface = DarkLeonidaColors.surface,
    onSurface = DarkLeonidaColors.textPrimary,
    surfaceVariant = DarkLeonidaColors.card,
    onSurfaceVariant = DarkLeonidaColors.textSecondary,
    outline = DarkLeonidaColors.outline,
    outlineVariant = DarkLeonidaColors.outlineSoft,
    error = DarkLeonidaColors.danger,
    onError = Color.White,
    errorContainer = DarkLeonidaColors.dangerSoft,
    onErrorContainer = DarkLeonidaColors.danger
)

private val ViceLightColorScheme = lightColorScheme(
    primary = LightLeonidaColors.pink,
    onPrimary = Color.White,
    primaryContainer = LightLeonidaColors.pinkSoft,
    onPrimaryContainer = LightLeonidaColors.textPrimary,
    secondary = LightLeonidaColors.cyan,
    onSecondary = Color.White,
    secondaryContainer = LightLeonidaColors.cyanSoft,
    onSecondaryContainer = LightLeonidaColors.cyan,
    tertiary = LightLeonidaColors.gold,
    onTertiary = Color.White,
    tertiaryContainer = LightLeonidaColors.cardElevated,
    onTertiaryContainer = LightLeonidaColors.gold,
    background = LightLeonidaColors.background,
    onBackground = LightLeonidaColors.textPrimary,
    surface = LightLeonidaColors.surface,
    onSurface = LightLeonidaColors.textPrimary,
    surfaceVariant = LightLeonidaColors.card,
    onSurfaceVariant = LightLeonidaColors.textSecondary,
    outline = LightLeonidaColors.outline,
    outlineVariant = LightLeonidaColors.outlineSoft,
    error = LightLeonidaColors.danger,
    onError = Color.White,
    errorContainer = LightLeonidaColors.dangerSoft,
    onErrorContainer = LightLeonidaColors.danger
)

@Composable
fun LeonidaGuideTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val leonidaColors = if (darkTheme) DarkLeonidaColors else LightLeonidaColors
    val colorScheme = if (darkTheme) ViceDarkColorScheme else ViceLightColorScheme

    CompositionLocalProvider(LocalLeonidaColors provides leonidaColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
