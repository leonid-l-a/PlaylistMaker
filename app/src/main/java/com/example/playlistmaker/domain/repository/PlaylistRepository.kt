package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.data.db.TrackPlaylistsEntity
import com.example.playlistmaker.domain.entitie.PlaylistCreationData

interface PlaylistRepository {
    suspend fun createPlaylist(data: PlaylistCreationData)
    suspend fun addTrackToPlaylist(track: TrackPlaylistsEntity, playlistId: Long): Boolean
    suspend fun removeTrackFromPlaylist(trackId: Long, playlistId: Long)
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun getPlaylists() : List<PlaylistEntity>
}
