package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.model.AudioTrack
import com.example.ui.SortOption
import com.example.ui.theme.LocalAuraColors

@Composable
fun LibraryView(
    tracks: List<AudioTrack>,
    totalTrackCount: Int,
    searchQuery: String,
    selectedGenre: String,
    sortOption: SortOption,
    hiResOnly: Boolean,
    isHiResTab: Boolean,
    currentPlayingTrack: AudioTrack?,
    isPlaying: Boolean,
    onSearchChange: (String) -> Unit,
    onGenreSelect: (String) -> Unit,
    onSortSelect: (SortOption) -> Unit,
    onToggleHiResOnly: () -> Unit,
    onScanStorage: () -> Unit,
    onImportFileClick: () -> Unit,
    onPlayTrack: (AudioTrack) -> Unit,
    onToggleFavorite: (AudioTrack) -> Unit,
    onOpenMetadataEditor: (AudioTrack) -> Unit,
    onOpenInspector: (AudioTrack) -> Unit,
    onAddToPlaylist: (AudioTrack) -> Unit,
    modifier: Modifier = Modifier
) {
    val auraColors = LocalAuraColors.current
    var isSortMenuExpanded by remember { mutableStateOf(false) }

    val genres = listOf("All", "Ambient", "Cyberpunk", "Neo-Classical", "Acoustic", "Electronic")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Header Row with Title & Quick Storage Scan / Import Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isHiResTab) "Hi-Res Studio Masters" else "Local Library",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = if (isHiResTab) "${tracks.size} Studio Master Tracks" else "$totalTrackCount Tracks • Offline",
                    color = Color(0xFF8E8E93),
                    fontSize = 12.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Storage Scan Button
                LiquidGlassIconButton(
                    icon = Icons.Default.Refresh,
                    contentDescription = "Scan Storage",
                    onClick = onScanStorage,
                    size = 38.dp,
                    iconSize = 20.dp,
                    modifier = Modifier.testTag("scan_storage_btn")
                )

                // Import Audio File SAF Button
                LiquidGlassIconButton(
                    icon = Icons.Default.FileOpen,
                    contentDescription = "Import Audio",
                    onClick = onImportFileClick,
                    size = 38.dp,
                    iconSize = 20.dp,
                    modifier = Modifier.testTag("import_audio_btn")
                )
            }
        }

        // Search Bar (Liquid Glass Style)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search songs, artists, albums...", color = Color(0xFF8E8E93), fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = Color(0xFF8E8E93),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color(0x35FFFFFF),
                focusedContainerColor = Color(0x20FFFFFF),
                unfocusedContainerColor = Color(0x15FFFFFF),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_bar")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal Genre Filters & Sort Dropdown
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sort Button
            Box {
                LiquidGlassButton(
                    text = "Sort",
                    icon = Icons.Default.Sort,
                    onClick = { isSortMenuExpanded = true },
                    modifier = Modifier.testTag("sort_button")
                )

                DropdownMenu(
                    expanded = isSortMenuExpanded,
                    onDismissRequest = { isSortMenuExpanded = false },
                    modifier = Modifier
                        .background(Color(0xE616161A))
                        .border(1.dp, Color(0x35FFFFFF), RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    SortOption.entries.forEach { opt ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = opt.displayName,
                                    color = if (opt == sortOption) Color.White else Color(0xFF8E8E93),
                                    fontWeight = if (opt == sortOption) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                isSortMenuExpanded = false
                                onSortSelect(opt)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Genre Pills Scrollable Row
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (!isHiResTab) {
                    // Hi-Res Only Quick Pill
                    val isHiResActive = hiResOnly
                    LiquidGlassButton(
                        text = "Hi-Res",
                        isProminent = isHiResActive,
                        onClick = onToggleHiResOnly,
                        modifier = Modifier.testTag("filter_hires_only")
                    )
                }

                genres.forEach { g ->
                    val isSelected = selectedGenre == g
                    LiquidGlassButton(
                        text = g,
                        isProminent = isSelected,
                        onClick = { onGenreSelect(g) },
                        modifier = Modifier.testTag("filter_genre_${g.lowercase()}")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tracks List
        if (tracks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = Color(0xFF8E8E93),
                        modifier = Modifier.size(52.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No Music Found",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap the folder icon at the top to import audio files or clear your search filters.",
                        color = Color(0xFF8E8E93),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(tracks, key = { it.id }) { track ->
                    TrackItemView(
                        track = track,
                        isCurrentPlaying = currentPlayingTrack?.id == track.id,
                        isPlaying = isPlaying,
                        onClick = { onPlayTrack(track) },
                        onToggleFavorite = { onToggleFavorite(track) },
                        onOpenMetadataEditor = { onOpenMetadataEditor(track) },
                        onOpenInspector = { onOpenInspector(track) },
                        onAddToPlaylist = { onAddToPlaylist(track) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(120.dp))
                }
            }
        }
    }
}
