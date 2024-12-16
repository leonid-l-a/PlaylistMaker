package com.example.playlistmaker

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
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
    private val itunesBaseUrl = "https://itunes.apple.com"
    private lateinit var itunesService: ItunesService

    companion object {
        var searchQuery: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.retrySearch.setOnClickListener { searchSongs(searchQuery) }

        val retrofit = Retrofit.Builder()
            .baseUrl(itunesBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        itunesService = retrofit.create(ItunesService::class.java)

        binding.searchScreenToolbar.setOnClickListener {
            finish()
        }

        binding.searchEditText.setText(searchQuery)

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = binding.searchEditText.text.toString()
                if (query.isNotEmpty()) {
                    searchQuery = query
                    searchSongs(query)
                } else {
                    showNoConnectionPlaceholder()
                }
                true
            } else {
                false
            }
        }

        binding.clearIcon.setOnClickListener {
            binding.searchEditText.setText("")
            hideKeyboard()
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                binding.clearIcon.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        searchQuery = ""
    }

    private fun searchSongs(query: String) {
        if (!isNetworkAvailable()) {
            showNoConnectionPlaceholder()
            return
        }

        itunesService.searchSongs(query).enqueue(object : Callback<ItunesResponse> {
            override fun onResponse(
                call: Call<ItunesResponse>,
                response: Response<ItunesResponse>
            ) {
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
                showNoConnectionPlaceholder()
            }
        })
    }

    private fun showRecyclerView(songs: List<Track>) {
        binding.rvListOfTracks.visibility = View.VISIBLE
        binding.noConnectionPlaceholder.visibility = View.GONE
        binding.nothingFoundPlaceholder.visibility = View.GONE

        val tracks = songs.map {
            Track(
                trackName = it.trackName,
                artistName = it.artistName,
                trackTimeMillis = it.getFormattedTrackTime(),
                artworkUrl100 = it.artworkUrl100
            )
        }

        binding.rvListOfTracks.layoutManager = LinearLayoutManager(this)
        binding.rvListOfTracks.adapter = TrackAdapter(tracks)
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

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}