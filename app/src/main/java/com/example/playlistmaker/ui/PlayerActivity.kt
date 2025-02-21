package com.example.playlistmaker.ui

import android.os.*
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.domain.interactor.PlayerInteractor
import com.example.playlistmaker.presentation.BaseActivity
import java.util.*

class PlayerActivity : BaseActivity() {

    companion object {
        private const val MAX_TRACK_DURATION = 30_000L
    }

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var playerInteractor: PlayerInteractor
    private val handler = Handler(Looper.getMainLooper())
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            updatePlaybackTime()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playerInteractor = Creator.providePlayerInteractor()

        val track = intent.getParcelableExtra<Track>("track")!!
        updateUI(track)

        binding.timePlayed.text = getString(R.string.time_start)

        playerInteractor.prepare(track.previewUrl,
            onPrepared = { binding.ibPlay.isEnabled = true },
            onCompletion = { resetPlayer() }
        )

        binding.searchScreenToolbar.setNavigationOnClickListener { finish() }
        binding.ibPlay.setOnClickListener { playbackControl() }
    }

    private fun updateUI(track: Track) = with(binding) {
        tvTrackName.text = track.trackName
        tvArtistName.text = track.artistName
        trackDurability.text = track.trackTimeMillis

        if (track.collectionName.isNullOrEmpty()) {
            trackAlbumNameText.visibility = View.GONE
            trackAlbumName.visibility = View.GONE
        } else {
            trackAlbumNameText.visibility = View.VISIBLE
            trackAlbumName.visibility = View.VISIBLE
            trackAlbumName.text = track.collectionName
        }

        trackYear.text = track.releaseDate.take(4)
        trackGenre.text = track.primaryGenreName
        trackCountry.text = track.country

        val imageUrl = track.artworkUrl100?.replaceAfterLast("/", "512x512bb.jpg")

        Glide.with(this@PlayerActivity)
            .load(imageUrl ?: R.drawable.ph_no_track_image)
            .placeholder(R.drawable.ph_no_track_image)
            .transform(RoundedCorners(8))
            .into(binding.trackImage)
    }

    private fun playbackControl() {
        if (playerInteractor.isPlaying()) {
            pausePlayer()
        } else {
            startPlayer()
        }
    }

    private fun startPlayer() {
        playerInteractor.play()
        binding.ibPlay.setImageResource(R.drawable.ic_pause)
        handler.post(updateTimeRunnable)
    }

    private fun pausePlayer() {
        playerInteractor.pause()
        binding.ibPlay.setImageResource(R.drawable.ic_play)
        handler.removeCallbacks(updateTimeRunnable)
    }

    private fun updatePlaybackTime() {
        val elapsedMillis = playerInteractor.getCurrentTime().toLong()
        val remainingMillis = MAX_TRACK_DURATION - elapsedMillis
        val minutes = remainingMillis / 60000
        val seconds = (remainingMillis % 60000) / 1000
        binding.timePlayed.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    private fun resetPlayer() {
        binding.ibPlay.setImageResource(R.drawable.ic_play)
        binding.timePlayed.text = getString(R.string.time_zero)
        handler.removeCallbacks(updateTimeRunnable)
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerInteractor.release()
        handler.removeCallbacks(updateTimeRunnable)
    }
}


