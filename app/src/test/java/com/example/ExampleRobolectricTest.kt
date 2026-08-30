package com.example

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.MusicDatabase
import com.example.data.local.entity.PlaylistEntity
import com.example.data.local.entity.TrackMetadataEntity
import com.example.data.model.AudioTrack
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Aura Music", appName)
    }

    @Test
    fun `audio track hi res badge computation`() {
        val hiResTrack = AudioTrack(
            id = "test_1",
            uri = "content://media/external/audio/media/1",
            title = "Aura Cyberpunk Dream",
            artist = "Neon Skyline",
            album = "Neural Horizons",
            durationMs = 210000L,
            sampleRate = 96000,
            bitDepth = 24,
            bitrateKbps = 4608,
            mimeType = "audio/flac",
            isHiRes = true
        )

        assertTrue(hiResTrack.isHiRes)
        assertTrue(hiResTrack.formatBadge.contains("Hi-Res"))
        assertTrue(hiResTrack.formatBadge.contains("24-Bit / 96kHz"))
    }

    @Test
    fun `room database insert and query metadata`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val db = MusicDatabase.getInstance(context)
        val dao = db.musicDao()

        val entity = TrackMetadataEntity(
            trackId = "track_demo_1",
            customTitle = "Custom Master Track",
            customArtist = "Aura Artist",
            customAlbum = "Master Album",
            customGenre = "Hi-Res Ambient",
            customYear = 2026,
            customTrackNumber = 1,
            customLyrics = "[00:00.00] Echoes in frosted glass",
            isFavorite = true,
            rating = 5,
            customCoverPreset = 1
        )

        dao.upsertMetadata(entity)
        val fetched = dao.getMetadataById("track_demo_1")

        assertNotNull(fetched)
        assertEquals("Custom Master Track", fetched?.customTitle)
        assertEquals(5, fetched?.rating)
        assertTrue(fetched?.isFavorite == true)
    }

    @Test
    fun `room database playlist creation and track additions`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val db = MusicDatabase.getInstance(context)
        val dao = db.musicDao()

        val playlist = PlaylistEntity(
            name = "Late Night Audiophile",
            description = "Studio master tracks for night listening",
            coverPreset = 0,
            trackIdsJson = "[\"synth_1\",\"synth_2\"]"
        )

        val id = dao.insertPlaylist(playlist)
        assertTrue(id > 0)

        val playlists = dao.getAllPlaylistsFlow().first()
        assertTrue(playlists.any { it.name == "Late Night Audiophile" })
    }
}
