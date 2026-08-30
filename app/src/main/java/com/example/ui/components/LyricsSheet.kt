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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AudioTrack
import com.example.ui.theme.LocalAuraColors

data class LyricLine(val timeMs: Long, val text: String)

@Composable
fun LyricsSheet(
    track: AudioTrack?,
    currentPositionMs: Long,
    onDismiss: () -> Unit,
    onEditLyrics: () -> Unit,
    onSeekTo: (Long) -> Unit
) {
    if (track == null) return
    val auraColors = LocalAuraColors.current

    val lyricLines = remember(track.lyrics) {
        parseLrcLyrics(track.lyrics)
    }

    val activeIndex = remember(lyricLines, currentPositionMs) {
        var found = -1
        for (i in lyricLines.indices) {
            if (currentPositionMs >= lyricLines[i].timeMs) {
                found = i
            } else {
                break
            }
        }
        found
    }

    val listState = rememberLazyListState()

    LaunchedEffect(activeIndex) {
        if (activeIndex >= 0 && activeIndex < lyricLines.size) {
            listState.animateScrollToItem(activeIndex.coerceAtLeast(0))
        }
    }

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
                    .height(600.dp),
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
                                    imageVector = Icons.Default.Lyrics,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Synchronized Lyrics",
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = track.title,
                                    color = Color(0xFF8E8E93),
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            LiquidGlassIconButton(
                                icon = Icons.Default.Edit,
                                contentDescription = "Edit Lyrics",
                                onClick = onEditLyrics,
                                size = 36.dp,
                                iconSize = 18.dp,
                                modifier = Modifier.testTag("edit_lyrics_btn")
                            )

                            LiquidGlassIconButton(
                                icon = Icons.Default.Close,
                                contentDescription = "Close",
                                onClick = onDismiss,
                                size = 36.dp,
                                iconSize = 18.dp,
                                modifier = Modifier.testTag("close_lyrics_sheet")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (lyricLines.isEmpty() || track.lyrics.isBlank()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "No Lyrics Available",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Tap the edit button above to add custom synced or plain text lyrics.",
                                    color = Color(0xFF8E8E93),
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            itemsIndexed(lyricLines) { index, line ->
                                val isActive = index == activeIndex
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isActive) Color(0x33FFFFFF) else Color.Transparent)
                                        .clickable { onSeekTo(line.timeMs) }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = line.text,
                                        color = if (isActive) Color.White else Color(0x80FFFFFF),
                                        fontSize = if (isActive) 18.sp else 15.sp,
                                        fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium,
                                        lineHeight = 26.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun parseLrcLyrics(rawLyrics: String): List<LyricLine> {
    if (rawLyrics.isBlank()) return emptyList()
    val lines = rawLyrics.lines()
    val result = mutableListOf<LyricLine>()
    val timeRegex = Regex("""\[(\d{2}):(\d{2})\.?(\d{2,3})?]""")

    for ((idx, line) in lines.withIndex()) {
        val trimmed = line.trim()
        if (trimmed.isBlank()) continue
        val match = timeRegex.find(trimmed)
        if (match != null) {
            val min = match.groupValues[1].toLongOrNull() ?: 0L
            val sec = match.groupValues[2].toLongOrNull() ?: 0L
            val msStr = match.groupValues.getOrNull(3) ?: "0"
            val ms = if (msStr.length == 2) (msStr.toLongOrNull() ?: 0L) * 10 else (msStr.toLongOrNull() ?: 0L)
            val totalMs = min * 60000L + sec * 1000L + ms
            val text = trimmed.substring(match.range.last + 1).trim()
            if (text.isNotBlank()) {
                result.add(LyricLine(totalMs, text))
            }
        } else {
            // Fallback for plain text lyrics: spread across track length
            result.add(LyricLine(idx * 15000L, trimmed))
        }
    }
    return result
}
