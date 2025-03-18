package com.example.playlistmaker.presentation.player

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.domain.interactor.PlayerInteractor

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val track: Track
) : ViewModel() {

    companion object {
        private const val MAX_TRACK_DURATION = 30_000L
    }

    private val handler = Handler(Looper.getMainLooper())
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            updatePlaybackTime()
            handler.postDelayed(this, 1000)
        }
    }

    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> = _playerState

    private val _trackData = MutableLiveData<TrackData>()
    val trackData: LiveData<TrackData> = _trackData

    init {
        loadTrackData()
        preparePlayer()
    }

    fun playbackControl() {
        if (playerInteractor.isPlaying()) {
            pausePlayer()
        } else {
            startPlayer()
        }
    }

    override fun onCleared() {
        releasePlayer()
        super.onCleared()
    }

    private fun loadTrackData() {
        _trackData.postValue(
            TrackData(
                trackName = track.trackName,
                artistName = track.artistName,
                duration = track.trackTimeMillis,
                artworkUrl = track.artworkUrl100?.replaceAfterLast("/", "512x512bb.jpg"),
                collectionName = track.collectionName,
                year = track.releaseDate.take(4),
                genre = track.primaryGenreName,
                country = track.country
            )
        )
    }

    private fun preparePlayer() {
        _playerState.postValue(PlayerState.Preparing)
        playerInteractor.prepare(
            track.previewUrl,
            onPrepared = { _playerState.postValue(PlayerState.Ready) },
            onCompletion = {
                _playerState.postValue(PlayerState.Completed)
                resetPlayer()
            }
        )
    }

    private fun startPlayer() {
        playerInteractor.play()
        _playerState.postValue(PlayerState.Playing(0, MAX_TRACK_DURATION))
        handler.post(updateTimeRunnable)
    }

    fun pausePlayer() {
        playerInteractor.pause()
        _playerState.postValue(PlayerState.Paused)
        handler.removeCallbacks(updateTimeRunnable)
    }

    fun releasePlayer() {
        playerInteractor.release()
        handler.removeCallbacks(updateTimeRunnable)
    }

    private fun resetPlayer() {
        _playerState.postValue(PlayerState.Paused)
        handler.removeCallbacks(updateTimeRunnable)
    }

    private fun updatePlaybackTime() {
        val elapsedMillis = playerInteractor.getCurrentTime().toLong()
        val remainingMillis = MAX_TRACK_DURATION - elapsedMillis
        _playerState.postValue(PlayerState.Playing(elapsedMillis, remainingMillis))
    }
}

