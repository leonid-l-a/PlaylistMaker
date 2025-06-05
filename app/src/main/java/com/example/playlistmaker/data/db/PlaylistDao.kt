package com.example.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: TrackPlaylistsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistTrackCrossRef(crossRef: PlaylistTrackCrossRef)

    @Transaction
    @Query("SELECT * FROM playlist_table")
    suspend fun getAllPlaylistsWithTracks(): List<PlaylistWithTracks>

    @Transaction
    @Query("SELECT * FROM playlist_table WHERE playlistId = :playlistId")
    suspend fun getPlaylistWithTracks(playlistId: Long): PlaylistWithTracks

    @Query("SELECT * FROM playlist_table")
    suspend fun getAllPlaylists(): List<PlaylistEntity>

    @Transaction
    @Query("SELECT trackId FROM playlist_track_cross_ref WHERE playlistId = :playlistId")
    suspend fun getTrackIdsForPlaylist(playlistId: Long): List<Long>

    @Query("SELECT trackId FROM playlist_track_cross_ref WHERE trackId = :trackId")
    suspend fun getAllRefsForTrack(trackId: Long): List<Long>

    @Query("DELETE FROM playlist_track_cross_ref WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)

    @Query("DELETE FROM playlist_track_cross_ref WHERE playlistId = :playlistId")
    suspend fun deleteAllCrossRefsForPlaylist(playlistId: Long)

    @Query("DELETE FROM tracks_playlists_table WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: Long)

    @Query("DELETE FROM playlist_table WHERE playlistId = :playlistId")
    suspend fun deletePlaylistById(playlistId: Long)

    @Transaction
    suspend fun deletePlaylistWithCleanup(playlistId: Long) {
        val trackIds = getTrackIdsForPlaylist(playlistId)

        deleteAllCrossRefsForPlaylist(playlistId)

        for (trackId in trackIds) {
            val refs = getAllRefsForTrack(trackId)
            if (refs.isEmpty()) {
                deleteTrack(trackId)
            }
        }

        deletePlaylistById(playlistId)
    }

    @Transaction
    suspend fun insertTrackAndUpdateCount(track: TrackPlaylistsEntity, playlistId: Long): Boolean {
        val existingRef = getPlaylistTrackCrossRef(playlistId, track.trackId)

        if (existingRef == null) {
            insertTrack(track)
            insertPlaylistTrackCrossRef(PlaylistTrackCrossRef(playlistId, track.trackId))

            val playlist = getPlaylistById(playlistId)
            updatePlaylist(playlist.copy(trackCount = playlist.trackCount + 1))
            return true
        }
        return false
    }

    @Query("SELECT * FROM playlist_track_cross_ref WHERE playlistId = :playlistId AND trackId = :trackId LIMIT 1")
    suspend fun getPlaylistTrackCrossRef(playlistId: Long, trackId: Long): PlaylistTrackCrossRef?

    @Query("SELECT * FROM playlist_table WHERE playlistId = :playlistId LIMIT 1")
    suspend fun getPlaylistById(playlistId: Long): PlaylistEntity

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)
}
