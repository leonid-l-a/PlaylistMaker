package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.entitie.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteInteractor {
    suspend fun addTrackToFavorites(track: Track)

    suspend fun deleteTrackFromFavorites(track: Track)

    fun getFavorites(): Flow<List<Track>>
}