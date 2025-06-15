package com.example.playlistmaker.domain.use_case.playlist

import com.example.playlistmaker.domain.entitie.Playlist

interface ShareUseCase {
    suspend fun share(playlist: Playlist)
}
