package com.example.playlistmaker.data.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val playlistId: Long? = null,
    val playlistName: String,
    val playlistDescription: String?,
    val imagePath: String?,
    val trackCount: Int = 0,
) : Parcelable
