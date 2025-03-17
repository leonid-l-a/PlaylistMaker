package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.domain.interactor.SearchTracksInteractor
import com.example.playlistmaker.domain.repository.SearchRepository

class SearchTracksInteractorImpl(private val repository: SearchRepository) : SearchTracksInteractor {
    override fun execute(query: String, callback: (Result<List<Track>>) -> Unit) {
        repository.searchTracks(query, callback)
    }
}
