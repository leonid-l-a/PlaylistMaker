package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.data.db.TrackPlaylistsEntity

interface PlaylistInteractor {
    suspend fun addTrackToPlaylist(track: TrackPlaylistsEntity, playlistId: Long): Boolean
    suspend fun removeTrackFromPlaylist(trackId: Long, playlistId: Long)
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun getAllPlaylists(): List<PlaylistEntity>
}
