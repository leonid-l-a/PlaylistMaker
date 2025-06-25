package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.data.db.TrackPlaylistsEntity
import com.example.playlistmaker.domain.entitie.Playlist
import com.example.playlistmaker.domain.entitie.Track

interface PlaylistInteractor {
    suspend fun addTrackToPlaylist(track: TrackPlaylistsEntity, playlistId: Long): Boolean
    suspend fun removeTrackFromPlaylist(trackId: Long, playlistId: Long)
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun getAllPlaylists(): List<PlaylistEntity>
    suspend fun getPlaylistWithTracks(playlistId: Long): Playlist
    suspend fun removeTrackFromPlaylistWithCleanup(playlistId: Long, trackId: Long)
    suspend fun getTracksForPlaylist(playlistId: Long): List<Track>
}
