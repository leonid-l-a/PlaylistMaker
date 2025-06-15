package com.example.playlistmaker.domain.use_case.playlist

import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.domain.repository.PlaylistRepository

class UpdatePlaylistUseCaseImpl(
    val playlistRepository: PlaylistRepository
): UpdatePlaylistUseCase {
    override suspend fun updatePlaylist(playlist: PlaylistEntity) {
        playlistRepository.updatePlaylist(playlist)
    }
}