package com.example.playlistmaker.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.presentation.BaseActivity
import com.example.playlistmaker.presentation.player.PlayerState
import com.example.playlistmaker.presentation.player.PlayerViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerActivity : BaseActivity() {

    private val binding: ActivityPlayerBinding by lazy { ActivityPlayerBinding.inflate(layoutInflater) }

    private val viewModel: PlayerViewModel by viewModel {
        @Suppress("DEPRECATION")
        parametersOf(intent.getParcelableExtra<Track>("track")!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
