package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.dto.ItunesNetworkResponse
import com.example.playlistmaker.data.dto.ItunesRequest
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.domain.repository.SearchRepository
import kotlin.concurrent.thread

class SearchRepositoryImpl(private val networkClient: RetrofitNetworkClient) : SearchRepository {
    override fun searchTracks(query: String, callback: (Result<List<Track>>) -> Unit) {
        thread {
            val requestDto = ItunesRequest(query)
            try {
                val response = networkClient.doRequest(requestDto)
                if (response.resultCode == 200) {
                    val itunesResponse = response as? ItunesNetworkResponse
                    val trackDto = itunesResponse?.results ?: emptyList()
                    val domainTracks = trackDto.map { it.toDomain() }
                    callback(Result.success(domainTracks))
                } else {
                    callback(Result.failure(Throwable("Error code: ${response.resultCode}")))
                }
            } catch (e: Exception) {
                callback(Result.failure(e))
            }
        }
    }
}
