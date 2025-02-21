package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.entitie.Track

interface SearchSongsInteractor {
    fun execute(query: String, callback: (Result<List<Track>>) -> Unit)
}
