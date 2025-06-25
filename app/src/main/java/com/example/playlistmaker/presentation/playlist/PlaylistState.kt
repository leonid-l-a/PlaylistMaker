package com.example.playlistmaker.presentation.playlist

import com.example.playlistmaker.domain.entitie.Playlist

data class PlaylistState(
    val playlist: Playlist? = null,
    val totalDuration: Int = 0,
    val isMenuVisible: Boolean = false
)