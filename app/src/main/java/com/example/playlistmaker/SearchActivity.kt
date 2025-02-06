package com.example.playlistmaker

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
import com.example.playlistmaker.databinding.ActivitySearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : BaseActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchHistory: SearchHistory
    private val itunesBaseUrl = "https://itunes.apple.com"
    private lateinit var itunesService: ItunesService
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchHistory = SearchHistory(this)

        val onTrackClickListener = { track: Track ->
            searchHistory.addTrackToHistory(track)
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
            searchHistory.clearHistory()
            updateHistoryView()
            binding.searchEditText.setText("")
            binding.searchEditText.requestFocus()
            showKeyboard()
        }

        binding.retrySearch.setOnClickListener {
            val query = binding.searchEditText.text.toString()
            if (query.isNotEmpty()) {
                searchSongs(query)
            }
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(itunesBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        itunesService = retrofit.create(ItunesService::class.java)

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

                    searchRunnable?.let { handler.removeCallbacks(it) }

                    if (s.isNullOrEmpty()) {
                        rvListOfTracks.visibility = View.GONE
                        updateHistoryView()
                    } else {
                        searchRunnable = Runnable {
                            val query = s.toString()
                            if (query.isNotEmpty()) {
                                showProgressBar()
                                searchSongs(query)
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
        }
    }

    private fun updateHistoryView() {
        val history = searchHistory.getHistory()
        binding.historyLayout.visibility = if (history.isNotEmpty()) View.VISIBLE else View.GONE
        (binding.rvHistoryList.adapter as TrackAdapter).updateData(history)
    }

    private fun searchSongs(query: String) {
        if (!isNetworkAvailable()) {
            binding.progressBar.visibility = View.GONE
            showNoConnectionPlaceholder()
            return
        }

        itunesService.searchSongs(query).enqueue(object : Callback<ItunesResponse> {
            override fun onResponse(
                call: Call<ItunesResponse>,
                response: Response<ItunesResponse>
            ) {
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val songs = response.body()?.results
                    if (!songs.isNullOrEmpty()) {
                        showRecyclerView(songs)
                    } else {
                        showNothingFoundPlaceholder()
                    }
                } else {
                    showNoConnectionPlaceholder()
                }
            }

            override fun onFailure(call: Call<ItunesResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                showNoConnectionPlaceholder()
            }
        })
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

        val tracks = songs.map {
            Track(
                trackName = it.trackName,
                artistName = it.artistName,
                trackTimeMillis = it.getFormattedTrackTime(),
                artworkUrl100 = it.artworkUrl100,
                collectionName = it.collectionName ?: "",
                releaseDate = it.releaseDate,
                primaryGenreName = it.primaryGenreName,
                country = it.country,
                previewUrl = it.previewUrl
            )
        }

        (rvListOfTracks.adapter as TrackAdapter).updateData(tracks)
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
