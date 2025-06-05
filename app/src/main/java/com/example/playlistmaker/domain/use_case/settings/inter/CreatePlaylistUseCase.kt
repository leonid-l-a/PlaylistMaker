package com.example.playlistmaker.domain.use_case.settings.inter

import com.example.playlistmaker.domain.entitie.PlaylistCreationData

interface CreatePlaylistUseCase {
    suspend operator fun invoke(data: PlaylistCreationData)
}
