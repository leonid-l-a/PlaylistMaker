package com.example.playlistmaker.presentation.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.domain.interactor.SearchHistoryInteractor
import com.example.playlistmaker.domain.interactor.SearchTracksInteractor
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchHistory: SearchHistoryInteractor,
    private val searchTracks: SearchTracksInteractor,
    private val savedState: SavedStateHandle,
) : ViewModel() {

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Empty)
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    private var lastQuery: String?
        get() = savedState["lastQuery"]
        set(value) { savedState["lastQuery"] = value }

    private var lastResults: List<Track>?
        get() = savedState["lastResults"]
        set(value) { savedState["lastResults"] = value }

    private val queryFlow = MutableStateFlow("")

    init {
        observeQuery()
    }

    fun onQueryChanged(query: String) {
        queryFlow.value = query
    }

    @OptIn(FlowPreview::class)
    private fun observeQuery() {
        viewModelScope.launch {
            queryFlow
                .debounce(2000L)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isEmpty()) {
                        loadHistory()
                    } else {
                        searchTracks(query)
                    }
                }
        }
    }

    fun searchTracks(query: String) {
        if (query == lastQuery) return

        lastQuery = query
        _searchState.value = SearchState.Loading

        viewModelScope.launch {
            searchTracks.execute(query).collect { result ->
                result.onSuccess { tracks ->
                    lastResults = tracks
                    _searchState.value = SearchState.Success(tracks)
                }.onFailure {
                    _searchState.value = SearchState.Error("No connection")
                }
            }
        }
    }

    fun retryLastSearch() {
        lastQuery?.let { searchTracks(it) }
    }

    fun restoreState() {
        lastResults?.let { _searchState.value = SearchState.Success(it) }
    }

    fun loadHistory() {
        val history = searchHistory.getHistory()
        _searchState.value = if (history.isEmpty()) SearchState.Empty else SearchState.History(history)
    }

    fun clearHistory() {
        searchHistory.clearHistory()
        loadHistory()
    }

    fun addToHistory(track: Track) {
        searchHistory.addTrack(track)
        if (lastQuery.isNullOrEmpty()) loadHistory()
    }

    fun restoreSearchResults() {
        lastResults?.let { _searchState.value = SearchState.Success(it) }
    }
}
