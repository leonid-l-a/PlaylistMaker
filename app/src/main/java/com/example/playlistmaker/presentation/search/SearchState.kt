package com.example.playlistmaker.presentation.search

import com.example.playlistmaker.domain.entitie.Track

sealed class SearchState {
    object Loading : SearchState()
    data class Success(val tracks: List<Track>) : SearchState()
    data class History(val tracks: List<Track>) : SearchState()
    object Empty : SearchState()
    data class Error(val message: String) : SearchState()
}