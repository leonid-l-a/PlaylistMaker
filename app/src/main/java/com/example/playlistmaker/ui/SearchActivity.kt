package com.example.playlistmaker.ui

import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.presentation.search.SearchState
import com.example.playlistmaker.presentation.search.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private val binding: ActivitySearchBinding by lazy { ActivitySearchBinding.inflate(layoutInflater) }
    private val viewModel: SearchViewModel by viewModel()
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private val searchAdapter: TrackAdapter by lazy { TrackAdapter(emptyList()) { track -> openPlayer(track) } }
    private val historyAdapter: TrackAdapter by lazy { TrackAdapter(emptyList()) { track -> openPlayer(track) } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupRecyclerViews()
        setupObservers()
        setupClickListeners()
        restoreState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("searchQuery", binding.searchEditText.text.toString())
    }

    override fun onResume() {
        super.onResume()
        updateUIState()
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    private fun setupRecyclerViews() {
        with(binding) {
            rvListOfTracks.apply {
                layoutManager = LinearLayoutManager(this@SearchActivity)
                adapter = searchAdapter
            }
            rvHistoryList.apply {
                layoutManager = LinearLayoutManager(this@SearchActivity)
                adapter = historyAdapter
            }
        }
    }

    private fun setupObservers() {
        viewModel.searchState.observe(this) { state ->
            updateUI(state)
        }
    }

    private fun setupClickListeners() {
        with(binding) {
            searchEditText.addTextChangedListener(createSearchTextWatcher())
            clearIcon.setOnClickListener { clearSearch() }
            clearHistoryButton.setOnClickListener { viewModel.clearHistory() }
            retrySearch.setOnClickListener { retrySearch() }
            searchScreenToolbar.setNavigationOnClickListener { finish() }
        }
    }

    private fun restoreState(savedInstanceState: Bundle?) {
        savedInstanceState?.getString("searchQuery")?.let {
            binding.searchEditText.setText(it)
        }
        viewModel.restoreState()
    }

    private fun updateUIState() {
        if (binding.searchEditText.text.isNullOrEmpty()) {
            viewModel.loadHistory()
        } else {
            viewModel.restoreSearchResults()
        }
    }

    private fun createSearchTextWatcher() = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) = handleTextChange(s)
        override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
    }

    private fun handleTextChange(s: Editable?) {
        with(binding) {
            clearIcon.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
        }
        searchRunnable?.let { handler.removeCallbacks(it) }
        searchRunnable = Runnable {
            val query = s?.toString().orEmpty()
            if (query.isEmpty()) {
                viewModel.loadHistory()
            } else {
                performSearch(query)
            }
        }
        handler.postDelayed(searchRunnable!!, SEARCH_DELAY_MS)
    }

    private fun performSearch(query: String) {
        if (query.isEmpty()) return
        if (isNetworkAvailable()) {
            viewModel.searchTracks(query)
        } else {
            showErrorState()
        }
    }

    private fun updateUI(state: SearchState) {
        with(binding) {
            progressBar.visibility = if (state is SearchState.Loading) View.VISIBLE else View.GONE
            rvListOfTracks.visibility = if (state is SearchState.Success) View.VISIBLE else View.GONE
            nothingFoundPlaceholder.visibility = if (state is SearchState.Empty) View.VISIBLE else View.GONE
            noConnectionPlaceholder.visibility = if (state is SearchState.Error) View.VISIBLE else View.GONE
            historyLayout.visibility = if (state is SearchState.History && state.tracks.isNotEmpty()) View.VISIBLE else View.GONE
        }
        when (state) {
            is SearchState.Success -> searchAdapter.updateData(state.tracks)
            is SearchState.History -> {
                historyAdapter.updateData(state.tracks)
                binding.clearHistoryButton.visibility = if (state.tracks.isEmpty()) View.GONE else View.VISIBLE
            }
            else -> Unit
        }
    }

    private fun openPlayer(track: Track) {
        viewModel.addToHistory(track)
        startActivity(Intent(this, PlayerActivity::class.java).apply {
            putExtra("track", track)
        })
    }

    private fun clearSearch() {
        binding.searchEditText.setText("")
        hideKeyboard()
        viewModel.loadHistory()
    }

    private fun retrySearch() {
        binding.searchEditText.text?.toString()?.takeUnless { it.isEmpty() }?.let(::performSearch)
    }

    private fun showErrorState() {
        binding.apply {
            progressBar.visibility = View.GONE
            rvListOfTracks.visibility = View.GONE
            noConnectionPlaceholder.visibility = View.VISIBLE
        }
    }

    private fun hideKeyboard() {
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            binding.searchEditText.windowToken, 0
        )
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    companion object {
        private const val SEARCH_DELAY_MS = 2000L
    }
}
