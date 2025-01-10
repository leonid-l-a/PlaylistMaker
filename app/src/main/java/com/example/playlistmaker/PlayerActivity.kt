package com.example.playlistmaker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val track = intent.getParcelableExtra<Track>("track")
        if (track != null) {
            updateUI(track)
        }

        binding.searchScreenToolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun updateUI(track: Track) {
        binding.tvTrackName.text = track.trackName
        binding.tvArtistName.text = track.artistName
        binding.trackDurability.text = track.getFormattedTrackTime()
        binding.trackAlbumName.text = track.collectionName ?: "Unknown Album"
        binding.trackYear.text = track.releaseDate.take(4)
        binding.trackGenre.text = track.primaryGenreName
        binding.trackCountry.text = track.country

        val imageUrl = track.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg")
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ph_no_track_image)
            .transform(RoundedCorners(8))
            .into(binding.trackImage)
    }
}