package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.entitie.Track
import kotlinx.coroutines.flow.Flow

interface SearchTracksInteractor {
    fun execute(query: String): Flow<Result<List<Track>>>
}
