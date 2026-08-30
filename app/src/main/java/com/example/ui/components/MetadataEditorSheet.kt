package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AudioTrack
import com.example.ui.theme.LocalAuraColors

@Composable
fun MetadataEditorSheet(
    track: AudioTrack?,
    onDismiss: () -> Unit,
    onSave: (
        trackId: String,
        title: String,
        artist: String,
        album: String,
        genre: String,
        year: Int,
        trackNumber: Int,
        lyrics: String,
        rating: Int,
        coverPreset: Int
    ) -> Unit
) {
    if (track == null) return
    val auraColors = LocalAuraColors.current

    var title by remember(track) { mutableStateOf(track.title) }
    var artist by remember(track) { mutableStateOf(track.artist) }
    var album by remember(track) { mutableStateOf(track.album) }
    var genre by remember(track) { mutableStateOf(track.genre) }
    var yearText by remember(track) { mutableStateOf(track.year.toString()) }
    var trackNumText by remember(track) { mutableStateOf(track.trackNumber.toString()) }
    var lyrics by remember(track) { mutableStateOf(track.lyrics) }
    var rating by remember(track) { mutableIntStateOf(track.rating) }
    var coverPreset by remember(track) { mutableIntStateOf(track.coverPreset) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(680.dp),
                shape = RoundedCornerShape(32.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
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
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "ID3 & Audio Metadata",
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Lossless Tag Editor",
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
                            modifier = Modifier.testTag("close_metadata_editor")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Form Fields
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        MetadataTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = "Track Title",
                            testTag = "input_meta_title"
                        )

                        MetadataTextField(
                            value = artist,
                            onValueChange = { artist = it },
                            label = "Artist Name",
                            testTag = "input_meta_artist"
                        )

                        MetadataTextField(
                            value = album,
                            onValueChange = { album = it },
                            label = "Album",
                            testTag = "input_meta_album"
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                MetadataTextField(
                                    value = genre,
                                    onValueChange = { genre = it },
                                    label = "Genre",
                                    testTag = "input_meta_genre"
                                )
                            }
                            Box(modifier = Modifier.weight(0.7f)) {
                                MetadataTextField(
                                    value = yearText,
                                    onValueChange = { yearText = it },
                                    label = "Year",
                                    keyboardType = KeyboardType.Number,
                                    testTag = "input_meta_year"
                                )
                            }
                            Box(modifier = Modifier.weight(0.5f)) {
                                MetadataTextField(
                                    value = trackNumText,
                                    onValueChange = { trackNumText = it },
                                    label = "Track #",
                                    keyboardType = KeyboardType.Number,
                                    testTag = "input_meta_track_num"
                                )
                            }
                        }

                        // Rating
                        Text(
                            text = "Audiophile Rating",
                            color = Color(0xFF8E8E93),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            (1..5).forEach { star ->
                                IconButton(
                                    onClick = { rating = star },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = if (star <= rating) Icons.Default.Star else Icons.Outlined.Star,
                                        contentDescription = "Rating $star",
                                        tint = if (star <= rating) Color.White else Color(0x55FFFFFF)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Custom Lyrics
                        MetadataTextField(
                            value = lyrics,
                            onValueChange = { lyrics = it },
                            label = "Synchronized / Offline Lyrics",
                            singleLine = false,
                            minLines = 4,
                            testTag = "input_meta_lyrics"
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LiquidGlassButton(
                            text = "Cancel",
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        )

                        LiquidGlassButton(
                            text = "Save Metadata",
                            icon = Icons.Default.Save,
                            isProminent = true,
                            onClick = {
                                val year = yearText.toIntOrNull() ?: 2026
                                val trackNum = trackNumText.toIntOrNull() ?: 1
                                onSave(
                                    track.id,
                                    title.trim(),
                                    artist.trim(),
                                    album.trim(),
                                    genre.trim(),
                                    year,
                                    trackNum,
                                    lyrics.trim(),
                                    rating,
                                    coverPreset
                                )
                            },
                            modifier = Modifier
                                .weight(1.5f)
                                .testTag("save_metadata_button")
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetadataTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    testTag: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
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
            .padding(vertical = 4.dp)
            .testTag(testTag)
    )
}
