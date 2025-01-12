package com.example.playlistmaker

import android.os.Bundle
import android.view.View
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
}