package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.domain.interactor.SearchSongsInteractor
import com.example.playlistmaker.domain.repository.SearchRepository

class SearchSongsInteractorImpl(private val repository: SearchRepository) : SearchSongsInteractor {
    override fun execute(query: String, callback: (Result<List<Track>>) -> Unit) {
        repository.searchSongs(query, callback)
    }
}
