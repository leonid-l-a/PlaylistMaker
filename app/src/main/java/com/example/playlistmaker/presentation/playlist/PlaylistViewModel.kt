package com.example.playlistmaker.presentation.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.domain.use_case.playlist.ShareUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val shareUseCase: ShareUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(PlaylistState())
    val state: StateFlow<PlaylistState> = _state.asStateFlow()

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistWithTracks(playlistId)
            val duration = playlist.tracks.sumOf { it.trackTimeMillis.toMinutes() }
            _state.update {
                it.copy(
                    playlist = playlist, totalDuration = duration, isMenuVisible = it.isMenuVisible
                )
            }
        }
    }

    fun setMenuVisibility(isVisible: Boolean) {
        _state.update { it.copy(isMenuVisible = isVisible) }
    }

    suspend fun removeTrackFromPlaylistWithCleanup(track: Track) {
        playlistInteractor.removeTrackFromPlaylistWithCleanup(
            state.value.playlist!!.id, track.trackId
        )
    }

    suspend fun deletePlaylist() {
        playlistInteractor.deletePlaylist(state.value.playlist!!.id)
    }

    private fun String.toMinutes(): Int = split(":").first().toIntOrNull() ?: 0

    fun share() {
        viewModelScope.launch { shareUseCase.share(state.value.playlist!!) }
    }

}