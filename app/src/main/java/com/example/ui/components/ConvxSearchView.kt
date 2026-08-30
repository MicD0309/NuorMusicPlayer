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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.PlaylistEntity
import com.example.data.model.AudioTrack
import com.example.ui.SearchCategory
import com.example.ui.theme.LocalAuraColors

@Composable
fun ConvxSearchView(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedCategory: SearchCategory,
    onSelectCategory: (SearchCategory) -> Unit,
    tracks: List<AudioTrack>,
    playlists: List<PlaylistEntity>,
    currentPlayingTrack: AudioTrack?,
    isPlaying: Boolean,
    onPlayTrack: (AudioTrack) -> Unit,
    onToggleFavorite: (AudioTrack) -> Unit,
    onOpenMetadataEditor: (AudioTrack) -> Unit,
    onOpenInspector: (AudioTrack) -> Unit,
    onAddToPlaylist: (AudioTrack) -> Unit,
    onSelectPlaylist: (PlaylistEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val auraColors = LocalAuraColors.current
    val focusManager = LocalFocusManager.current

    val filteredTracks = remember(searchQuery, selectedCategory, tracks) {
        if (searchQuery.isBlank()) {
            tracks
        } else {
            val q = searchQuery.trim()
            tracks.filter { track ->
                when (selectedCategory) {
                    SearchCategory.ALL -> track.title.contains(q, ignoreCase = true) ||
                            track.artist.contains(q, ignoreCase = true) ||
                            track.album.contains(q, ignoreCase = true) ||
                            track.genre.contains(q, ignoreCase = true)
                    SearchCategory.SONGS -> track.title.contains(q, ignoreCase = true)
                    SearchCategory.ALBUMS -> track.album.contains(q, ignoreCase = true)
                    SearchCategory.ARTISTS -> track.artist.contains(q, ignoreCase = true)
                    SearchCategory.PLAYLISTS -> false
                }
            }
        }
    }

    val filteredPlaylists = remember(searchQuery, selectedCategory, playlists) {
        if (selectedCategory == SearchCategory.SONGS || selectedCategory == SearchCategory.ALBUMS || selectedCategory == SearchCategory.ARTISTS) {
            emptyList<PlaylistEntity>()
        } else if (searchQuery.isBlank()) {
            playlists
        } else {
            playlists.filter { it.name.contains(searchQuery.trim(), ignoreCase = true) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Liquid Glass Search Input Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = {
                Text(
                    text = "Search songs, albums, artists...",
                    color = Color(0xFF8E8E93),
                    fontSize = 15.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(26.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0x22FFFFFF),
                unfocusedContainerColor = Color(0x14FFFFFF),
                focusedBorderColor = Color(0x80FFFFFF),
                unfocusedBorderColor = Color(0x30FFFFFF),
                cursorColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("convx_search_input")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Liquid Glass Filter Category Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(SearchCategory.entries) { category ->
                val isSelected = category == selectedCategory

                val chipBg = if (isSelected) {
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFFFFF), Color(0xFFE2E2E8))
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(Color(0x30FFFFFF), Color(0x10FFFFFF))
                    )
                }
                val textColor = if (isSelected) Color(0xFF101012) else Color.White
                val borderBrush = if (isSelected) {
                    Brush.linearGradient(listOf(Color(0xFFFFFFFF), Color(0x88FFFFFF)))
                } else {
                    Brush.linearGradient(listOf(Color(0x60FFFFFF), Color(0x15FFFFFF)))
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(chipBg)
                        .border(1.dp, borderBrush, RoundedCornerShape(20.dp))
                        .clickable { onSelectCategory(category) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category.displayName,
                        color = textColor,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Results Section
        if (filteredTracks.isEmpty() && filteredPlaylists.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = Color(0x40FFFFFF),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No results found",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try searching for a different track, artist, or album",
                        color = Color(0xFF8E8E93),
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 140.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (selectedCategory == SearchCategory.ALL || selectedCategory == SearchCategory.PLAYLISTS) {
                    if (filteredPlaylists.isNotEmpty()) {
                        item {
                            Text(
                                text = "Playlists (${filteredPlaylists.size})",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                        items(filteredPlaylists) { playlist ->
                            PlaylistSearchCard(
                                playlist = playlist,
                                onClick = { onSelectPlaylist(playlist) }
                            )
                        }
                    }
                }

                if (selectedCategory != SearchCategory.PLAYLISTS && filteredTracks.isNotEmpty()) {
                    item {
                        Text(
                            text = "Tracks (${filteredTracks.size})",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                    items(filteredTracks, key = { it.id }) { track ->
                        TrackItemView(
                            track = track,
                            isCurrentPlaying = track.id == currentPlayingTrack?.id,
                            isPlaying = isPlaying,
                            onClick = { onPlayTrack(track) },
                            onToggleFavorite = { onToggleFavorite(track) },
                            onOpenMetadataEditor = { onOpenMetadataEditor(track) },
                            onOpenInspector = { onOpenInspector(track) },
                            onAddToPlaylist = { onAddToPlaylist(track) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaylistSearchCard(
    playlist: PlaylistEntity,
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
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x30FFFFFF))
                    .border(1.dp, Color(0x60FFFFFF), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = playlist.name,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = playlist.description.ifEmpty { "Offline Playlist" },
                    color = Color(0xFF8E8E93),
                    fontSize = 12.sp
                )
            }
        }
    }
}
