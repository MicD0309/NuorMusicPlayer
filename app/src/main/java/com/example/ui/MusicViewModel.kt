package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.MusicDatabase
import com.example.data.local.entity.PlaylistEntity
import com.example.data.model.AudioTrack
import com.example.data.repository.MusicRepository
import com.example.player.AudioEngine
import com.example.player.EqBand
import com.example.player.RepeatMode
import com.example.ui.theme.AuraThemeStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NavigationTab(val title: String) {
    HOME("Home"),
    SONGS("Songs"),
    LIBRARY("Library"),
    SETTINGS("Settings"),
    SEARCH("Search"),
    PLAYLISTS("Playlists"),
    HI_RES_MASTERS("Hi-Res Masters"),
    STUDIO_EQ("Studio EQ"),
    THEMES("Themes")
}

enum class SearchCategory(val displayName: String) {
    ALL("All"),
    SONGS("Songs"),
    ALBUMS("Albums"),
    ARTISTS("Artists"),
    PLAYLISTS("Playlists")
}

enum class SortOption(val displayName: String) {
    TITLE("Title (A-Z)"),
    ARTIST("Artist"),
    ALBUM("Album"),
    DURATION("Duration"),
    RESOLUTION("Resolution / Bitrate"),
    DATE_ADDED("Recently Added")
}

class MusicViewModel(application: Application) : AndroidViewModel(application) {
    private val database = MusicDatabase.getInstance(application)
    private val repository = MusicRepository(application, database.musicDao())
    val audioEngine = AudioEngine(application)

    val currentTrack: StateFlow<AudioTrack?> = audioEngine.currentTrack
    val isPlaying: StateFlow<Boolean> = audioEngine.isPlaying
    val currentPositionMs: StateFlow<Long> = audioEngine.currentPositionMs
    val durationMs: StateFlow<Long> = audioEngine.durationMs
    val repeatMode: StateFlow<RepeatMode> = audioEngine.repeatMode
    val isShuffle: StateFlow<Boolean> = audioEngine.isShuffle
    val playbackSpeed: StateFlow<Float> = audioEngine.playbackSpeed
    val volume: StateFlow<Float> = audioEngine.volume
    val visualizerAmplitudes: StateFlow<List<Float>> = audioEngine.visualizerAmplitudes
    val eqBands: StateFlow<List<EqBand>> = audioEngine.eqBands
    val bassBoostStrength: StateFlow<Int> = audioEngine.bassBoostStrength
    val isVirtualizerEnabled: StateFlow<Boolean> = audioEngine.isVirtualizerEnabled

    val allTracks: StateFlow<List<AudioTrack>> = repository.allTracksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val playlists: StateFlow<List<PlaylistEntity>> = repository.playlistsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentTab = MutableStateFlow(NavigationTab.HOME)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchCategory = MutableStateFlow(SearchCategory.ALL)
    val searchCategory: StateFlow<SearchCategory> = _searchCategory.asStateFlow()

    private val _selectedGenre = MutableStateFlow("All")
    val selectedGenre: StateFlow<String> = _selectedGenre.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.TITLE)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _hiResOnly = MutableStateFlow(false)
    val hiResOnly: StateFlow<Boolean> = _hiResOnly.asStateFlow()

    private val _selectedTheme = MutableStateFlow(AuraThemeStyle.CONVX_DARK)
    val selectedTheme: StateFlow<AuraThemeStyle> = _selectedTheme.asStateFlow()

    // Sheet / Modal visibility states
    private val _isNowPlayingExpanded = MutableStateFlow(false)
    val isNowPlayingExpanded: StateFlow<Boolean> = _isNowPlayingExpanded.asStateFlow()

    private val _isLyricsOpen = MutableStateFlow(false)
    val isLyricsOpen: StateFlow<Boolean> = _isLyricsOpen.asStateFlow()

    private val _isAudioInspectorOpen = MutableStateFlow(false)
    val isAudioInspectorOpen: StateFlow<Boolean> = _isAudioInspectorOpen.asStateFlow()

    private val _metadataEditorTrack = MutableStateFlow<AudioTrack?>(null)
    val metadataEditorTrack: StateFlow<AudioTrack?> = _metadataEditorTrack.asStateFlow()

    private val _isPlaylistCreatorOpen = MutableStateFlow(false)
    val isPlaylistCreatorOpen: StateFlow<Boolean> = _isPlaylistCreatorOpen.asStateFlow()

    private val _addToPlaylistTrack = MutableStateFlow<AudioTrack?>(null)
    val addToPlaylistTrack: StateFlow<AudioTrack?> = _addToPlaylistTrack.asStateFlow()

    private val _activePlaylist = MutableStateFlow<PlaylistEntity?>(null)
    val activePlaylist: StateFlow<PlaylistEntity?> = _activePlaylist.asStateFlow()

    private data class FilterState(
        val query: String,
        val genre: String,
        val sort: SortOption,
        val hiresOnly: Boolean,
        val tab: NavigationTab,
        val activePl: PlaylistEntity?
    )

    private val _filterState: StateFlow<FilterState> = combine(
        combine(_searchQuery, _selectedGenre, _sortOption) { q, g, s -> Triple(q, g, s) },
        combine(_hiResOnly, _currentTab, _activePlaylist) { h, t, p -> Triple(h, t, p) }
    ) { (q, g, s), (h, t, p) ->
        FilterState(q, g, s, h, t, p)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FilterState("", "All", SortOption.TITLE, false, NavigationTab.LIBRARY, null))

    // Filtered and Sorted Tracks
    val filteredTracks: StateFlow<List<AudioTrack>> = combine(
        allTracks,
        _filterState
    ) { tracks, filter ->
        var list = tracks

        if (filter.tab == NavigationTab.HI_RES_MASTERS) {
            list = list.filter { it.isHiRes }
        }

        if (filter.activePl != null && filter.tab == NavigationTab.PLAYLISTS) {
            val ids = filter.activePl.trackIdsJson.removeSurrounding("[", "]")
                .split(",")
                .map { it.trim().removeSurrounding("\"") }
                .toSet()
            list = list.filter { ids.contains(it.id) }
        }

        if (filter.hiresOnly && filter.tab != NavigationTab.HI_RES_MASTERS) {
            list = list.filter { it.isHiRes }
        }

        if (filter.genre != "All") {
            list = list.filter { it.genre.contains(filter.genre, ignoreCase = true) }
        }

        if (filter.query.isNotBlank()) {
            list = list.filter {
                it.title.contains(filter.query, ignoreCase = true) ||
                it.artist.contains(filter.query, ignoreCase = true) ||
                it.album.contains(filter.query, ignoreCase = true) ||
                it.genre.contains(filter.query, ignoreCase = true)
            }
        }

        when (filter.sort) {
            SortOption.TITLE -> list.sortedBy { it.title.lowercase() }
            SortOption.ARTIST -> list.sortedBy { it.artist.lowercase() }
            SortOption.ALBUM -> list.sortedBy { it.album.lowercase() }
            SortOption.DURATION -> list.sortedByDescending { it.durationMs }
            SortOption.RESOLUTION -> list.sortedByDescending { it.sampleRate * it.bitDepth }
            SortOption.DATE_ADDED -> list.sortedByDescending { it.dateAdded }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        audioEngine.setOnTrackCompletedListener {
            playNext()
        }
        scanStorage()
    }

    fun setTab(tab: NavigationTab) {
        _currentTab.value = tab
        if (tab != NavigationTab.PLAYLISTS) {
            _activePlaylist.value = null
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSearchCategory(category: SearchCategory) {
        _searchCategory.value = category
    }

    fun shuffleAll() {
        val tracks = allTracks.value
        if (tracks.isNotEmpty()) {
            val randomTrack = tracks.random()
            audioEngine.playTrack(randomTrack)
        }
    }

    fun setSelectedGenre(genre: String) {
        _selectedGenre.value = genre
    }

    fun setSortOption(sort: SortOption) {
        _sortOption.value = sort
    }

    fun toggleHiResOnly() {
        _hiResOnly.value = !_hiResOnly.value
    }

    fun setTheme(theme: AuraThemeStyle) {
        _selectedTheme.value = theme
    }

    fun setNowPlayingExpanded(expanded: Boolean) {
        _isNowPlayingExpanded.value = expanded
    }

    fun setLyricsOpen(open: Boolean) {
        _isLyricsOpen.value = open
    }

    fun setAudioInspectorOpen(open: Boolean) {
        _isAudioInspectorOpen.value = open
    }

    fun openMetadataEditor(track: AudioTrack) {
        _metadataEditorTrack.value = track
    }

    fun closeMetadataEditor() {
        _metadataEditorTrack.value = null
    }

    fun openPlaylistCreator() {
        _isPlaylistCreatorOpen.value = true
    }

    fun closePlaylistCreator() {
        _isPlaylistCreatorOpen.value = false
    }

    fun openAddToPlaylist(track: AudioTrack) {
        _addToPlaylistTrack.value = track
    }

    fun closeAddToPlaylist() {
        _addToPlaylistTrack.value = null
    }

    fun setActivePlaylist(playlist: PlaylistEntity?) {
        _activePlaylist.value = playlist
    }

    fun playTrack(track: AudioTrack) {
        audioEngine.playTrack(track)
    }

    fun togglePlayPause() {
        if (currentTrack.value == null && filteredTracks.value.isNotEmpty()) {
            playTrack(filteredTracks.value.first())
        } else {
            audioEngine.togglePlayPause()
        }
    }

    fun seekTo(positionMs: Long) {
        audioEngine.seekTo(positionMs)
    }

    fun playNext() {
        val tracks = filteredTracks.value.ifEmpty { allTracks.value }
        if (tracks.isEmpty()) return

        val current = currentTrack.value
        val currentIndex = tracks.indexOfFirst { it.id == current?.id }

        val nextTrack = if (isShuffle.value) {
            tracks.random()
        } else if (currentIndex != -1 && currentIndex < tracks.size - 1) {
            tracks[currentIndex + 1]
        } else {
            tracks.first()
        }
        audioEngine.playTrack(nextTrack)
    }

    fun playPrevious() {
        if (currentPositionMs.value > 3000L) {
            seekTo(0L)
            return
        }
        val tracks = filteredTracks.value.ifEmpty { allTracks.value }
        if (tracks.isEmpty()) return

        val current = currentTrack.value
        val currentIndex = tracks.indexOfFirst { it.id == current?.id }

        val prevTrack = if (isShuffle.value) {
            tracks.random()
        } else if (currentIndex > 0) {
            tracks[currentIndex - 1]
        } else {
            tracks.last()
        }
        audioEngine.playTrack(prevTrack)
    }

    fun cycleRepeatMode() = audioEngine.cycleRepeatMode()
    fun toggleShuffle() = audioEngine.toggleShuffle()
    fun setPlaybackSpeed(speed: Float) = audioEngine.setSpeed(speed)
    fun setVolume(vol: Float) = audioEngine.setVolume(vol)

    fun toggleFavorite(track: AudioTrack) {
        viewModelScope.launch {
            repository.toggleFavorite(track.id, track.isFavorite)
        }
    }

    fun saveMetadata(
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
    ) {
        viewModelScope.launch {
            repository.updateTrackMetadata(
                trackId = trackId,
                title = title,
                artist = artist,
                album = album,
                genre = genre,
                year = year,
                trackNumber = trackNumber,
                lyrics = lyrics,
                rating = rating,
                coverPreset = coverPreset
            )
            // If current playing track was updated, refresh it
            val current = currentTrack.value
            if (current != null && current.id == trackId) {
                // Update in memory
            }
            closeMetadataEditor()
        }
    }

    fun createPlaylist(name: String, desc: String, coverPreset: Int = 0) {
        viewModelScope.launch {
            repository.createPlaylist(name, desc, coverPreset)
            closePlaylistCreator()
        }
    }

    fun addTrackToPlaylist(playlist: PlaylistEntity, track: AudioTrack) {
        viewModelScope.launch {
            repository.addTrackToPlaylist(playlist, track.id)
            closeAddToPlaylist()
        }
    }

    fun removeTrackFromPlaylist(playlist: PlaylistEntity, trackId: String) {
        viewModelScope.launch {
            repository.removeTrackFromPlaylist(playlist, trackId)
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            if (_activePlaylist.value?.playlistId == playlistId) {
                _activePlaylist.value = null
            }
            repository.deletePlaylist(playlistId)
        }
    }

    fun scanStorage() {
        viewModelScope.launch {
            repository.scanDeviceAudioFiles()
        }
    }

    fun importAudioUri(uri: Uri, displayName: String?, sizeBytes: Long, mimeType: String?) {
        viewModelScope.launch {
            repository.importAudioUri(uri, displayName, sizeBytes, mimeType)
        }
    }

    // Studio & Equalizer
    fun setEqBandLevel(bandIndex: Int, levelMb: Int) = audioEngine.setEqBandLevel(bandIndex, levelMb)
    fun setBassBoost(strength: Int) = audioEngine.setBassBoost(strength)
    fun toggleVirtualizer() = audioEngine.toggleVirtualizer()
    fun applyEqPreset(presetName: String) = audioEngine.applyEqPreset(presetName)

    override fun onCleared() {
        super.onCleared()
        audioEngine.release()
    }
}
