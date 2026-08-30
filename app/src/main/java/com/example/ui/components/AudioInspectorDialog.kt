package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AudioTrack
import com.example.ui.theme.LocalAuraColors

@Composable
fun AudioInspectorDialog(
    track: AudioTrack?,
    onDismiss: () -> Unit
) {
    if (track == null) return
    val auraColors = LocalAuraColors.current

    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0x30FFFFFF))
                                .border(1.dp, Color(0x60FFFFFF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Hi-Res Audio Inspector",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Bit-Perfect Lossless Signal Path",
                                color = Color(0xFF8E8E93),
                                fontSize = 12.sp
                            )
                        }
                    }

                    LiquidGlassIconButton(
                        icon = Icons.Default.Close,
                        contentDescription = "Close",
                        onClick = onDismiss,
                        size = 36.dp,
                        iconSize = 18.dp,
                        modifier = Modifier.testTag("close_audio_inspector")
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // High-Res Audio Badge Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0x25FFFFFF),
                                    Color(0x10FFFFFF)
                                )
                            )
                        )
                        .border(
                            1.dp,
                            Color(0x40FFFFFF),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = track.formatBadge.uppercase(),
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Offline Master Stream: ${track.mimeType.uppercase()}",
                            color = Color(0xFFA1A1A8),
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Detailed Audiophile Spec Matrix
                SpecRow(
                    label = "Sampling Rate",
                    value = "${track.sampleRate / 1000.0} kHz (${track.sampleRate} Hz)"
                )
                SpecRow(
                    label = "Bit Depth",
                    value = "${track.bitDepth}-Bit Integer / Lossless PCM"
                )
                SpecRow(
                    label = "Audio Bitrate",
                    value = "${track.bitrateKbps} kbps"
                )
                SpecRow(
                    label = "Channel Architecture",
                    value = "2.0 Discrete Stereo (Spatial Rendered)"
                )
                SpecRow(
                    label = "Dynamic Range",
                    value = if (track.bitDepth >= 24) "144 dB (Studio Master Grade)" else "96 dB (Redbook CD)"
                )
                SpecRow(
                    label = "Offline Storage Mode",
                    value = "Zero Latency, 100% On-Device"
                )

                Spacer(modifier = Modifier.height(20.dp))

                LiquidGlassButton(
                    text = "Done",
                    onClick = onDismiss,
                    isProminent = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SpecRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color(0xFF8E8E93),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace
        )
    }
}
