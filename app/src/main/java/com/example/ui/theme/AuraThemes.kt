package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

enum class AuraThemeStyle(
    val title: String,
    val subtitle: String,
    val isDark: Boolean
) {
    CONVX_DARK("CONVX Dark", "Obsidian & Liquid Frosted Glass (B&W)", true)
}

@Immutable
data class AuraColors(
    val backgroundStart: Color,
    val backgroundEnd: Color,
    val backgroundGlow: Color,
    val surfaceGlass: Color,
    val surfaceGlassBorder: Color,
    val glassHighlight: Color,
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val onPrimaryAccent: Color = Color(0xFF101012),
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val hiResBadge: Color,
    val hiResBadgeBg: Color,
    val vinylGlow: Color,
    val scrubberActive: Color,
    val scrubberTrack: Color,
    val cardBackground: Color,
    val chipBackground: Color = Color(0x26FFFFFF)
)

object AuraPalettes {
    val ConvxDark = AuraColors(
        backgroundStart = Color(0xFF0C0C0E),
        backgroundEnd = Color(0xFF050507),
        backgroundGlow = Color(0x26FFFFFF),
        surfaceGlass = Color(0x1AFFFFFF),
        surfaceGlassBorder = Color(0x40FFFFFF),
        glassHighlight = Color(0x33FFFFFF),
        primaryAccent = Color(0xFFFFFFFF),
        secondaryAccent = Color(0xFFE5E5EA),
        onPrimaryAccent = Color(0xFF101012),
        textPrimary = Color(0xFFFFFFFF),
        textSecondary = Color(0xFFA1A1A8),
        textMuted = Color(0xFF6E6E75),
        hiResBadge = Color(0xFFFFFFFF),
        hiResBadgeBg = Color(0x22FFFFFF),
        vinylGlow = Color(0x33FFFFFF),
        scrubberActive = Color(0xFFFFFFFF),
        scrubberTrack = Color(0x2EFFFFFF),
        cardBackground = Color(0x18FFFFFF),
        chipBackground = Color(0x22FFFFFF)
    )

    fun getColors(themeStyle: AuraThemeStyle): AuraColors {
        return ConvxDark
    }
}
