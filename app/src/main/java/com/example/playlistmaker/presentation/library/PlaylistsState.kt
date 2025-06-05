package com.example.playlistmaker.presentation.library

import com.example.playlistmaker.data.db.PlaylistEntity

sealed class PlaylistsState {
    object Empty : PlaylistsState()
    data class Playlists(val playlists: List<PlaylistEntity>): PlaylistsState()
}