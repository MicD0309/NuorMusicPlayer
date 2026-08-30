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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.PlaylistEntity
import com.example.data.model.AudioTrack
import com.example.ui.theme.LocalAuraColors

@Composable
fun PlaylistsView(
    playlists: List<PlaylistEntity>,
    activePlaylist: PlaylistEntity?,
    allTracks: List<AudioTrack>,
    currentPlayingTrack: AudioTrack?,
    isPlaying: Boolean,
    onSelectPlaylist: (PlaylistEntity) -> Unit,
    onBackFromPlaylist: () -> Unit,
    onCreatePlaylistClick: () -> Unit,
    onDeletePlaylist: (Long) -> Unit,
    onPlayTrack: (AudioTrack) -> Unit,
    onToggleFavorite: (AudioTrack) -> Unit,
    onOpenMetadataEditor: (AudioTrack) -> Unit,
    onOpenInspector: (AudioTrack) -> Unit,
    modifier: Modifier = Modifier
) {
    val auraColors = LocalAuraColors.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        if (activePlaylist == null) {
            // Main Playlists Overview
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0x30FFFFFF))
                            .border(1.dp, Color(0x60FFFFFF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QueueMusic,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Playlists",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "${playlists.size} Curated Collections",
                            color = Color(0xFF8E8E93),
                            fontSize = 12.sp
                        )
                    }
                }

                LiquidGlassIconButton(
                    icon = Icons.Default.Add,
                    contentDescription = "New Playlist",
                    onClick = onCreatePlaylistClick,
                    size = 40.dp,
                    iconSize = 22.dp,
                    isProminent = true,
                    modifier = Modifier.testTag("create_playlist_fab")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (playlists.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No Playlists Yet",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Create custom audiophile collections by tapping '+' above.",
                            color = Color(0xFF8E8E93),
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(playlists) { pl ->
                        val trackCount = parseCount(pl.trackIdsJson)

                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("playlist_item_${pl.playlistId}"),
                            shape = RoundedCornerShape(20.dp),
                            onClick = { onSelectPlaylist(pl) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0x30FFFFFF))
                                        .border(1.dp, Color(0x60FFFFFF), RoundedCornerShape(14.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QueueMusic,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = pl.name,
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "$trackCount tracks • ${pl.description.ifBlank { "Custom Playlist" }}",
                                        color = Color(0xFF8E8E93),
                                        fontSize = 12.sp
                                    )
                                }

                                LiquidGlassIconButton(
                                    icon = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    onClick = { onDeletePlaylist(pl.playlistId) },
                                    size = 36.dp,
                                    iconSize = 18.dp
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        } else {
            // Detailed Playlist Track List View
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LiquidGlassIconButton(
                    icon = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    onClick = onBackFromPlaylist,
                    size = 40.dp,
                    iconSize = 22.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = activePlaylist.name,
                        color = Color.White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = activePlaylist.description.ifBlank { "Curated Playlist" },
                        color = Color(0xFF8E8E93),
                        fontSize = 12.sp
                    )
                }
            }

            val targetIds = parseTrackIds(activePlaylist.trackIdsJson)
            val playlistTracks = allTracks.filter { it.id in targetIds }

            if (playlistTracks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "This playlist is empty. Add songs from your library!",
                        color = Color(0xFF8E8E93),
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(playlistTracks) { track ->
                        TrackItemView(
                            track = track,
                            isCurrentPlaying = currentPlayingTrack?.id == track.id,
                            isPlaying = isPlaying && currentPlayingTrack?.id == track.id,
                            onClick = { onPlayTrack(track) },
                            onToggleFavorite = { onToggleFavorite(track) },
                            onOpenMetadataEditor = { onOpenMetadataEditor(track) },
                            onOpenInspector = { onOpenInspector(track) },
                            onAddToPlaylist = { /* Already in playlist */ }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CreatePlaylistDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, description: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Create New Playlist",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    LiquidGlassIconButton(
                        icon = Icons.Default.Close,
                        contentDescription = "Close",
                        onClick = onDismiss,
                        size = 34.dp,
                        iconSize = 18.dp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Playlist Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color(0x35FFFFFF),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color(0xFF8E8E93),
                        cursorColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_playlist_name")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description (Optional)") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color(0x35FFFFFF),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color(0xFF8E8E93),
                        cursorColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_playlist_desc")
                )

                Spacer(modifier = Modifier.height(18.dp))

                LiquidGlassButton(
                    text = "Create Playlist",
                    onClick = {
                        if (name.isNotBlank()) {
                            onCreate(name.trim(), desc.trim())
                        }
                    },
                    isProminent = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("confirm_create_playlist_btn")
                )
            }
        }
    }
}

@Composable
fun AddToPlaylistDialog(
    track: AudioTrack?,
    playlists: List<PlaylistEntity>,
    onDismiss: () -> Unit,
    onAddToPlaylist: (PlaylistEntity, AudioTrack) -> Unit,
    onCreateNewPlaylist: () -> Unit
) {
    if (track == null) return

    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add to Playlist",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    LiquidGlassIconButton(
                        icon = Icons.Default.Close,
                        contentDescription = "Close",
                        onClick = onDismiss,
                        size = 34.dp,
                        iconSize = 18.dp
                    )
                }

                Text(
                    text = "${track.title} by ${track.artist}",
                    color = Color(0xFF8E8E93),
                    fontSize = 12.sp,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (playlists.isEmpty()) {
                    Text(
                        text = "No playlists found. Create one first!",
                        color = Color(0xFF8E8E93),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LiquidGlassButton(
                        text = "Create New Playlist",
                        onClick = onCreateNewPlaylist,
                        isProminent = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(260.dp)
                    ) {
                        items(playlists) { pl ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0x20FFFFFF))
                                    .border(1.dp, Color(0x35FFFFFF), RoundedCornerShape(14.dp))
                                    .clickable { onAddToPlaylist(pl, track) }
                                    .padding(14.dp)
                            ) {
                                Text(
                                    text = pl.name,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun parseCount(json: String): Int {
    val cleaned = json.trim().removeSurrounding("[", "]")
    if (cleaned.isBlank()) return 0
    return cleaned.split(",").filter { it.isNotBlank() }.size
}

private fun parseTrackIds(json: String): Set<String> {
    val cleaned = json.trim().removeSurrounding("[", "]")
    if (cleaned.isBlank()) return emptySet()
    return cleaned.split(",").map { it.trim().removeSurrounding("\"") }.toSet()
}
