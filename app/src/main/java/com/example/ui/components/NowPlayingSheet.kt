package com.example.ui.components

import android.os.Build
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AudioTrack
import com.example.player.RepeatMode
import com.example.ui.theme.LocalAuraColors

@Composable
fun NowPlayingSheet(
    track: AudioTrack?,
    isPlaying: Boolean,
    progressMs: Long,
    durationMs: Long,
    repeatMode: RepeatMode,
    isShuffle: Boolean,
    playbackSpeed: Float,
    volume: Float,
    visualizerAmplitudes: List<Float>,
    onDismiss: () -> Unit,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    onCycleRepeat: () -> Unit,
    onToggleShuffle: () -> Unit,
    onSetSpeed: (Float) -> Unit,
    onSetVolume: (Float) -> Unit,
    onToggleFavorite: (AudioTrack) -> Unit,
    onOpenLyrics: () -> Unit,
    onOpenInspector: () -> Unit,
    onOpenMetadataEditor: (AudioTrack) -> Unit,
    onOpenEqualizer: () -> Unit,
    onAddToPlaylist: (AudioTrack) -> Unit,
    modifier: Modifier = Modifier
) {
    if (track == null) return

    var isDraggingScrubber by remember { mutableStateOf(false) }
    var dragProgressFraction by remember { mutableFloatStateOf(0f) }
    var isMoreMenuOpen by remember { mutableStateOf(false) }

    val currentFraction = if (isDraggingScrubber) {
        dragProgressFraction
    } else {
        if (durationMs > 0) (progressMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f
    }

    val playButtonScale by animateFloatAsState(
        targetValue = if (isPlaying) 1.04f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "PlayButtonScale"
    )

    // Full screen blurred background container matching Screenshot 2
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xEE111116))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Blurred ambient background of artwork & lights
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            TrackArtwork(
                track = track,
                size = 280.dp,
                shapeRadius = 32.dp,
                isRotatingVinyl = false,
                isPlaying = isPlaying,
                showPlayIconWhenActive = false,
                modifier = Modifier
                    .padding(top = 40.dp)
                    .scale(1.2f)
                    .then(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            Modifier.blur(48.dp)
                        } else {
                            Modifier
                        }
                    )
            )
        }

        // Dark frosted tint scrim to ensure perfect contrast and sleek aesthetic
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x9508080C))
        )

        // Main Content Column matching Screenshot_20260830_190200.jpg
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar with Dismiss Icon & Library label
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF))
                        .border(1.dp, Color(0x35FFFFFF), CircleShape)
                        .clickable(onClick = onDismiss)
                        .testTag("dismiss_now_playing"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Dismiss",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = "NOW PLAYING",
                    color = Color(0xFFA1A1A8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.4.sp
                )

                Spacer(modifier = Modifier.size(42.dp))
            }

            // Central Artwork
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                TrackArtwork(
                    track = track,
                    size = 220.dp,
                    shapeRadius = 24.dp,
                    isRotatingVinyl = false,
                    isPlaying = isPlaying,
                    showPlayIconWhenActive = false,
                    modifier = Modifier.testTag("now_playing_artwork")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Title, Artist, More (...) and Favorite Buttons (matching Screenshot 2 layout)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                ) {
                    Text(
                        text = track.title,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = track.artist.ifBlank { "<unknown>" },
                        color = Color(0xFFAAAAAF),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // More Options (...) Button
                    Box {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0x35FFFFFF))
                                .border(1.dp, Color(0x35FFFFFF), CircleShape)
                                .clickable { isMoreMenuOpen = true }
                                .testTag("more_options_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreHoriz,
                                contentDescription = "More Options",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = isMoreMenuOpen,
                            onDismissRequest = { isMoreMenuOpen = false },
                            modifier = Modifier
                                .background(Color(0xF018181E))
                                .border(1.dp, Color(0x40FFFFFF), RoundedCornerShape(16.dp))
                                .clip(RoundedCornerShape(16.dp))
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit Metadata", color = Color.White, fontSize = 13.sp) },
                                onClick = {
                                    isMoreMenuOpen = false
                                    onOpenMetadataEditor(track)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Add to Playlist", color = Color.White, fontSize = 13.sp) },
                                onClick = {
                                    isMoreMenuOpen = false
                                    onAddToPlaylist(track)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Hi-Res Audio Specs", color = Color.White, fontSize = 13.sp) },
                                onClick = {
                                    isMoreMenuOpen = false
                                    onOpenInspector()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Speed: ${playbackSpeed}x", color = Color.White, fontSize = 13.sp) },
                                onClick = {
                                    isMoreMenuOpen = false
                                    val nextSpeed = when (playbackSpeed) {
                                        1.0f -> 1.25f
                                        1.25f -> 1.5f
                                        1.5f -> 2.0f
                                        2.0f -> 0.75f
                                        else -> 1.0f
                                    }
                                    onSetSpeed(nextSpeed)
                                }
                            )
                        }
                    }

                    // Favorite Button (Heart)
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (track.isFavorite) Color.White else Color(0x35FFFFFF))
                            .border(1.dp, Color(0x35FFFFFF), CircleShape)
                            .clickable { onToggleFavorite(track) }
                            .testTag("now_playing_favorite_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (track.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (track.isFavorite) Color.Black else Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Sleek Capsule Progress Bar (Scrubber) matching Screenshot 2
            MinimalistProgressBar(
                fraction = currentFraction,
                onSeekFraction = { frac ->
                    val targetMs = (frac * durationMs).toLong()
                    onSeek(targetMs)
                },
                onDragStateChange = { isDragging, frac ->
                    isDraggingScrubber = isDragging
                    dragProgressFraction = frac
                    if (!isDragging) {
                        val targetMs = (frac * durationMs).toLong()
                        onSeek(targetMs)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("track_progress_bar")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Time Row + Format Badge Capsule (e.g. MP3 • 48.0kHz)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val currentMs = (currentFraction * durationMs).toLong()
                Text(
                    text = formatTime(currentMs),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                // Format Spec Capsule Badge matching Screenshot 2
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x30FFFFFF))
                        .border(1.dp, Color(0x35FFFFFF), RoundedCornerShape(12.dp))
                        .clickable(onClick = onOpenInspector)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = Color(0xFFD4D4D8),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "${track.formatBadge.uppercase()} • ${track.sampleRate / 1000.0}kHz",
                            color = Color(0xFFEEEEF2),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Text(
                    text = formatTime(durationMs),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Playback Controls Row (Shuffle, Previous, Main Play/Pause Squircle, Next, Repeat) matching image.png
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle Button (Dark circular pill)
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    if (isShuffle) Color(0x55FFFFFF) else Color(0x30FFFFFF),
                                    if (isShuffle) Color(0x35FFFFFF) else Color(0x18FFFFFF)
                                )
                            )
                        )
                        .border(
                            1.dp,
                            if (isShuffle) Color(0x80FFFFFF) else Color(0x33FFFFFF),
                            CircleShape
                        )
                        .clickable(onClick = onToggleShuffle)
                        .testTag("control_shuffle"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Previous Button (Dark circular pill)
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0x38FFFFFF),
                                    Color(0x1EFFFFFF)
                                )
                            )
                        )
                        .border(1.dp, Color(0x35FFFFFF), CircleShape)
                        .clickable(onClick = onPrevious)
                        .testTag("control_previous"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous Track",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Main Play/Pause Button (Squircle / Rounded Rectangle, Solid White with Black Icon)
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .scale(playButtonScale)
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(22.dp),
                            ambientColor = Color(0x60000000),
                            spotColor = Color(0x50FFFFFF)
                        )
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color.White)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = Color.Black.copy(alpha = 0.2f)),
                            onClick = onTogglePlay
                        )
                        .testTag("control_play_pause"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.Black,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Next Button (Dark circular pill)
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0x38FFFFFF),
                                    Color(0x1EFFFFFF)
                                )
                            )
                        )
                        .border(1.dp, Color(0x35FFFFFF), CircleShape)
                        .clickable(onClick = onNext)
                        .testTag("control_next"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Track",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Repeat Button (Circle, Solid White when active, Dark Pill when inactive)
                val isRepeatActive = repeatMode != RepeatMode.OFF
                val repeatIcon = if (repeatMode == RepeatMode.ONE) Icons.Default.RepeatOne else Icons.Default.Repeat
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            if (isRepeatActive) {
                                SolidColor(Color.White)
                            } else {
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0x30FFFFFF),
                                        Color(0x18FFFFFF)
                                    )
                                )
                            }
                        )
                        .border(
                            1.dp,
                            if (isRepeatActive) Color.White else Color(0x33FFFFFF),
                            CircleShape
                        )
                        .clickable(onClick = onCycleRepeat)
                        .testTag("control_repeat"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = repeatIcon,
                        contentDescription = "Repeat",
                        tint = if (isRepeatActive) Color.Black else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Volume Bar matching Screenshot 2 (Mute icon, capsule slider, Loud volume icon)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeMute,
                    contentDescription = "Mute",
                    tint = Color.White,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onSetVolume(0f) }
                )

                // Sleek Capsule Volume Slider
                MinimalistVolumeBar(
                    volume = volume,
                    onVolumeChange = onSetVolume,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("volume_slider")
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Max Volume",
                    tint = Color.White,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onSetVolume(1f) }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Bottom Action Bar (Queue/Playlist, Center Paired Capsule for EQ & Specs/Timer, Lyrics)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Queue / Playlist Button
                IconButton(
                    onClick = { onAddToPlaylist(track) },
                    modifier = Modifier
                        .size(46.dp)
                        .testTag("action_btn_playlist")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                        contentDescription = "Queue & Playlists",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Center Floating Paired Capsule (Equalizer Studio + Specs / Timer)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x35FFFFFF))
                        .border(1.dp, Color(0x38FFFFFF), RoundedCornerShape(20.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onOpenEqualizer,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("action_btn_eq")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Equalizer,
                            contentDescription = "Studio Equalizer",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    IconButton(
                        onClick = onOpenInspector,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("action_btn_specs")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Specs & Audio Timer",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Lyrics / Transcript Button
                IconButton(
                    onClick = onOpenLyrics,
                    modifier = Modifier
                        .size(46.dp)
                        .testTag("action_btn_lyrics")
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Synchronized Lyrics",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MinimalistProgressBar(
    fraction: Float,
    onSeekFraction: (Float) -> Unit,
    onDragStateChange: (Boolean, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(24.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val frac = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
                    onSeekFraction(frac)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val frac = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
                        onDragStateChange(true, frac)
                    },
                    onDragEnd = {
                        onDragStateChange(false, fraction)
                    },
                    onDragCancel = {
                        onDragStateChange(false, fraction)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        val frac = (change.position.x / size.width.toFloat()).coerceIn(0f, 1f)
                        onDragStateChange(true, frac)
                    }
                )
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
        ) {
            val trackHeight = size.height
            val trackWidth = size.width
            val cornerRadius = CornerRadius(trackHeight / 2f, trackHeight / 2f)

            // Inactive dark track
            drawRoundRect(
                color = Color(0x40FFFFFF),
                topLeft = Offset(0f, 0f),
                size = Size(trackWidth, trackHeight),
                cornerRadius = cornerRadius
            )

            // Active solid light track
            val activeWidth = trackWidth * fraction
            if (activeWidth > 0) {
                drawRoundRect(
                    color = Color(0xFFE5E5EA),
                    topLeft = Offset(0f, 0f),
                    size = Size(activeWidth, trackHeight),
                    cornerRadius = cornerRadius
                )
            }
        }
    }
}

@Composable
private fun MinimalistVolumeBar(
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(24.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val frac = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
                    onVolumeChange(frac)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, _ ->
                        change.consume()
                        val frac = (change.position.x / size.width.toFloat()).coerceIn(0f, 1f)
                        onVolumeChange(frac)
                    }
                )
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
        ) {
            val trackHeight = size.height
            val trackWidth = size.width
            val cornerRadius = CornerRadius(trackHeight / 2f, trackHeight / 2f)

            // Inactive dark track
            drawRoundRect(
                color = Color(0x40FFFFFF),
                topLeft = Offset(0f, 0f),
                size = Size(trackWidth, trackHeight),
                cornerRadius = cornerRadius
            )

            // Active solid light track
            val activeWidth = trackWidth * volume.coerceIn(0f, 1f)
            if (activeWidth > 0) {
                drawRoundRect(
                    color = Color(0xFFE5E5EA),
                    topLeft = Offset(0f, 0f),
                    size = Size(activeWidth, trackHeight),
                    cornerRadius = cornerRadius
                )
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000L).coerceAtLeast(0L)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
