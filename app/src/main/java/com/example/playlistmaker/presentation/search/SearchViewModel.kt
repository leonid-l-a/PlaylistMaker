package com.example.playlistmaker.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.domain.interactor.SearchHistoryInteractor
import com.example.playlistmaker.domain.interactor.SearchTracksInteractor

class SearchViewModel(
    private val searchHistory: SearchHistoryInteractor,
    private val searchTracks: SearchTracksInteractor,
    private val savedState: SavedStateHandle
) : ViewModel() {

    private val _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> = _searchState

    private var lastQuery: String?
        get() = savedState["lastQuery"]
        set(value) { savedState["lastQuery"] = value }

    private var lastResults: List<Track>?
        get() = savedState["lastResults"]
        set(value) { savedState["lastResults"] = value }

    fun restoreState() {
        lastResults?.let { _searchState.value = SearchState.Success(it) }
    }

    fun loadHistory() {
        _searchState.value = SearchState.History(searchHistory.getHistory())
    }

    fun clearHistory() {
        searchHistory.clearHistory()
        loadHistory()
    }

    fun addToHistory(track: Track) {
        searchHistory.addTrack(track)
        if (lastQuery.isNullOrEmpty()) {
            loadHistory()
        }
    }

    fun searchTracks(query: String) {
        if (query == lastQuery) return

        lastQuery = query
        _searchState.value = SearchState.Loading

        searchTracks.execute(query) { result ->
            result.onSuccess { tracks ->
                lastResults = tracks
                _searchState.postValue(
                    if (tracks.isEmpty()) SearchState.Empty
                    else SearchState.Success(tracks)
                )
            }.onFailure {
                _searchState.postValue(SearchState.Error("No connection"))
            }
        }
    }

    fun restoreSearchResults() {
        lastResults?.let { _searchState.value = SearchState.Success(it) }
    }
}