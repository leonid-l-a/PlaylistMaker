package com.example.playlistmaker.presentation.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.domain.interactor.FavoriteInteractor
import com.example.playlistmaker.domain.interactor.PlayerInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favoriteInteractor: FavoriteInteractor,
    private val track: Track,
) : ViewModel() {

    companion object {
        private const val MAX_TRACK_DURATION = 30_000L
        private const val UPDATE_INTERVAL = 300L
    }

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Preparing)
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val _trackData = MutableStateFlow<TrackData?>(null)
    val trackData: StateFlow<TrackData?> = _trackData.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private var updateJob: Job? = null

    init {
        loadTrackData()
        preparePlayer()
        observeFavorites()
    }

    fun playbackControl() {
        if (playerInteractor.isPlaying()) pausePlayer() else startPlayer()
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            if (_isFavorite.value) {
                favoriteInteractor.deleteTrackFromFavorites(track)
            } else {
                favoriteInteractor.addTrackToFavorites(track)
            }
        }
    }

    override fun onCleared() {
        releasePlayer()
        super.onCleared()
    }

    private fun loadTrackData() {
        _trackData.value = TrackData(
            trackName      = track.trackName,
            artistName     = track.artistName,
            duration       = track.trackTimeMillis,
            artworkUrl     = track.artworkUrl100?.replaceAfterLast("/", "512x512bb.jpg"),
            collectionName = track.collectionName,
            year           = track.releaseDate.take(4),
            genre          = track.primaryGenreName,
            country        = track.country,
            isFavorite     = track.isFavorite
        )
    }

    private fun preparePlayer() {
        _playerState.value = PlayerState.Preparing
        playerInteractor.prepare(
            track.previewUrl,
            onPrepared   = { _playerState.value = PlayerState.Ready },
            onCompletion = {
                _playerState.value = PlayerState.Completed
                resetPlayer()
            }
        )
    }

    private fun startPlayer() {
        playerInteractor.play()
        _playerState.value = PlayerState.Playing(0, MAX_TRACK_DURATION)
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            while (isActive) {
                updatePlaybackTime()
                delay(UPDATE_INTERVAL)
            }
        }
    }

    fun pausePlayer() {
        playerInteractor.pause()
        _playerState.value = PlayerState.Paused
        updateJob?.cancel()
    }

    fun releasePlayer() {
        playerInteractor.release()
        updateJob?.cancel()
    }

    private fun resetPlayer() {
        _playerState.value = PlayerState.Paused
        updateJob?.cancel()
    }

    private fun updatePlaybackTime() {
        val elapsed = playerInteractor.getCurrentTime().toLong()
        val remaining = MAX_TRACK_DURATION - elapsed
        _playerState.value = PlayerState.Playing(elapsed, remaining)
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoriteInteractor.getFavorites().collectLatest { favList ->
                val isFav = favList.any { it.trackId == track.trackId }
                _isFavorite.value = isFav
                track.isFavorite = isFav
            }
        }
    }
}
