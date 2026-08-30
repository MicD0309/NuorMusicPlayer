package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioTrack
import com.example.ui.theme.LocalAuraColors

@Composable
fun ConvxHomeView(
    tracks: List<AudioTrack>,
    currentPlayingTrack: AudioTrack?,
    isPlaying: Boolean,
    onPlayTrack: (AudioTrack) -> Unit,
    onShuffleAll: () -> Unit,
    onNavigateToSongs: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val auraColors = LocalAuraColors.current
    val scrollState = rememberScrollState()

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState)
                .padding(bottom = 120.dp)
        ) {
            // Top Bar: CONVX Brand Logo & Liquid Glass Settings Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "CONVX",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x33FFFFFF))
                            .border(1.dp, Color(0x66FFFFFF), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "HI-RES",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.6.sp
                        )
                    }
                }

                // Liquid Glass Settings Button
                LiquidGlassIconButton(
                    icon = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    onClick = onNavigateToSettings,
                    modifier = Modifier.testTag("home_settings_btn")
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Featured Hero Banner Card - Pure Liquid Glass Obsidian & Specular Crystal
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(175.dp)
                    .testTag("home_hero_card"),
                shape = RoundedCornerShape(26.dp),
                elevation = 14.dp,
                onClick = onShuffleAll
            ) {
                // Crystal Gradient Backing
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0x40FFFFFF),
                                    Color(0x10FFFFFF),
                                    Color(0x05FFFFFF),
                                    Color(0x18FFFFFF)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "DAILY HI-RES MIX",
                                color = Color(0xFFB0B0B8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Pure Lossless\nSoundscapes",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                lineHeight = 26.sp
                            )
                        }

                        // Liquid Glass Play Orb
                        LiquidGlassIconButton(
                            icon = Icons.Default.PlayArrow,
                            contentDescription = "Play Mix",
                            onClick = onShuffleAll,
                            size = 48.dp,
                            iconSize = 28.dp,
                            isProminent = true
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x28FFFFFF))
                                .border(1.dp, Color(0x40FFFFFF), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "FLAC 24-Bit / 96kHz",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "${tracks.size} Tracks Available",
                            color = Color(0xFFA0A0A8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section: Recently Played
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recently Played",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "See all",
                    color = Color(0xFFD1D1D6),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onNavigateToSongs)
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Horizontal Recent Tracks Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(tracks.take(8)) { track ->
                    RecentTrackCard(
                        track = track,
                        isCurrentPlaying = track.id == currentPlayingTrack?.id,
                        isPlaying = isPlaying,
                        onClick = { onPlayTrack(track) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Quick Action Buttons Row - Liquid Glass
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LiquidGlassButton(
                    text = "Shuffle All",
                    icon = Icons.Default.Shuffle,
                    isProminent = true,
                    onClick = onShuffleAll,
                    modifier = Modifier.weight(1f)
                )

                LiquidGlassButton(
                    text = "All Songs",
                    icon = Icons.Default.Headphones,
                    isProminent = false,
                    onClick = onNavigateToSongs,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Section: Audiophile Master Series
            Text(
                text = "Audiophile Master Series",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AudiophileFeatureTile(
                    title = "Lossless Studio Masters",
                    subtitle = "Bit-perfect 96kHz / 24-bit direct audio stream",
                    badge = "DIRECT FLAC",
                    onClick = onShuffleAll
                )

                AudiophileFeatureTile(
                    title = "Spatial Acoustic Depth",
                    subtitle = "Ultra-low jitter & 3D soundstage engine",
                    badge = "3D DAC",
                    onClick = onShuffleAll
                )
            }
        }

        // Floating Search Liquid Glass Orb Button (FAB)
        LiquidGlassIconButton(
            icon = Icons.Default.Search,
            contentDescription = "Search",
            onClick = onNavigateToSearch,
            size = 56.dp,
            iconSize = 26.dp,
            isProminent = true,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 86.dp)
                .testTag("home_search_fab")
        )
    }
}

@Composable
private fun RecentTrackCard(
    track: AudioTrack,
    isCurrentPlaying: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    val auraColors = LocalAuraColors.current

    Column(
        modifier = Modifier
            .width(130.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
    ) {
        TrackArtwork(
            track = track,
            size = 130.dp,
            shapeRadius = 18.dp,
            isRotatingVinyl = isCurrentPlaying,
            isPlaying = isPlaying,
            showPlayIconWhenActive = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = track.title,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = track.artist,
            color = Color(0xFF8E8E93),
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AudiophileFeatureTile(
    title: String,
    subtitle: String,
    badge: String,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x28FFFFFF))
                            .border(1.dp, Color(0x45FFFFFF), RoundedCornerShape(6.dp))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    color = Color(0xFF9E9EA4),
                    fontSize = 12.sp
                )
            }

            Icon(
                imageVector = Icons.Default.GraphicEq,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
