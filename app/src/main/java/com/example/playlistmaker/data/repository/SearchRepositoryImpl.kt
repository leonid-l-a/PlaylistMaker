package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.db.TrackDao
import com.example.playlistmaker.data.dto.ItunesNetworkResponse
import com.example.playlistmaker.data.dto.ItunesRequest
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(
    private val networkClient: RetrofitNetworkClient,
    private val favoriteDao: TrackDao
) : SearchRepository {

    override fun searchTracks(query: String): Flow<Result<List<Track>>> = flow {
        try {
            val requestDto = ItunesRequest(query)
            val response = networkClient.doRequest(requestDto)

            if (response.resultCode == 200) {
                val itunesResponse = response as? ItunesNetworkResponse
                val trackDto = itunesResponse?.results ?: emptyList()
                val domainTracks = trackDto.map { it.toDomain() }

                val favoriteIds = favoriteDao.getFavoriteIds()

                domainTracks.forEach { track ->
                    track.isFavorite = track.trackId in favoriteIds
                }
                emit(Result.success(domainTracks))
            } else {
                emit(Result.failure(Throwable("Error code: ${response.resultCode}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
