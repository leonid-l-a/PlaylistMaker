package com.example.playlistmaker.domain.impl


import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.domain.interactor.SearchHistoryInteractor
import com.example.playlistmaker.domain.repository.SearchHistoryRepository

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository) : SearchHistoryInteractor {
    override fun addTrack(track: Track) = repository.addTrackToHistory(track)
    override fun getHistory(): List<Track> = repository.getHistory()
    override fun clearHistory() = repository.clearHistory()
}
