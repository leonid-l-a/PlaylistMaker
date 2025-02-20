package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.entitie.Track

interface SearchHistoryRepository {
    fun addTrackToHistory(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()
}
