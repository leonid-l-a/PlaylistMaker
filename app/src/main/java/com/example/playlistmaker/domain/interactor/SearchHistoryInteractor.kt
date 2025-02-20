package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.entitie.Track

interface SearchHistoryInteractor {
    fun addTrack(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()
}
