package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun TrackItemView(
    track: AudioTrack,
    isCurrentPlaying: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onOpenMetadataEditor: () -> Unit,
    onOpenInspector: () -> Unit,
    onAddToPlaylist: () -> Unit,
    modifier: Modifier = Modifier
) {
    val auraColors = LocalAuraColors.current
    var isMenuExpanded by remember { mutableStateOf(false) }

    val activeGlassModifier = if (isCurrentPlaying) {
        Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0x35FFFFFF),
                        Color(0x15FFFFFF)
                    )
                )
            )
            .border(
                BorderStroke(
                    1.dp,
                    Brush.linearGradient(
                        listOf(
                            Color(0x80FFFFFF),
                            Color(0x20FFFFFF),
                            Color(0x50FFFFFF)
                        )
                    )
                ),
                shape = RoundedCornerShape(18.dp)
            )
    } else {
        Modifier.clip(RoundedCornerShape(18.dp))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(activeGlassModifier)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = Color.White.copy(alpha = 0.15f)),
                onClick = onClick
            )
            .testTag("track_item_${track.id}")
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Track Artwork / Monogram
            TrackArtwork(
                track = track,
                size = 48.dp,
                shapeRadius = 14.dp,
                isRotatingVinyl = isCurrentPlaying,
                isPlaying = isPlaying,
                showPlayIconWhenActive = true
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Title & Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = if (isCurrentPlaying) FontWeight.Bold else FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${track.artist} • ${formatDuration(track.durationMs)}",
                    color = if (isCurrentPlaying) Color(0xFFD1D1D6) else Color(0xFF8E8E93),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 3-Dot Options Menu
            Box {
                IconButton(
                    onClick = { isMenuExpanded = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = Color(0xFF8E8E93),
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false },
                    modifier = Modifier
                        .background(Color(0xE616161A))
                        .border(1.dp, Color(0x35FFFFFF), RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    DropdownMenuItem(
                        text = { Text("Play Track", color = Color.White) },
                        leadingIcon = { Icon(Icons.Default.PlayArrow, null, tint = Color.White) },
                        onClick = {
                            isMenuExpanded = false
                            onClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(if (track.isFavorite) "Remove from Favorites" else "Add to Favorites", color = Color.White) },
                        leadingIcon = { Icon(if (track.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, null, tint = Color.White) },
                        onClick = {
                            isMenuExpanded = false
                            onToggleFavorite()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Add to Playlist", color = Color.White) },
                        leadingIcon = { Icon(Icons.Default.PlaylistAdd, null, tint = Color.White) },
                        onClick = {
                            isMenuExpanded = false
                            onAddToPlaylist()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Edit Metadata", color = Color.White) },
                        leadingIcon = { Icon(Icons.Default.Edit, null, tint = Color.White) },
                        onClick = {
                            isMenuExpanded = false
                            onOpenMetadataEditor()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Audio Specs", color = Color.White) },
                        leadingIcon = { Icon(Icons.Default.Info, null, tint = Color.White) },
                        onClick = {
                            isMenuExpanded = false
                            onOpenInspector()
                        }
                    )
                }
            }
        }
    }
}

private fun formatDuration(ms: Long): String {
    val totalSecs = (ms / 1000L).coerceAtLeast(0L)
    val mins = totalSecs / 60
    val secs = totalSecs % 60
    return String.format("%d:%02d", mins, secs)
}
