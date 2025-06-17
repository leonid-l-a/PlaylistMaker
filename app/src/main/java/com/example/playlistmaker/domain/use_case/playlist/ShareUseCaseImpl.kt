package com.example.playlistmaker.domain.use_case.playlist

import com.example.playlistmaker.domain.entitie.Playlist
import com.example.playlistmaker.domain.repository.ShareRepository

class ShareUseCaseImpl(
    private val repository: ShareRepository,
    private val mapper: PlaylistMapper
): ShareUseCase {
    override suspend fun share(playlist: Playlist) {
        val playlistWithTracks = mapper.map(playlist)
        repository.share(playlistWithTracks)
    }
}