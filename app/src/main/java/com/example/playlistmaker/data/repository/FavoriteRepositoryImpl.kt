package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.TrackDbConvertor
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.TrackEntity
import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteRepositoryImpl(
    private val convertor: TrackDbConvertor,
    private val database: AppDatabase
) : FavoriteRepository {

    override suspend fun addTrackToFavorites(track: Track) {
        val trackEntity = convertor.map(track)
        database.trackDao().insertTrack(trackEntity)
    }

    override suspend fun deleteTrackFromFavorites(track: Track) {
        database.trackDao().deleteTrackById(track.trackId)
    }

    override fun getFavorites(): Flow<List<Track>> {
        return database.trackDao()
            .getFavoriteTracks()
            .map { entities -> convertFromTrackEntity(entities) }
    }


    private fun convertFromTrackEntity(entities: List<TrackEntity>): List<Track> {
        return entities.map { entity -> convertor.map(entity) }
    }
}
