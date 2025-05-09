package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.domain.interactor.SearchTracksInteractor
import com.example.playlistmaker.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow

class SearchTracksInteractorImpl(
    private val repository: SearchRepository,
) : SearchTracksInteractor {

    override fun execute(query: String): Flow<Result<List<Track>>> {
        return repository.searchTracks(query)
    }
}
