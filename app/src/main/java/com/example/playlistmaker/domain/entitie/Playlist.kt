package com.example.playlistmaker.domain.entitie

data class Playlist(
    val id: Long,
    val name: String,
    val description: String? = "",
    val imagePath: String? = "",
    val tracks: List<Track>? = emptyList<Track>(),
    val trackCount: Int
)