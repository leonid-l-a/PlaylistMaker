package com.example.playlistmaker.ui

import android.content.Context
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.presentation.BaseActivity

class SearchActivity : BaseActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchHistoryInteractor: com.example.playlistmaker.domain.interactor.SearchHistoryInteractor
    private lateinit var searchSongsInteractor: com.example.playlistmaker.domain.interactor.SearchSongsInteractor
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchHistoryInteractor = Creator.createSearchHistoryInteractor(this)
        searchSongsInteractor = Creator.createSearchSongsInteractor()

        val onTrackClickListener = { track: Track ->
            searchHistoryInteractor.addTrack(track)
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("track", track)
            startActivity(intent)
            updateHistoryView()
        }

        binding.rvListOfTracks.layoutManager = LinearLayoutManager(this)
        binding.rvListOfTracks.adapter = TrackAdapter(emptyList(), onTrackClickListener)

        binding.rvHistoryList.layoutManager = LinearLayoutManager(this)
        binding.rvHistoryList.adapter = TrackAdapter(emptyList(), onTrackClickListener)

        binding.clearHistoryButton.setOnClickListener {
            searchHistoryInteractor.clearHistory()
            updateHistoryView()
            binding.searchEditText.setText("")
            binding.searchEditText.requestFocus()
            showKeyboard()
        }

        binding.retrySearch.setOnClickListener {
            val query = binding.searchEditText.text.toString()
            if (query.isNotEmpty()) {
                performSearch(query)
            }
        }

        binding.searchScreenToolbar.setOnClickListener {
            finish()
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                with(binding) {
                    clearIcon.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                    historyLayout.visibility = if (s.isNullOrEmpty()) View.VISIBLE else View.GONE
                    if (s.isNullOrEmpty()) {
                        clearIcon.visibility = View.GONE
                        historyLayout.visibility = View.VISIBLE
                        makePhsInvisible()
                    } else {
                        clearIcon.visibility = View.VISIBLE
                        historyLayout.visibility = View.GONE
                    }
                    searchRunnable?.let { handler.removeCallbacks(it) }

                    if (s.isNullOrEmpty()) {
                        rvListOfTracks.visibility = View.GONE
                        updateHistoryView()
                    } else {
                        searchRunnable = Runnable {
                            val query = s.toString()
                            if (query.isNotEmpty()) {
                                showProgressBar()
                                performSearch(query)
                            }
                        }
                        handler.postDelayed(searchRunnable!!, 2000)
                    }
                }
            }
        })

        updateHistoryView()

        binding.clearIcon.setOnClickListener {
            binding.searchEditText.setText("")
            hideKeyboard()
            binding.rvListOfTracks.visibility = View.GONE
            updateHistoryView()
            binding.searchEditText.clearFocus()
            makePhsInvisible()
        }
    }

    private fun updateHistoryView() {
        val history = searchHistoryInteractor.getHistory()
        binding.historyLayout.visibility = if (history.isNotEmpty()) View.VISIBLE else View.GONE
        (binding.rvHistoryList.adapter as TrackAdapter).updateData(history)
    }

    private fun makePhsInvisible() {
        binding.nothingFoundPlaceholder.visibility = View.GONE
        binding.noConnectionPlaceholder.visibility = View.GONE
    }

    private fun performSearch(query: String) {
        if (!isNetworkAvailable()) {
            binding.progressBar.visibility = View.GONE
            showNoConnectionPlaceholder()
            return
        }
        searchSongsInteractor.execute(query) { result ->
            runOnUiThread {
                binding.progressBar.visibility = View.GONE
                result.onSuccess { songs ->
                    if (songs.isNotEmpty()) {
                        showRecyclerView(songs)
                    } else {
                        showNothingFoundPlaceholder()
                    }
                }.onFailure {
                    showNoConnectionPlaceholder()
                }
            }
        }
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvListOfTracks.visibility = View.GONE
        binding.noConnectionPlaceholder.visibility = View.GONE
        binding.nothingFoundPlaceholder.visibility = View.GONE
    }

    private fun showRecyclerView(songs: List<Track>) = with(binding) {
        rvListOfTracks.visibility = View.VISIBLE
        noConnectionPlaceholder.visibility = View.GONE
        nothingFoundPlaceholder.visibility = View.GONE
        (rvListOfTracks.adapter as TrackAdapter).updateData(songs)
    }

    private fun showNoConnectionPlaceholder() {
        binding.rvListOfTracks.visibility = View.GONE
        binding.noConnectionPlaceholder.visibility = View.VISIBLE
        binding.nothingFoundPlaceholder.visibility = View.GONE
    }

    private fun showNothingFoundPlaceholder() {
        binding.rvListOfTracks.visibility = View.GONE
        binding.nothingFoundPlaceholder.visibility = View.VISIBLE
        binding.noConnectionPlaceholder.visibility = View.GONE
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    private fun showKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }


    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()
        binding.searchEditText.clearFocus()
    }
}
