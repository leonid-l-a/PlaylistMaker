package com.example.playlistmaker.presentation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactor.FavoriteInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class FavoritesViewModel(
    private val favoriteInteractor: FavoriteInteractor
) : ViewModel() {

    private val _favoritesState = MutableStateFlow<FavoritesState>(FavoritesState.Empty)
    val favoritesState: StateFlow<FavoritesState> = _favoritesState.asStateFlow()

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoriteInteractor.getFavorites()
                .collect { tracks ->
                    if (tracks.isEmpty()) {
                        _favoritesState.value = FavoritesState.Empty
                    } else {
                        _favoritesState.value = FavoritesState.Favorites(tracks)
                    }
                }
        }
    }
}