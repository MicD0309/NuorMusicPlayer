package com.example.data.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.data.local.dao.MusicDao
import com.example.data.local.entity.PlaylistEntity
import com.example.data.local.entity.TrackMetadataEntity
import com.example.data.model.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext

class MusicRepository(
    private val context: Context,
    private val musicDao: MusicDao
) {
    // Custom imported URIs (via SAF document picker)
    private val customImportedTracks = MutableStateFlow<List<AudioTrack>>(emptyList())

    // Scanned device storage tracks
    private val scannedDeviceTracks = MutableStateFlow<List<AudioTrack>>(emptyList())

    // Bundled Master High-Res Audiophile Tracks
    private val defaultHiResTracks = listOf(
        AudioTrack(
            id = "aura_hires_1",
            uri = "aura://track_1",
            title = "Starlight Horizon (Hi-Res Master)",
            artist = "Aura Sound Ensemble",
            album = "Celestial Harmonics Vol. 1",
            durationMs = 214000L,
            sampleRate = 96000,
            bitDepth = 24,
            bitrateKbps = 4608,
            formatBadge = "Hi-Res LOSSLESS 24-Bit / 96kHz FLAC",
            isHiRes = true,
            genre = "Hi-Res Ambient",
            year = 2026,
            trackNumber = 1,
            discNumber = 1,
            lyrics = """
                [00:04.00] In the quiet expanse of the night
                [00:12.50] Signals pulse through waves of light
                [00:22.00] Floating above the sapphire sky
                [00:31.20] In high-res lossless frequency we fly
                [00:42.00] Pure acoustic waves in twenty-four bit
                [00:54.00] Every harmonic perfectly lit
                [01:06.00] Timeless resonance in stereo sound
                [01:18.00] Where sonic wonders are forever found
            """.trimIndent(),
            isFavorite = true,
            rating = 5,
            coverPreset = 0
        ),
        AudioTrack(
            id = "aura_hires_2",
            uri = "aura://track_2",
            title = "Cyberpunk Prism 2099",
            artist = "Neo Tokyo Soundlab",
            album = "Synthetic Horizon 2099",
            durationMs = 198000L,
            sampleRate = 192000,
            bitDepth = 24,
            bitrateKbps = 9216,
            formatBadge = "Hi-Res Studio Master 24-Bit / 192kHz",
            isHiRes = true,
            genre = "Synthwave / Cyberpunk",
            year = 2026,
            trackNumber = 2,
            discNumber = 1,
            lyrics = """
                [00:03.00] Neon rain upon the glass
                [00:10.00] Watching cyber shadows pass
                [00:18.00] Analog circuits in digital flow
                [00:26.00] 192 kilohertz ultraviolet glow
                [00:35.00] Feel the sub-bass rumble deep
                [00:44.00] In a city that never sleeps
            """.trimIndent(),
            isFavorite = false,
            rating = 5,
            coverPreset = 1
        ),
        AudioTrack(
            id = "aura_hires_3",
            uri = "aura://track_3",
            title = "Quantum Resonance in D-Minor",
            artist = "Dr. S. K. Vance & Orchestra",
            album = "Spatial Acoustics & Timbre",
            durationMs = 245000L,
            sampleRate = 352800,
            bitDepth = 32,
            bitrateKbps = 11289,
            formatBadge = "Direct Stream Digital DSD 5.6MHz",
            isHiRes = true,
            genre = "Neo-Classical Spatial",
            year = 2025,
            trackNumber = 3,
            discNumber = 1,
            lyrics = """
                [00:06.00] (Instrumental spatial overture)
                [00:20.00] Harmonic overtone resonance
                [00:45.00] Binaural chamber depth
                [01:15.00] Dynamic range: 128 dB Ultra High Fidelity
            """.trimIndent(),
            isFavorite = true,
            rating = 5,
            coverPreset = 2
        ),
        AudioTrack(
            id = "aura_hires_4",
            uri = "aura://track_4",
            title = "Acoustic Mirage (Liquid Velvet)",
            artist = "Ethereal Strings Duo",
            album = "Analog Strings & Timber",
            durationMs = 186000L,
            sampleRate = 96000,
            bitDepth = 24,
            bitrateKbps = 4608,
            formatBadge = "Hi-Res LOSSLESS 24-Bit / 96kHz",
            isHiRes = true,
            genre = "Acoustic / Instrumental",
            year = 2026,
            trackNumber = 4,
            discNumber = 1,
            lyrics = """
                [00:05.00] Finger-picked nylon warmth
                [00:15.00] Crisp harmonics in high fidelity
                [00:30.00] Uncompressed master audio direct
            """.trimIndent(),
            isFavorite = false,
            rating = 4,
            coverPreset = 0
        ),
        AudioTrack(
            id = "aura_hires_5",
            uri = "aura://track_5",
            title = "Midnight Reverie (Spatial Mix)",
            artist = "Luna Ray",
            album = "Nightfall Holograms",
            durationMs = 225000L,
            sampleRate = 48000,
            bitDepth = 24,
            bitrateKbps = 2304,
            formatBadge = "Apple Lossless ALAC 24-Bit / 48kHz",
            isHiRes = true,
            genre = "Dream Pop / Lo-Fi",
            year = 2026,
            trackNumber = 5,
            discNumber = 1,
            lyrics = """
                [00:04.00] Distant stars in purple haze
                [00:12.00] Lost in endless sonic maze
                [00:22.00] Soft whispers on the breeze
                [00:32.00] Gliding through the quiet trees
            """.trimIndent(),
            isFavorite = false,
            rating = 4,
            coverPreset = 1
        ),
        AudioTrack(
            id = "aura_hires_6",
            uri = "aura://track_6",
            title = "Solaris Velocity",
            artist = "Apex Frequency",
            album = "Kinetic Soundscapes",
            durationMs = 205000L,
            sampleRate = 192000,
            bitDepth = 24,
            bitrateKbps = 9216,
            formatBadge = "Studio Master FLAC 24-Bit / 192kHz",
            isHiRes = true,
            genre = "Electronic / Techno",
            year = 2026,
            trackNumber = 6,
            discNumber = 1,
            lyrics = """
                [00:08.00] Pulse generator initializing
                [00:18.00] 192 kHz lossless bandwidth
                [00:36.00] Accelerating to lightspeed audio
            """.trimIndent(),
            isFavorite = true,
            rating = 5,
            coverPreset = 2
        )
    )

    // Reactive tracks flow merging default tracks + scanned files + custom files with Room metadata overrides
    val allTracksFlow: Flow<List<AudioTrack>> = combine(
        scannedDeviceTracks,
        customImportedTracks,
        musicDao.getAllMetadataFlow()
    ) { scanned, imported, metadataList ->
        val metadataMap = metadataList.associateBy { it.trackId }
        val rawList = defaultHiResTracks + imported + scanned

        rawList.map { track ->
            val meta = metadataMap[track.id]
            if (meta != null) {
                track.copy(
                    title = meta.customTitle ?: track.title,
                    artist = meta.customArtist ?: track.artist,
                    album = meta.customAlbum ?: track.album,
                    genre = meta.customGenre ?: track.genre,
                    year = meta.customYear ?: track.year,
                    trackNumber = meta.customTrackNumber ?: track.trackNumber,
                    lyrics = meta.customLyrics ?: track.lyrics,
                    isFavorite = meta.isFavorite,
                    rating = if (meta.rating > 0) meta.rating else track.rating,
                    coverPreset = if (meta.customCoverPreset != 0) meta.customCoverPreset else track.coverPreset
                )
            } else {
                track
            }
        }
    }

    val playlistsFlow: Flow<List<PlaylistEntity>> = musicDao.getAllPlaylistsFlow()

    suspend fun scanDeviceAudioFiles() = withContext(Dispatchers.IO) {
        val tracks = mutableListOf<AudioTrack>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.DATE_ADDED
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

        try {
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder
            )

            cursor?.use {
                val idCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val sizeCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val mimeCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)
                val yearCol = it.getColumnIndex(MediaStore.Audio.Media.YEAR)
                val trackCol = it.getColumnIndex(MediaStore.Audio.Media.TRACK)
                val dateAddedCol = it.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)

                var count = 0
                while (it.moveToNext()) {
                    val id = it.getLong(idCol)
                    val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id).toString()
                    val title = it.getString(titleCol) ?: "Audio Track $count"
                    val artist = it.getString(artistCol) ?: "Unknown Artist"
                    val album = it.getString(albumCol) ?: "Local Music"
                    val durationMs = it.getLong(durationCol)
                    val sizeBytes = it.getLong(sizeCol)
                    val mimeType = it.getString(mimeCol) ?: "audio/mpeg"
                    val year = if (yearCol != -1) it.getInt(yearCol) else 2026
                    val trackNum = if (trackCol != -1) it.getInt(trackCol) else (count + 1)
                    val dateAdded = if (dateAddedCol != -1) it.getLong(dateAddedCol) * 1000L else System.currentTimeMillis()

                    val isFlacOrLossless = mimeType.contains("flac", ignoreCase = true) ||
                            mimeType.contains("wav", ignoreCase = true) ||
                            mimeType.contains("alac", ignoreCase = true)

                    val badge = when {
                        mimeType.contains("flac", ignoreCase = true) -> "Hi-Res LOSSLESS 24-Bit / 96kHz FLAC"
                        mimeType.contains("wav", ignoreCase = true) -> "Hi-Res WAV 24-Bit / 96kHz PCM"
                        mimeType.contains("alac", ignoreCase = true) -> "Apple Lossless ALAC 24-Bit"
                        mimeType.contains("ogg", ignoreCase = true) -> "Ogg Vorbis 320 kbps"
                        mimeType.contains("aac", ignoreCase = true) -> "AAC 320 kbps High Quality"
                        else -> "MP3 Audio 320 kbps"
                    }

                    tracks.add(
                        AudioTrack(
                            id = "local_$id",
                            uri = uri,
                            title = title,
                            artist = if (artist.contains("<unknown>", ignoreCase = true)) "Local Artist" else artist,
                            album = if (album.contains("<unknown>", ignoreCase = true)) "Device Audio" else album,
                            durationMs = if (durationMs > 0) durationMs else 180000L,
                            sizeBytes = sizeBytes,
                            mimeType = mimeType,
                            sampleRate = if (isFlacOrLossless) 96000 else 44100,
                            bitDepth = if (isFlacOrLossless) 24 else 16,
                            bitrateKbps = if (isFlacOrLossless) 4608 else 320,
                            formatBadge = badge,
                            isHiRes = isFlacOrLossless,
                            genre = if (isFlacOrLossless) "Hi-Res Lossless" else "Local Music",
                            year = if (year > 0) year else 2026,
                            trackNumber = trackNum,
                            coverPreset = count % 3,
                            dateAdded = dateAdded
                        )
                    )
                    count++
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        scannedDeviceTracks.value = tracks
    }

    suspend fun importAudioUri(uri: Uri, displayName: String?, sizeBytes: Long, mimeType: String?) = withContext(Dispatchers.IO) {
        val cleanName = displayName?.substringBeforeLast(".") ?: "Imported Track"
        val mime = mimeType ?: "audio/flac"
        val isHiRes = mime.contains("flac", true) || mime.contains("wav", true) || displayName?.endsWith(".flac", true) == true || displayName?.endsWith(".wav", true) == true

        val newTrack = AudioTrack(
            id = "imported_${System.currentTimeMillis()}_${(0..9999).random()}",
            uri = uri.toString(),
            title = cleanName,
            artist = "Local Studio",
            album = "Imported Audio Library",
            durationMs = 210000L,
            sizeBytes = sizeBytes,
            mimeType = mime,
            sampleRate = if (isHiRes) 96000 else 44100,
            bitDepth = if (isHiRes) 24 else 16,
            bitrateKbps = if (isHiRes) 4608 else 320,
            formatBadge = if (isHiRes) "Hi-Res LOSSLESS 24-Bit / 96kHz" else "Master Audio 320 kbps",
            isHiRes = isHiRes,
            genre = "Hi-Res Studio",
            year = 2026,
            trackNumber = customImportedTracks.value.size + 1,
            coverPreset = (customImportedTracks.value.size) % 3,
            dateAdded = System.currentTimeMillis()
        )

        customImportedTracks.value = customImportedTracks.value + newTrack
    }

    suspend fun updateTrackMetadata(
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
    ) = withContext(Dispatchers.IO) {
        val existing = musicDao.getMetadataById(trackId)
        val updated = (existing ?: TrackMetadataEntity(trackId = trackId)).copy(
            customTitle = title,
            customArtist = artist,
            customAlbum = album,
            customGenre = genre,
            customYear = year,
            customTrackNumber = trackNumber,
            customLyrics = lyrics,
            rating = rating,
            customCoverPreset = coverPreset
        )
        musicDao.upsertMetadata(updated)
    }

    suspend fun toggleFavorite(trackId: String, currentFavorite: Boolean) = withContext(Dispatchers.IO) {
        val existing = musicDao.getMetadataById(trackId)
        val updated = (existing ?: TrackMetadataEntity(trackId = trackId)).copy(
            isFavorite = !currentFavorite
        )
        musicDao.upsertMetadata(updated)
    }

    suspend fun createPlaylist(name: String, description: String, coverPreset: Int = 0): Long = withContext(Dispatchers.IO) {
        val playlist = PlaylistEntity(
            name = name,
            description = description,
            coverPreset = coverPreset
        )
        musicDao.insertPlaylist(playlist)
    }

    suspend fun addTrackToPlaylist(playlist: PlaylistEntity, trackId: String) = withContext(Dispatchers.IO) {
        val currentIds = parseJsonArray(playlist.trackIdsJson).toMutableList()
        if (!currentIds.contains(trackId)) {
            currentIds.add(trackId)
            val updatedJson = toJsonArray(currentIds)
            musicDao.updatePlaylist(playlist.copy(trackIdsJson = updatedJson))
        }
    }

    suspend fun removeTrackFromPlaylist(playlist: PlaylistEntity, trackId: String) = withContext(Dispatchers.IO) {
        val currentIds = parseJsonArray(playlist.trackIdsJson).toMutableList()
        if (currentIds.remove(trackId)) {
            val updatedJson = toJsonArray(currentIds)
            musicDao.updatePlaylist(playlist.copy(trackIdsJson = updatedJson))
        }
    }

    suspend fun deletePlaylist(playlistId: Long) = withContext(Dispatchers.IO) {
        musicDao.deletePlaylist(playlistId)
    }

    private fun parseJsonArray(json: String): List<String> {
        val cleaned = json.trim().removeSurrounding("[", "]")
        if (cleaned.isBlank()) return emptyList()
        return cleaned.split(",").map { it.trim().removeSurrounding("\"") }.filter { it.isNotBlank() }
    }

    private fun toJsonArray(list: List<String>): String {
        return "[" + list.joinToString(",") { "\"$it\"" } + "]"
    }
}
