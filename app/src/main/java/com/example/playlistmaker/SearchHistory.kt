package com.example.playlistmaker

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val context: Context) {

    private val sharedPreferences = context.getSharedPreferences("SearchHistory", Context.MODE_PRIVATE)
    private val historyKey = "search_history"
    private val maxHistorySize = 10

    fun addTrackToHistory(track: Track) {
        val history = getHistory().toMutableList()
        history.removeAll { it.trackName == track.trackName && it.artistName == track.artistName }
        history.add(0, track)
        if (history.size > maxHistorySize) {
            history.removeAt(history.size - 1)
        }
        saveHistory(history)
    }

    fun getHistory(): List<Track> {
        val historyJson = sharedPreferences.getString(historyKey, null)
        return if (historyJson != null) {
            val type = object : TypeToken<List<Track>>() {}.type
            Gson().fromJson(historyJson, type)
        } else {
            emptyList()
        }
    }

    private fun saveHistory(history: List<Track>) {
        val historyJson = Gson().toJson(history)
        sharedPreferences.edit().putString(historyKey, historyJson).apply()
    }

    fun clearHistory() {
        sharedPreferences.edit().remove(historyKey).apply()
    }
}