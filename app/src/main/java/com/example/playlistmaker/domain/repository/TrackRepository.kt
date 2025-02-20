package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.entitie.Track

interface TrackRepository {
    fun searchTracks(expression: String): List<Track>
}