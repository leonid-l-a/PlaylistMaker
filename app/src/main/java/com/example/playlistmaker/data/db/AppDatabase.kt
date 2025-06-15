package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 7,
    entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        TrackPlaylistsEntity::class,
        PlaylistTrackCrossRef::class
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
}