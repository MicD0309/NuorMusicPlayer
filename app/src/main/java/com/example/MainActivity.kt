package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.AudioTrack
import com.example.ui.MusicViewModel
import com.example.ui.NavigationTab
import com.example.ui.SearchCategory
import com.example.ui.components.AddToPlaylistDialog
import com.example.ui.components.AudioInspectorDialog
import com.example.ui.components.ConvxHomeView
import com.example.ui.components.ConvxSearchView
import com.example.ui.components.ConvxSettingsView
import com.example.ui.components.ConvxSongsView
import com.example.ui.components.CreatePlaylistDialog
import com.example.ui.components.EqualizerStudioView
import com.example.ui.components.LibraryView
import com.example.ui.components.LyricsSheet
import com.example.ui.components.MetadataEditorSheet
import com.example.ui.components.MiniPlayerBar
import com.example.ui.components.MinimalistNavBar
import com.example.ui.components.NowPlayingSheet
import com.example.ui.components.PlaylistsView
import com.example.ui.components.ThemePickerView
import com.example.ui.theme.AuraMusicTheme
import com.example.ui.theme.LocalAuraColors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AuraMusicApp()
        }
    }
}

@Composable
fun AuraMusicApp(
    viewModel: MusicViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentTheme by viewModel.selectedTheme.collectAsState()

    // State collections
    val currentTab by viewModel.currentTab.collectAsState()
    val allTracks by viewModel.allTracks.collectAsState()
    val filteredTracks by viewModel.filteredTracks.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchCategory by viewModel.searchCategory.collectAsState()
    val selectedGenre by viewModel.selectedGenre.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()
    val hiResOnly by viewModel.hiResOnly.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val activePlaylist by viewModel.activePlaylist.collectAsState()

    // Playback state
    val currentTrack by viewModel.currentTrack.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val progressMs by viewModel.currentPositionMs.collectAsState()
    val durationMs by viewModel.durationMs.collectAsState()
    val repeatMode by viewModel.repeatMode.collectAsState()
    val isShuffle by viewModel.isShuffle.collectAsState()
    val playbackSpeed by viewModel.playbackSpeed.collectAsState()
    val volume by viewModel.volume.collectAsState()
    val visualizerAmplitudes by viewModel.visualizerAmplitudes.collectAsState()
    val eqBands by viewModel.eqBands.collectAsState()
    val bassBoostStrength by viewModel.bassBoostStrength.collectAsState()
    val isVirtualizerEnabled by viewModel.isVirtualizerEnabled.collectAsState()

    // Dialog & sheet state
    val isNowPlayingExpanded by viewModel.isNowPlayingExpanded.collectAsState()
    val isLyricsOpen by viewModel.isLyricsOpen.collectAsState()
    val isAudioInspectorOpen by viewModel.isAudioInspectorOpen.collectAsState()
    val metadataEditorTrack by viewModel.metadataEditorTrack.collectAsState()
    val isPlaylistCreatorOpen by viewModel.isPlaylistCreatorOpen.collectAsState()
    val addToPlaylistTrack by viewModel.addToPlaylistTrack.collectAsState()

    var inspectorTargetTrack by remember { mutableStateOf<AudioTrack?>(null) }

    // SAF Audio File Picker
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            var displayName = "Imported Track"
            var sizeBytes = 0L
            try {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (cursor.moveToFirst()) {
                        if (nameIndex != -1) displayName = cursor.getString(nameIndex) ?: displayName
                        if (sizeIndex != -1) sizeBytes = cursor.getLong(sizeIndex)
                    }
                }
            } catch (_: Exception) {}

            val mimeType = context.contentResolver.getType(uri)
            viewModel.importAudioUri(uri, displayName, sizeBytes, mimeType)
        }
    }

    // Permission request launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        viewModel.scanStorage()
    }

    LaunchedEffect(Unit) {
        val permissionsToRequest = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_AUDIO)
            }
        } else {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    AuraMusicTheme(themeStyle = currentTheme) {
        val auraColors = LocalAuraColors.current

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            auraColors.backgroundStart,
                            auraColors.backgroundGlow.copy(alpha = 0.35f),
                            auraColors.backgroundEnd
                        )
                    )
                )
        ) {
            // Main Content Area
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    AnimatedContent(
                        targetState = currentTab,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "TabContent"
                    ) { targetTab ->
                        when (targetTab) {
                            NavigationTab.HOME -> {
                                ConvxHomeView(
                                    tracks = allTracks,
                                    currentPlayingTrack = currentTrack,
                                    isPlaying = isPlaying,
                                    onPlayTrack = viewModel::playTrack,
                                    onShuffleAll = viewModel::shuffleAll,
                                    onNavigateToSongs = { viewModel.setTab(NavigationTab.SONGS) },
                                    onNavigateToSettings = { viewModel.setTab(NavigationTab.SETTINGS) },
                                    onNavigateToSearch = { viewModel.setTab(NavigationTab.SEARCH) },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            NavigationTab.SONGS -> {
                                ConvxSongsView(
                                    tracks = filteredTracks,
                                    currentPlayingTrack = currentTrack,
                                    isPlaying = isPlaying,
                                    sortOption = sortOption,
                                    onSortSelect = viewModel::setSortOption,
                                    onPlayTrack = viewModel::playTrack,
                                    onShuffleAll = viewModel::shuffleAll,
                                    onToggleFavorite = viewModel::toggleFavorite,
                                    onOpenMetadataEditor = viewModel::openMetadataEditor,
                                    onOpenInspector = {
                                        inspectorTargetTrack = it
                                        viewModel.setAudioInspectorOpen(true)
                                    },
                                    onAddToPlaylist = viewModel::openAddToPlaylist,
                                    onScanStorage = viewModel::scanStorage,
                                    onImportFileClick = { audioPickerLauncher.launch("audio/*") },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            NavigationTab.SEARCH -> {
                                ConvxSearchView(
                                    searchQuery = searchQuery,
                                    onSearchChange = viewModel::setSearchQuery,
                                    selectedCategory = searchCategory,
                                    onSelectCategory = viewModel::setSearchCategory,
                                    tracks = allTracks,
                                    playlists = playlists,
                                    currentPlayingTrack = currentTrack,
                                    isPlaying = isPlaying,
                                    onPlayTrack = viewModel::playTrack,
                                    onToggleFavorite = viewModel::toggleFavorite,
                                    onOpenMetadataEditor = viewModel::openMetadataEditor,
                                    onOpenInspector = {
                                        inspectorTargetTrack = it
                                        viewModel.setAudioInspectorOpen(true)
                                    },
                                    onAddToPlaylist = viewModel::openAddToPlaylist,
                                    onSelectPlaylist = { entity ->
                                        viewModel.setActivePlaylist(entity)
                                        viewModel.setTab(NavigationTab.PLAYLISTS)
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            NavigationTab.SETTINGS -> {
                                ConvxSettingsView(
                                    currentTheme = currentTheme,
                                    onSelectTheme = viewModel::setTheme,
                                    onNavigateToEqualizer = { viewModel.setTab(NavigationTab.STUDIO_EQ) },
                                    onScanStorage = viewModel::scanStorage,
                                    onImportFile = { audioPickerLauncher.launch("audio/*") },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            NavigationTab.LIBRARY -> {
                                LibraryView(
                                    tracks = filteredTracks,
                                    totalTrackCount = allTracks.size,
                                    searchQuery = searchQuery,
                                    selectedGenre = selectedGenre,
                                    sortOption = sortOption,
                                    hiResOnly = hiResOnly,
                                    isHiResTab = false,
                                    currentPlayingTrack = currentTrack,
                                    isPlaying = isPlaying,
                                    onSearchChange = viewModel::setSearchQuery,
                                    onGenreSelect = viewModel::setSelectedGenre,
                                    onSortSelect = viewModel::setSortOption,
                                    onToggleHiResOnly = viewModel::toggleHiResOnly,
                                    onScanStorage = viewModel::scanStorage,
                                    onImportFileClick = { audioPickerLauncher.launch("audio/*") },
                                    onPlayTrack = viewModel::playTrack,
                                    onToggleFavorite = viewModel::toggleFavorite,
                                    onOpenMetadataEditor = viewModel::openMetadataEditor,
                                    onOpenInspector = {
                                        inspectorTargetTrack = it
                                        viewModel.setAudioInspectorOpen(true)
                                    },
                                    onAddToPlaylist = viewModel::openAddToPlaylist
                                )
                            }
                            NavigationTab.HI_RES_MASTERS -> {
                                LibraryView(
                                    tracks = filteredTracks,
                                    totalTrackCount = allTracks.count { it.isHiRes },
                                    searchQuery = searchQuery,
                                    selectedGenre = selectedGenre,
                                    sortOption = sortOption,
                                    hiResOnly = true,
                                    isHiResTab = true,
                                    currentPlayingTrack = currentTrack,
                                    isPlaying = isPlaying,
                                    onSearchChange = viewModel::setSearchQuery,
                                    onGenreSelect = viewModel::setSelectedGenre,
                                    onSortSelect = viewModel::setSortOption,
                                    onToggleHiResOnly = {},
                                    onScanStorage = viewModel::scanStorage,
                                    onImportFileClick = { audioPickerLauncher.launch("audio/*") },
                                    onPlayTrack = viewModel::playTrack,
                                    onToggleFavorite = viewModel::toggleFavorite,
                                    onOpenMetadataEditor = viewModel::openMetadataEditor,
                                    onOpenInspector = {
                                        inspectorTargetTrack = it
                                        viewModel.setAudioInspectorOpen(true)
                                    },
                                    onAddToPlaylist = viewModel::openAddToPlaylist
                                )
                            }
                            NavigationTab.PLAYLISTS -> {
                                PlaylistsView(
                                    playlists = playlists,
                                    activePlaylist = activePlaylist,
                                    allTracks = allTracks,
                                    currentPlayingTrack = currentTrack,
                                    isPlaying = isPlaying,
                                    onSelectPlaylist = viewModel::setActivePlaylist,
                                    onBackFromPlaylist = { viewModel.setActivePlaylist(null) },
                                    onCreatePlaylistClick = viewModel::openPlaylistCreator,
                                    onDeletePlaylist = viewModel::deletePlaylist,
                                    onPlayTrack = viewModel::playTrack,
                                    onToggleFavorite = viewModel::toggleFavorite,
                                    onOpenMetadataEditor = viewModel::openMetadataEditor,
                                    onOpenInspector = {
                                        inspectorTargetTrack = it
                                        viewModel.setAudioInspectorOpen(true)
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            NavigationTab.STUDIO_EQ -> {
                                EqualizerStudioView(
                                    eqBands = eqBands,
                                    bassBoostStrength = bassBoostStrength,
                                    isVirtualizerEnabled = isVirtualizerEnabled,
                                    onSetBandLevel = viewModel::setEqBandLevel,
                                    onSetBassBoost = viewModel::setBassBoost,
                                    onToggleVirtualizer = viewModel::toggleVirtualizer,
                                    onApplyPreset = viewModel::applyEqPreset,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            NavigationTab.THEMES -> {
                                ThemePickerView(
                                    currentTheme = currentTheme,
                                    onSelectTheme = viewModel::setTheme,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Navigation & Mini Player Floating Dock
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                MiniPlayerBar(
                    track = currentTrack,
                    isPlaying = isPlaying,
                    progressMs = progressMs,
                    durationMs = durationMs,
                    onExpand = { viewModel.setNowPlayingExpanded(true) },
                    onTogglePlay = viewModel::togglePlayPause,
                    onNext = viewModel::playNext
                )

                MinimalistNavBar(
                    currentTab = currentTab,
                    onTabSelected = viewModel::setTab
                )
            }

            // Full-screen Now Playing Sheet
            AnimatedVisibility(
                visible = isNowPlayingExpanded,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                NowPlayingSheet(
                    track = currentTrack,
                    isPlaying = isPlaying,
                    progressMs = progressMs,
                    durationMs = durationMs,
                    repeatMode = repeatMode,
                    isShuffle = isShuffle,
                    playbackSpeed = playbackSpeed,
                    volume = volume,
                    visualizerAmplitudes = visualizerAmplitudes,
                    onDismiss = { viewModel.setNowPlayingExpanded(false) },
                    onTogglePlay = viewModel::togglePlayPause,
                    onNext = viewModel::playNext,
                    onPrevious = viewModel::playPrevious,
                    onSeek = viewModel::seekTo,
                    onCycleRepeat = viewModel::cycleRepeatMode,
                    onToggleShuffle = viewModel::toggleShuffle,
                    onSetSpeed = viewModel::setPlaybackSpeed,
                    onSetVolume = viewModel::setVolume,
                    onToggleFavorite = viewModel::toggleFavorite,
                    onOpenLyrics = { viewModel.setLyricsOpen(true) },
                    onOpenInspector = {
                        inspectorTargetTrack = currentTrack
                        viewModel.setAudioInspectorOpen(true)
                    },
                    onOpenMetadataEditor = { track ->
                        viewModel.openMetadataEditor(track)
                    },
                    onOpenEqualizer = {
                        viewModel.setNowPlayingExpanded(false)
                        viewModel.setTab(NavigationTab.STUDIO_EQ)
                    },
                    onAddToPlaylist = { track ->
                        viewModel.openAddToPlaylist(track)
                    }
                )
            }

            // Lyrics Sheet
            if (isLyricsOpen) {
                LyricsSheet(
                    track = currentTrack,
                    currentPositionMs = progressMs,
                    onDismiss = { viewModel.setLyricsOpen(false) },
                    onEditLyrics = {
                        viewModel.setLyricsOpen(false)
                        currentTrack?.let { viewModel.openMetadataEditor(it) }
                    },
                    onSeekTo = viewModel::seekTo
                )
            }

            // Audio Format & DAC Inspector
            if (isAudioInspectorOpen) {
                AudioInspectorDialog(
                    track = inspectorTargetTrack ?: currentTrack,
                    onDismiss = { viewModel.setAudioInspectorOpen(false) }
                )
            }

            // Metadata Editor Sheet
            metadataEditorTrack?.let { track ->
                MetadataEditorSheet(
                    track = track,
                    onDismiss = viewModel::closeMetadataEditor,
                    onSave = viewModel::saveMetadata
                )
            }

            // Create Playlist Dialog
            if (isPlaylistCreatorOpen) {
                CreatePlaylistDialog(
                    onDismiss = viewModel::closePlaylistCreator,
                    onCreate = viewModel::createPlaylist
                )
            }

            // Add to Playlist Dialog
            addToPlaylistTrack?.let { track ->
                AddToPlaylistDialog(
                    track = track,
                    playlists = playlists,
                    onDismiss = viewModel::closeAddToPlaylist,
                    onAddToPlaylist = viewModel::addTrackToPlaylist,
                    onCreateNewPlaylist = {
                        viewModel.closeAddToPlaylist()
                        viewModel.openPlaylistCreator()
                    }
                )
            }
        }
    }
}
