package com.example.playlistmaker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val playlistId: Long? = null,
    val playlistName: String,
    val playlistDescription: String?,
    val imagePath: String?,
    val trackCount: Int = 0
)
