package com.example.playlistmaker.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.presentation.player.PlayerState
import com.example.playlistmaker.presentation.player.PlayerViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Suppress("UNCHECKED_CAST")
class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private val viewModel: PlayerViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("DEPRECATION") val track = intent.getParcelableExtra<Track>("track")!!
                return PlayerViewModel(Creator.providePlayerInteractor(), track) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupUI()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    private fun setupUI() {
        binding.searchScreenToolbar.setNavigationOnClickListener { finish() }
        binding.ibPlay.setOnClickListener { viewModel.playbackControl() }

        viewModel.trackData.observe(this) { trackData ->
            with(binding) {
                tvTrackName.text = trackData.trackName
                tvArtistName.text = trackData.artistName
                trackDurability.text = trackData.duration

                trackAlbumNameText.visibility =
                    if (trackData.collectionName.isNullOrEmpty()) View.GONE else View.VISIBLE
                trackAlbumName.visibility = trackAlbumNameText.visibility
                trackAlbumName.text = trackData.collectionName

                trackYear.text = trackData.year
                trackGenre.text = trackData.genre
                trackCountry.text = trackData.country


                Glide.with(this@PlayerActivity)
                    .load(trackData.artworkUrl ?: R.drawable.ph_no_track_image)
                    .placeholder(R.drawable.ph_no_track_image)
                    .transform(RoundedCorners(8))
                    .into(trackImage)
            }
        }
    }

    private fun setupObservers() {
        viewModel.playerState.observe(this) { state ->
            when (state) {
                is PlayerState.Preparing -> {
                    binding.ibPlay.isEnabled = false
                    binding.timePlayed.text = getString(R.string.time_start)
                }

                is PlayerState.Ready -> {
                    binding.ibPlay.isEnabled = true
                }

                is PlayerState.Playing -> {
                    binding.timePlayed.text =
                        SimpleDateFormat("mm:ss", Locale.getDefault()).format(state.remainingMillis)
                    binding.ibPlay.setImageResource(R.drawable.ic_pause)
                }

                is PlayerState.Paused -> {
                    binding.ibPlay.setImageResource(R.drawable.ic_play)
                }

                is PlayerState.Completed -> {
                    binding.timePlayed.text = getString(R.string.time_zero)
                    binding.ibPlay.setImageResource(R.drawable.ic_play)
                }

                is PlayerState.Error -> {
                    finish()
                    Toast.makeText(this, "Unexpected error", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}