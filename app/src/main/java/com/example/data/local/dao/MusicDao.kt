package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.PlaylistEntity
import com.example.data.local.entity.TrackMetadataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {
    @Query("SELECT * FROM track_metadata")
    fun getAllMetadataFlow(): Flow<List<TrackMetadataEntity>>

    @Query("SELECT * FROM track_metadata WHERE trackId = :trackId")
    suspend fun getMetadataById(trackId: String): TrackMetadataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMetadata(metadata: TrackMetadataEntity)

    @Query("UPDATE track_metadata SET isFavorite = :isFavorite WHERE trackId = :trackId")
    suspend fun updateFavorite(trackId: String, isFavorite: Boolean)

    @Query("UPDATE track_metadata SET playCount = playCount + 1, lastPlayedTimestamp = :timestamp WHERE trackId = :trackId")
    suspend fun incrementPlayCount(trackId: String, timestamp: Long)

    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    fun getAllPlaylistsFlow(): Flow<List<PlaylistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Query("DELETE FROM playlists WHERE playlistId = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)
}
