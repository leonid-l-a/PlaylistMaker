package com.example.playlistmaker.presentation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactor.PlaylistInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _state = MutableStateFlow<PlaylistsState>(PlaylistsState.Empty)
    val state: StateFlow<PlaylistsState> = _state.asStateFlow()

    init {
        loadPlaylists()
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            try {
                val playlists = playlistInteractor.getAllPlaylists()
                _state.value = if (playlists.isEmpty()) {
                    PlaylistsState.Empty
                } else {
                    PlaylistsState.Playlists(playlists)
                }
            } catch (_: Exception) {
                _state.value = PlaylistsState.Empty
            }
        }
    }
}