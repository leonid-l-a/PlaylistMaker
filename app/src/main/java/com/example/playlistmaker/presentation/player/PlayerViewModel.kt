package com.example.playlistmaker.presentation.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.domain.entitie.toTrackPlaylistsEntity
import com.example.playlistmaker.domain.interactor.FavoriteInteractor
import com.example.playlistmaker.domain.interactor.PlayerInteractor
import com.example.playlistmaker.domain.interactor.PlaylistInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favoriteInteractor: FavoriteInteractor,
    private val track: Track,
    private val playlistInteractor: PlaylistInteractor,
) : ViewModel() {

    companion object {
        private const val MAX_TRACK_DURATION = 30_000L
        private const val UPDATE_INTERVAL = 300L
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
            errorMessage = null
        )
    )
    val playerScreenState: StateFlow<PlayerScreenState> = _playerState.asStateFlow()

    private var updateJob: Job? = null

    init {
        loadTrackData()

        preparePlayer()

        observeFavorites()

        setPlaylists()
    }

    fun playbackControl() {
        if (playerInteractor.isPlaying()) {
            pausePlayer()
        } else {
            startPlayer()
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

    override fun onCleared() {
        releasePlayer()
        super.onCleared()
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

        _playerState.update { old ->
            old.copy(trackData = data)
        }
    }

    private fun preparePlayer() {
        _playerState.update { old ->
            old.copy(
                status = PlayerScreenState.Status.PREPARING,
                formattedTime = "00:30",
                errorMessage = null
            )
        }

        playerInteractor.prepare(
            track.previewUrl,
            onPrepared = {
                _playerState.update { old ->
                    old.copy(
                        status = PlayerScreenState.Status.READY,
                        formattedTime = "00:30",
                        errorMessage = null
                    )
                }
            },
            onCompletion = {
                _playerState.update { old ->
                    old.copy(
                        status = PlayerScreenState.Status.COMPLETED,
                        isPlaying = false,
                        elapsedMillis = MAX_TRACK_DURATION,
                        remainingMillis = 0L,
                        formattedTime = "00:30",
                        errorMessage = null
                    )
                }
                updateJob?.cancel()
            }
        )
    }

    private fun startPlayer() {
        playerInteractor.play()
        _playerState.update { old ->
            old.copy(
                status = PlayerScreenState.Status.PLAYING,
                isPlaying = true,
                errorMessage = null
            )
        }

        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            while (isActive) {
                val elapsed = playerInteractor.getCurrentTime().toLong()
                val remaining = (MAX_TRACK_DURATION - elapsed).coerceAtLeast(0L)

                val minutes = TimeUnit.MILLISECONDS.toMinutes(remaining) % 60
                val seconds = TimeUnit.MILLISECONDS.toSeconds(remaining) % 60
                val formatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

                _playerState.update { old ->
                    old.copy(
                        status = PlayerScreenState.Status.PLAYING,
                        elapsedMillis = elapsed,
                        remainingMillis = remaining,
                        formattedTime = formatted,
                        isPlaying = true,
                        errorMessage = null,
                    )
                }
                delay(UPDATE_INTERVAL)
            }
        }
    }

    fun pausePlayer() {
        playerInteractor.pause()
        _playerState.update { old ->
            old.copy(
                status = PlayerScreenState.Status.PAUSED,
                isPlaying = false,
                errorMessage = null
            )
        }
        updateJob?.cancel()
    }

    fun releasePlayer() {
        playerInteractor.release()
        updateJob?.cancel()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoriteInteractor.getFavorites().collectLatest { favList ->
                val isFav = favList.any { it.trackId == track.trackId }
                _playerState.update { old ->
                    old.copy(isFavorite = isFav)
                }
            }
        }
    }

    fun setPlaylists() {
        viewModelScope.launch {
            val playlists = getPlaylists()
            _playerState.update { old ->
                old.copy(
                    playlists = playlists
                )
            }

        }
    }

    suspend fun getPlaylists(): List<PlaylistEntity> {
        return playlistInteractor.getAllPlaylists()
    }

    fun addTrackToPlaylist(playlist: PlaylistEntity) {
        viewModelScope.launch {
            playlistInteractor.addTrackToPlaylist(
                track.toTrackPlaylistsEntity(),
                playlist.playlistId!!
            )
        }
    }
}
