package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Equalizer
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.NavigationTab
import com.example.ui.theme.LocalAuraColors

@Composable
fun MinimalistNavBar(
    currentTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val auraColors = LocalAuraColors.current
    val visibleTabs = listOf(
        NavigationTab.HOME,
        NavigationTab.SONGS,
        NavigationTab.LIBRARY,
        NavigationTab.SETTINGS
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        // Floating 100% Real Liquid Glass Dock Card
        Box(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth()
                .height(68.dp)
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(34.dp),
                    ambientColor = Color(0x66000000),
                    spotColor = Color(0x35FFFFFF)
                )
                .clip(RoundedCornerShape(34.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xE01A1A20),
                            Color(0xCC101014),
                            Color(0xE6141418)
                        )
                    )
                )
                .border(
                    BorderStroke(
                        1.2.dp,
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0x80FFFFFF),
                                Color(0x18FFFFFF),
                                Color(0x55FFFFFF)
                            )
                        )
                    ),
                    shape = RoundedCornerShape(34.dp)
                )
        ) {
            // Specular Top Sheen Highlight Overlay
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            0.0f to Color(0x30FFFFFF),
                            0.4f to Color(0x06FFFFFF),
                            1.0f to Color.Transparent
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .matchParentSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                visibleTabs.forEach { tab ->
                    val isSelected = tab == currentTab
                    NavTabItem(
                        tab = tab,
                        isSelected = isSelected,
                        onClick = { onTabSelected(tab) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NavTabItem(
    tab: NavigationTab,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.12f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "TabScale"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color(0xFF8E8E93),
        label = "TabColor"
    )

    val icon: ImageVector = when (tab) {
        NavigationTab.HOME -> if (isSelected) Icons.Filled.Home else Icons.Outlined.Home
        NavigationTab.SONGS -> if (isSelected) Icons.Filled.MusicNote else Icons.Outlined.MusicNote
        NavigationTab.LIBRARY -> if (isSelected) Icons.Filled.LibraryMusic else Icons.Outlined.LibraryMusic
        NavigationTab.SETTINGS -> if (isSelected) Icons.Filled.Settings else Icons.Outlined.Settings
        NavigationTab.SEARCH -> if (isSelected) Icons.Filled.Search else Icons.Outlined.Search
        NavigationTab.PLAYLISTS -> if (isSelected) Icons.Filled.LibraryMusic else Icons.Outlined.LibraryMusic
        NavigationTab.STUDIO_EQ -> if (isSelected) Icons.Filled.Equalizer else Icons.Outlined.Equalizer
        NavigationTab.THEMES, NavigationTab.HI_RES_MASTERS -> if (isSelected) Icons.Filled.Palette else Icons.Outlined.Palette
    }

    val activeGlassModifier = if (isSelected) {
        Modifier
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0x35FFFFFF),
                        Color(0x15FFFFFF)
                    )
                )
            )
            .border(
                BorderStroke(
                    1.dp,
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0x80FFFFFF),
                            Color(0x25FFFFFF)
                        )
                    )
                ),
                shape = RoundedCornerShape(22.dp)
            )
    } else {
        Modifier.clip(RoundedCornerShape(22.dp))
    }

    Box(
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .then(activeGlassModifier)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = 26.dp, color = Color.White.copy(alpha = 0.25f)),
                onClick = onClick
            )
            .testTag("nav_tab_${tab.name.lowercase()}")
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = tab.title,
                tint = contentColor,
                modifier = Modifier
                    .size(22.dp)
                    .scale(iconScale)
            )

            Text(
                text = tab.title,
                color = contentColor,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
