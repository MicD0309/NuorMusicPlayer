package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

val LocalAuraColors = staticCompositionLocalOf { AuraPalettes.ConvxDark }
val LocalAuraThemeStyle = staticCompositionLocalOf { AuraThemeStyle.CONVX_DARK }

@Composable
fun AuraMusicTheme(
    themeStyle: AuraThemeStyle = AuraThemeStyle.CONVX_DARK,
    content: @Composable () -> Unit
) {
    val auraColors = AuraPalettes.getColors(themeStyle)

    val colorScheme = if (themeStyle.isDark) {
        darkColorScheme(
            primary = auraColors.primaryAccent,
            secondary = auraColors.secondaryAccent,
            background = auraColors.backgroundStart,
            surface = auraColors.surfaceGlass,
            onPrimary = auraColors.onPrimaryAccent,
            onSecondary = auraColors.textPrimary,
            onBackground = auraColors.textPrimary,
            onSurface = auraColors.textPrimary,
            surfaceVariant = auraColors.chipBackground,
            onSurfaceVariant = auraColors.textSecondary
        )
    } else {
        lightColorScheme(
            primary = auraColors.primaryAccent,
            secondary = auraColors.secondaryAccent,
            background = auraColors.backgroundStart,
            surface = auraColors.surfaceGlass,
            onPrimary = auraColors.onPrimaryAccent,
            onSecondary = auraColors.textPrimary,
            onBackground = auraColors.textPrimary,
            onSurface = auraColors.textPrimary,
            surfaceVariant = auraColors.chipBackground,
            onSurfaceVariant = auraColors.textSecondary
        )
    }

    CompositionLocalProvider(
        LocalAuraColors provides auraColors,
        LocalAuraThemeStyle provides themeStyle
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
