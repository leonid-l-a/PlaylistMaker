package com.example.playlistmaker.presentation.library

import com.example.playlistmaker.domain.entitie.Track

sealed class FavoritesState {
    object Empty : FavoritesState()
    data class Favorites(val tracks: List<Track>): FavoritesState()
}