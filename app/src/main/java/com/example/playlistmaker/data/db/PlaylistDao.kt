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
    suspend fun getPlaylistWithTracks(playlistId: Long): PlaylistWithTracks {
        val playlist = getPlaylistById(playlistId)
        val tracks = getTracksForPlaylistSorted(playlistId)
        return PlaylistWithTracks(playlist = playlist, tracks = tracks)
    }

    @Query(" SELECT t.* FROM tracks_playlists_table AS t INNER JOIN playlist_track_cross_ref AS cr ON t.trackId = cr.trackId WHERE cr.playlistId = :playlistId ORDER BY cr.addedAt DESC ")
    suspend fun getTracksForPlaylistSorted(playlistId: Long): List<TrackPlaylistsEntity>

    @Query("SELECT * FROM playlist_table")
    suspend fun getAllPlaylists(): List<PlaylistEntity>

    @Query("SELECT playlistId FROM playlist_track_cross_ref WHERE trackId = :trackId")
    suspend fun getPlaylistIdsForTrack(trackId: Long): List<Long>

    @Query("SELECT t.* FROM tracks_playlists_table AS t INNER JOIN playlist_track_cross_ref AS cr ON t.trackId = cr.trackId WHERE cr.playlistId = :playlistId")
    suspend fun getTracksForPlaylist(playlistId: Long): List<TrackPlaylistsEntity>

    private suspend fun deleteTrackIfOrphaned(trackId: Long) {
        val playlistIds = getPlaylistIdsForTrack(trackId)
        if (playlistIds.isEmpty()) {
            deleteTrack(trackId)
        }
    }

    @Transaction
    suspend fun removeTrackFromPlaylistWithCleanup(playlistId: Long, trackId: Long) {
        removeTrackFromPlaylist(playlistId, trackId)

        val playlist = getPlaylistById(playlistId)
        val newCount = playlist.trackCount - 1
        updatePlaylist(playlist.copy(trackCount = newCount))

        deleteTrackIfOrphaned(trackId)
    }

    @Transaction
    @Query("SELECT trackId FROM playlist_track_cross_ref WHERE playlistId = :playlistId")
    suspend fun getTrackIdsForPlaylist(playlistId: Long): List<Long>

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
            deleteTrackIfOrphaned(trackId)
        }
        deletePlaylistById(playlistId)
    }


    @Transaction
    suspend fun insertTrackAndUpdateCount(track: TrackPlaylistsEntity, playlistId: Long): Boolean {
        val existingRef = getPlaylistTrackCrossRef(playlistId, track.trackId)

        if (existingRef == null) {
            insertTrack(track)
            insertPlaylistTrackCrossRef(
                PlaylistTrackCrossRef(
                    playlistId = playlistId,
                    trackId = track.trackId,
                    addedAt = System.currentTimeMillis()
                )
            )

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
