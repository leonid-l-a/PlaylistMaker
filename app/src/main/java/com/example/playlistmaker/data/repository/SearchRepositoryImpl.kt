package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.dto.ItunesNetworkResponse
import com.example.playlistmaker.data.dto.ItunesRequest
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.domain.repository.SearchRepository

class SearchRepositoryImpl : SearchRepository {
    private val networkClient = RetrofitNetworkClient()

    override fun searchSongs(query: String, callback: (Result<List<Track>>) -> Unit) {
        Thread {
            val requestDto = ItunesRequest(query)
            try {
                val response = networkClient.doRequest(requestDto)
                if (response.resultCode == 200) {
                    val itunesResponse = response as? ItunesNetworkResponse
                    val tracks = itunesResponse?.results ?: emptyList()
                    if (tracks.isNotEmpty()) {
                        callback(Result.success(tracks))
                    } else {
                        callback(Result.failure(Throwable("No songs found")))
                    }
                } else {
                    callback(Result.failure(Throwable("Error code: ${response.resultCode}")))
                }
            } catch (e: Exception) {
                callback(Result.failure(e))
            }
        }.start()
    }

}
