package com.example.playlistmaker.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
) : SearchHistoryRepository {

    private val historyKey = "search_history"
    private val maxHistorySize = 10
    private val gson = Gson()

    override fun addTrackToHistory(track: Track) {
        val history = getHistory().toMutableList()
        history.removeAll { it.trackName == track.trackName && it.artistName == track.artistName }
        history.add(0, track)
        if (history.size > maxHistorySize) {
            history.removeAt(history.size - 1)
        }
        saveHistory(history)
    }

    override fun getHistory(): List<Track> {
        val historyJson = sharedPreferences.getString(historyKey, null)
        return if (historyJson != null) {
            val type = object : TypeToken<List<Track>>() {}.type
            gson.fromJson(historyJson, type)
        } else {
            emptyList()
        }
    }

    override fun clearHistory() {
        sharedPreferences.edit { remove(historyKey) }
    }

    private fun saveHistory(history: List<Track>) {
        val historyJson = gson.toJson(history)
        sharedPreferences.edit { putString(historyKey, historyJson) }
    }
}
