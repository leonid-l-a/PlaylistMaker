package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import java.util.Locale

class PlayerActivity : BaseActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private lateinit var binding: ActivityPlayerBinding
    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private var shouldPlayOnPrepared = false

    private var trackDurationMillis: Long = 30000L

    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            if (mediaPlayer.isPlaying) {
                val remainingMillis = trackDurationMillis - mediaPlayer.currentPosition
                if (remainingMillis > 0) {
                    val minutes = remainingMillis / 60000
                    val seconds = (remainingMillis % 60000) / 1000
                    binding.timePlayed.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
                    handler.postDelayed(this, 1000)
                } else {
                    binding.timePlayed.text = getString(R.string.time_zero)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val track = intent.getParcelableExtra<Track>("track")!!
        updateUI(track)

        binding.timePlayed.text = getString(R.string.time_start)

        preparePlayer(track.previewUrl)
        binding.ibPlay.isEnabled = false

        binding.searchScreenToolbar.setNavigationOnClickListener {
            finish()
        }

        binding.ibPlay.setOnClickListener {
            playbackControl()
        }
    }

    private fun updateUI(track: Track) = with(binding) {
        tvTrackName.text = track.trackName
        tvArtistName.text = track.artistName
        trackDurability.text = getString(R.string.time_start)

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

        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this@PlayerActivity)
                .load(imageUrl)
                .placeholder(R.drawable.ph_no_track_image)
                .transform(RoundedCorners(8))
                .into(binding.trackImage)
        } else {
            Glide.with(this@PlayerActivity)
                .load(R.drawable.ph_no_track_image)
                .transform(RoundedCorners(8))
                .into(binding.trackImage)
        }
    }

    private fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            binding.ibPlay.isEnabled = true
            playerState = STATE_PREPARED
            if (shouldPlayOnPrepared) {
                startPlayer()
                shouldPlayOnPrepared = false
            }
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            binding.ibPlay.setImageResource(R.drawable.ic_play)
            binding.timePlayed.text = getString(R.string.time_zero)
            handler.removeCallbacks(updateRunnable)
        }
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
            else -> {
                shouldPlayOnPrepared = true
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        binding.ibPlay.setImageResource(R.drawable.ic_pause)
        handler.post(updateRunnable)
    }

    private fun pausePlayer() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
        playerState = STATE_PAUSED
        binding.ibPlay.setImageResource(R.drawable.ic_play)
        handler.removeCallbacks(updateRunnable)
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacks(updateRunnable)
    }
}
