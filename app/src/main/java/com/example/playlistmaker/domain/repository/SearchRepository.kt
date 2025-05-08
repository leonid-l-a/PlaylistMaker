package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.entitie.Track
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchTracks(query: String): Flow<Result<List<Track>>>
}

