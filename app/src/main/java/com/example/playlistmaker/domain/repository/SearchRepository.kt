package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.entitie.Track

interface SearchRepository {
    fun searchSongs(query: String, callback: (Result<List<Track>>) -> Unit)
}
