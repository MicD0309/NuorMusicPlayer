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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioTrack
import com.example.ui.SortOption
import com.example.ui.theme.LocalAuraColors
import kotlinx.coroutines.launch

@Composable
fun ConvxSongsView(
    tracks: List<AudioTrack>,
    currentPlayingTrack: AudioTrack?,
    isPlaying: Boolean,
    sortOption: SortOption,
    onSortSelect: (SortOption) -> Unit,
    onPlayTrack: (AudioTrack) -> Unit,
    onShuffleAll: () -> Unit,
    onToggleFavorite: (AudioTrack) -> Unit,
    onOpenMetadataEditor: (AudioTrack) -> Unit,
    onOpenInspector: (AudioTrack) -> Unit,
    onAddToPlaylist: (AudioTrack) -> Unit,
    onScanStorage: () -> Unit,
    onImportFileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val auraColors = LocalAuraColors.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var isSortMenuOpen by remember { mutableStateOf(false) }

    val alphabetChars = listOf(
        "↑", "0", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
        "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
        "W", "X", "Y", "Z", "#"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Header Row: "Songs" title + subtitle count + Liquid Glass action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Songs",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${tracks.size} Songs",
                        color = Color(0xFFA0A0A8),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Liquid Glass Refresh Storage Scanner
                    LiquidGlassIconButton(
                        icon = Icons.Default.Refresh,
                        contentDescription = "Scan Storage",
                        onClick = onScanStorage,
                        size = 40.dp,
                        iconSize = 18.dp
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Liquid Glass Import Audio File
                    LiquidGlassIconButton(
                        icon = Icons.Default.FileOpen,
                        contentDescription = "Import Audio",
                        onClick = onImportFileClick,
                        size = 40.dp,
                        iconSize = 18.dp
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Liquid Glass Sort Menu Button
                    Box {
                        LiquidGlassIconButton(
                            icon = Icons.Default.Sort,
                            contentDescription = "Sort",
                            onClick = { isSortMenuOpen = true },
                            size = 40.dp,
                            iconSize = 18.dp,
                            modifier = Modifier.testTag("songs_sort_button")
                        )

                        DropdownMenu(
                            expanded = isSortMenuOpen,
                            onDismissRequest = { isSortMenuOpen = false },
                            modifier = Modifier
                                .background(Color(0xE6141418))
                                .border(1.dp, Color(0x35FFFFFF), RoundedCornerShape(16.dp))
                                .clip(RoundedCornerShape(16.dp))
                        ) {
                            SortOption.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = option.displayName,
                                            color = if (option == sortOption) Color.White else Color(0xFFB0B0B8),
                                            fontWeight = if (option == sortOption) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        onSortSelect(option)
                                        isSortMenuOpen = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Songs List and Right-side Alphabet Scroller
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Tracks List
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentPadding = PaddingValues(bottom = 140.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    itemsIndexed(
                        items = tracks,
                        key = { _, track -> track.id }
                    ) { _, track ->
                        val isCurrent = track.id == currentPlayingTrack?.id
                        TrackItemView(
                            track = track,
                            isCurrentPlaying = isCurrent,
                            isPlaying = isPlaying,
                            onClick = { onPlayTrack(track) },
                            onToggleFavorite = { onToggleFavorite(track) },
                            onOpenMetadataEditor = { onOpenMetadataEditor(track) },
                            onOpenInspector = { onOpenInspector(track) },
                            onAddToPlaylist = { onAddToPlaylist(track) }
                        )
                    }
                }

                // Alphabet Fast Scroller on the right (B&W Frosted)
                Column(
                    modifier = Modifier
                        .width(22.dp)
                        .fillMaxHeight()
                        .padding(vertical = 4.dp, horizontal = 2.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    alphabetChars.forEach { char ->
                        Text(
                            text = char,
                            color = Color(0xFFD1D1D6),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    coroutineScope.launch {
                                        if (char == "↑") {
                                            listState.scrollToItem(0)
                                        } else {
                                            val targetIndex = tracks.indexOfFirst {
                                                it.title.startsWith(char, ignoreCase = true)
                                            }
                                            if (targetIndex != -1) {
                                                listState.scrollToItem(targetIndex)
                                            }
                                        }
                                    }
                                }
                                .padding(vertical = 0.5.dp)
                        )
                    }
                }
            }
        }

        // Floating Liquid Glass Shuffle FAB
        LiquidGlassIconButton(
            icon = Icons.Default.Shuffle,
            contentDescription = "Shuffle",
            onClick = onShuffleAll,
            size = 56.dp,
            iconSize = 26.dp,
            isProminent = true,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 26.dp, bottom = 86.dp)
                .testTag("songs_shuffle_fab")
        )
    }
}
