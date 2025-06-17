package com.example.playlistmaker.domain.use_case.playlist

import com.example.playlistmaker.data.db.PlaylistEntity

interface UpdatePlaylistUseCase {
    suspend fun updatePlaylist(playlist: PlaylistEntity)
}