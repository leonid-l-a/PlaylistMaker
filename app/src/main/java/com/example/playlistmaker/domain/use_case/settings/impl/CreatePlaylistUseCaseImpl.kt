package com.example.playlistmaker.domain.use_case.settings.impl

import com.example.playlistmaker.domain.entitie.PlaylistCreationData
import com.example.playlistmaker.domain.repository.PlaylistRepository
import com.example.playlistmaker.domain.use_case.settings.inter.CreatePlaylistUseCase

class CreatePlaylistUseCaseImpl(
    private val playlistRepository: PlaylistRepository
) : CreatePlaylistUseCase {

    override suspend fun invoke(data: PlaylistCreationData) {
        playlistRepository.createPlaylist(data)
    }
}
