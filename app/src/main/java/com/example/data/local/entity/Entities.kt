package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_metadata")
data class TrackMetadataEntity(
    @PrimaryKey val trackId: String,
    val customTitle: String? = null,
    val customArtist: String? = null,
    val customAlbum: String? = null,
    val customGenre: String? = null,
    val customYear: Int? = null,
    val customTrackNumber: Int? = null,
    val customLyrics: String? = null,
    val isFavorite: Boolean = false,
    val rating: Int = 0,
    val customCoverPreset: Int = 0,
    val playCount: Int = 0,
    val lastPlayedTimestamp: Long = 0L
)

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val playlistId: Long = 0L,
    val name: String,
    val description: String = "",
    val coverPreset: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val trackIdsJson: String = "[]"
)
