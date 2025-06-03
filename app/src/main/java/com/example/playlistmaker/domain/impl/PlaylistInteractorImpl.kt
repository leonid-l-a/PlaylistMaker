package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.data.db.TrackPlaylistsEntity
import com.example.playlistmaker.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.domain.repository.PlaylistRepository

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository
) : PlaylistInteractor {

    override suspend fun addTrackToPlaylist(track: TrackPlaylistsEntity, playlistId: Long): Boolean {
        return repository.addTrackToPlaylist(track, playlistId)
    }

    override suspend fun removeTrackFromPlaylist(trackId: Long, playlistId: Long) {
        repository.removeTrackFromPlaylist(trackId, playlistId)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        repository.deletePlaylist(playlistId)
    }

    override suspend fun getAllPlaylists(): List<PlaylistEntity> {
        return repository.getPlaylists()
    }
}
