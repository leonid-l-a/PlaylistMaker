package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import java.util.Locale

class PlayerActivity : BaseActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private var playerState = PlayerState.DEFAULT
    private var mediaPlayer = MediaPlayer()
    private var shouldPlayOnPrepared = false

    private var trackDurationMillis: Long = 30000L

    private var countDownTimer: CountDownTimer? = null

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

    private fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            binding.ibPlay.isEnabled = true
            playerState = PlayerState.PREPARED
            if (shouldPlayOnPrepared) {
                startPlayer()
                shouldPlayOnPrepared = false
            }
        }
        mediaPlayer.setOnCompletionListener {
            playerState = PlayerState.PREPARED
            binding.ibPlay.setImageResource(R.drawable.ic_play)
            binding.timePlayed.text = getString(R.string.time_zero)
            cancelCountDownTimer()
        }
    }

    private fun playbackControl() {
        when (playerState) {
            PlayerState.PLAYING -> pausePlayer()
            PlayerState.PREPARED, PlayerState.PAUSED -> startPlayer()
            else -> {
                shouldPlayOnPrepared = true
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = PlayerState.PLAYING
        binding.ibPlay.setImageResource(R.drawable.ic_pause)

        cancelCountDownTimer()

        val remainingMillis = trackDurationMillis - mediaPlayer.currentPosition
        countDownTimer = object : CountDownTimer(remainingMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 60000
                val seconds = (millisUntilFinished % 60000) / 1000
                binding.timePlayed.text =
                    String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                binding.timePlayed.text = getString(R.string.time_zero)
            }
        }
        countDownTimer?.start()
    }

    private fun pausePlayer() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
        playerState = PlayerState.PAUSED
        binding.ibPlay.setImageResource(R.drawable.ic_play)
        cancelCountDownTimer()
    }

    private fun cancelCountDownTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        cancelCountDownTimer()
    }
}


