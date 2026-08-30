package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AuraThemeStyle
import com.example.ui.theme.LocalAuraColors

@Composable
fun ConvxSettingsView(
    currentTheme: AuraThemeStyle,
    onSelectTheme: (AuraThemeStyle) -> Unit,
    onNavigateToEqualizer: () -> Unit,
    onScanStorage: () -> Unit,
    onImportFile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val auraColors = LocalAuraColors.current
    val scrollState = rememberScrollState()

    var isBitPerfectEnabled by remember { mutableStateOf(true) }
    var isDacDirectEnabled by remember { mutableStateOf(true) }
    var isGaplessEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState)
            .padding(bottom = 140.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Settings",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Section: Audiophile Playback Engine
        SettingsSectionHeader(title = "AUDIOPHILE ENGINE")

        SettingsToggleCard(
            title = "Bit-Perfect Lossless Output",
            subtitle = "Bypass Android audio resampler for bit-exact FLAC & DSD playback",
            icon = Icons.Default.GraphicEq,
            isChecked = isBitPerfectEnabled,
            onCheckedChange = { isBitPerfectEnabled = it }
        )

        Spacer(modifier = Modifier.height(10.dp))

        SettingsToggleCard(
            title = "Direct Hardware DAC Access",
            subtitle = "Enable 32-bit floating point high dynamic range output",
            icon = Icons.Default.Speed,
            isChecked = isDacDirectEnabled,
            onCheckedChange = { isDacDirectEnabled = it }
        )

        Spacer(modifier = Modifier.height(10.dp))

        SettingsToggleCard(
            title = "Gapless Audio Transitions",
            subtitle = "Seamless zero-latency crossfade between tracks",
            icon = Icons.Default.MusicNote,
            isChecked = isGaplessEnabled,
            onCheckedChange = { isGaplessEnabled = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Section: Sound & Equalizer
        SettingsSectionHeader(title = "ACOUSTIC STUDIO")

        SettingsActionCard(
            title = "Studio Parametric Equalizer",
            subtitle = "10-Band EQ, Dynamic Bass Boost & Spatial 3D Virtualizer",
            icon = Icons.Default.Equalizer,
            onClick = onNavigateToEqualizer
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Section: Appearance & Theme
        SettingsSectionHeader(title = "APPEARANCE")

        SettingsActionCard(
            title = "CONVX Dark Theme",
            subtitle = "Pure Obsidian & Liquid Frosted Glass (Black & White)",
            icon = Icons.Default.Palette,
            onClick = { /* Keep active */ }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Section: Library & Storage
        SettingsSectionHeader(title = "LOCAL LIBRARY")

        SettingsActionCard(
            title = "Rescan Storage for Audio Files",
            subtitle = "Scan internal storage and SD card for new songs",
            icon = Icons.Default.Refresh,
            onClick = onScanStorage
        )

        Spacer(modifier = Modifier.height(10.dp))

        SettingsActionCard(
            title = "Import Audio File Manually",
            subtitle = "Select any audio file from file explorer",
            icon = Icons.Default.FileOpen,
            onClick = onImportFile
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Section: About
        SettingsSectionHeader(title = "ABOUT")

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x35FFFFFF))
                            .border(1.dp, Color(0x60FFFFFF), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "C",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "CONVX Music Player",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Version 2.6.0 • Liquid Glass Edition",
                            color = Color(0xFF8E8E93),
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "High-Resolution offline audio player with bit-perfect decoding, liquid glass interface, and lossless soundstage.",
                    color = Color(0xFF9E9EA4),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        color = Color(0xFFD1D1D6),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

@Composable
private fun SettingsToggleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x28FFFFFF))
                        .border(1.dp, Color(0x40FFFFFF), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = subtitle,
                        color = Color(0xFF8E8E93),
                        fontSize = 11.sp
                    )
                }
            }

            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF101012),
                    checkedTrackColor = Color.White,
                    uncheckedThumbColor = Color(0xFF8E8E93),
                    uncheckedTrackColor = Color(0x25FFFFFF),
                    uncheckedBorderColor = Color(0x40FFFFFF)
                )
            )
        }
    }
}

@Composable
private fun SettingsActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x25FFFFFF))
                        .border(1.dp, Color(0x40FFFFFF), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = subtitle,
                        color = Color(0xFF8E8E93),
                        fontSize = 11.sp
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF8E8E93),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
