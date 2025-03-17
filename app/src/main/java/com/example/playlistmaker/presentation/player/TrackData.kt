package com.example.playlistmaker.presentation.player

data class TrackData(
    val trackName: String,
    val artistName: String,
    val duration: String,
    val artworkUrl: String?,
    val collectionName: String?,
    val year: String,
    val genre: String,
    val country: String
)