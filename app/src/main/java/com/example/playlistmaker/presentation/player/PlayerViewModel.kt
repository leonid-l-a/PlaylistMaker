package com.example.playlistmaker.presentation.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.domain.entitie.toTrackPlaylistsEntity
import com.example.playlistmaker.domain.interactor.FavoriteInteractor
import com.example.playlistmaker.domain.interactor.PlayerInteractor
import com.example.playlistmaker.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.service.PlayerServiceInterface
import com.example.playlistmaker.service.PlayerServiceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class PlayerViewModel(
    private val favoriteInteractor: FavoriteInteractor,
    private val track: Track,
    private val playlistInteractor: PlaylistInteractor,
    private val playerInteractor: PlayerInteractor
) : ViewModel() {

    companion object {
        private const val MAX_TRACK_DURATION = 30_000L
    }

    private val _playerState = MutableStateFlow(
        PlayerScreenState(
            status = PlayerScreenState.Status.PREPARING,
            elapsedMillis = 0L,
            remainingMillis = MAX_TRACK_DURATION,
            formattedTime = "00:00",
            isPlaying = false,
            isFavorite = track.isFavorite,
            trackData = null,
            errorMessage = null,
            playlists = null,
            addTrackResult = null
        )
    )
    val playerScreenState: StateFlow<PlayerScreenState> = _playerState

    private var playerService: PlayerServiceInterface? = null

    init {
        loadTrackData()
        observeFavorites()
        setPlaylists()
    }

    fun setService(service: PlayerServiceInterface) {
        playerService = service
        observeServiceState()
    }

    private fun observeServiceState() {
        viewModelScope.launch {
            playerService?.getPlayerStateFlow()?.collect { state ->
                _playerState.update { old ->
                    val status = when (state.status) {
                        PlayerServiceState.Status.PREPARING -> PlayerScreenState.Status.PREPARING
                        PlayerServiceState.Status.PLAYING -> PlayerScreenState.Status.PLAYING
                        PlayerServiceState.Status.PAUSED -> PlayerScreenState.Status.PAUSED
                        PlayerServiceState.Status.COMPLETED -> PlayerScreenState.Status.COMPLETED
                    }
                    val formatted = when (state.status) {
                        PlayerServiceState.Status.PREPARING,
                        PlayerServiceState.Status.COMPLETED,
                            -> "00:00"

                        else -> {
                            val elapsedSec = state.elapsedMillis / 1000
                            val minutes = elapsedSec / 60
                            val seconds = elapsedSec % 60
                            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
                        }
                    }
                    old.copy(
                        status = status,
                        elapsedMillis = state.elapsedMillis,
                        remainingMillis = state.remainingMillis,
                        formattedTime = formatted,
                        isPlaying = (state.status == PlayerServiceState.Status.PLAYING)
                    )
                }
            }
        }
    }

    fun playbackControl() {
        playerService?.let {
            if (it.isPlaying()) it.pause() else it.play()
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            if (_playerState.value.isFavorite) {
                favoriteInteractor.deleteTrackFromFavorites(track)
            } else {
                favoriteInteractor.addTrackToFavorites(track)
            }
        }
    }

    private fun loadTrackData() {
        val data = TrackData(
            trackName = track.trackName,
            artistName = track.artistName,
            duration = track.trackTimeMillis,
            artworkUrl = track.artworkUrl100?.replaceAfterLast("/", "512x512bb.jpg"),
            collectionName = track.collectionName,
            year = track.releaseDate.take(4),
            genre = track.primaryGenreName,
            country = track.country,
            isFavorite = track.isFavorite
        )
        _playerState.update { it.copy(trackData = data) }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoriteInteractor.getFavorites().collectLatest { favList ->
                val isFav = favList.any { it.trackId == track.trackId }
                _playerState.update { old -> old.copy(isFavorite = isFav) }
            }
        }
    }

    fun setPlaylists() {
        viewModelScope.launch {
            val playlists = playlistInteractor.getAllPlaylists()
            _playerState.update { it.copy(playlists = playlists) }
        }
    }

    fun addTrackToPlaylist(playlist: PlaylistEntity) {
        viewModelScope.launch {
            val result = playlistInteractor.addTrackToPlaylist(
                track.toTrackPlaylistsEntity(),
                playlist.playlistId!!
            )
            if (result) {
                val updatedPlaylists = playlistInteractor.getAllPlaylists()
                _playerState.update { old ->
                    old.copy(
                        addTrackResult = PlayerScreenState.AddTrackResult(
                            success = true,
                            playlistName = playlist.playlistName
                        ),
                        playlists = updatedPlaylists
                    )
                }
            } else {
                _playerState.update { old ->
                    old.copy(
                        addTrackResult = PlayerScreenState.AddTrackResult(
                            success = false,
                            playlistName = playlist.playlistName
                        )
                    )
                }
            }
        }
    }

    fun resetAddTrackResult() {
        _playerState.update { it.copy(addTrackResult = null) }
    }

    fun releasePlayer() {
        playerInteractor.release()
    }
}
