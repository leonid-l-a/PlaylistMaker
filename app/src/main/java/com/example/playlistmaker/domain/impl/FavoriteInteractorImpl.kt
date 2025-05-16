package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.domain.interactor.FavoriteInteractor
import com.example.playlistmaker.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow

class FavoriteInteractorImpl(private val repository: FavoriteRepository) : FavoriteInteractor {

    override suspend fun addTrackToFavorites(track: Track) {
        repository.addTrackToFavorites(track)
    }

    override suspend fun deleteTrackFromFavorites(track: Track) {
        repository.deleteTrackFromFavorites(track)
    }

    override fun getFavorites(): Flow<List<Track>> {
        return repository.getFavorites()
    }
}