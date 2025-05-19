package com.example.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Query("DELETE FROM track_table WHERE trackId = :trackId")
    suspend fun deleteTrackById(trackId: Long)

    @Query("SELECT * FROM track_table")
    fun getFavoriteTracks(): Flow<List<TrackEntity>>

    @Query("SELECT trackId FROM track_table")
    suspend fun getFavoriteIds(): List<Long>
}