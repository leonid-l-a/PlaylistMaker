package com.example.playlistmaker.data.db

import androidx.room.Embedded
import androidx.room.Ignore

data class PlaylistWithTracks(
    @Embedded val playlist: PlaylistEntity,

    @Ignore
    val tracks: List<TrackPlaylistsEntity> = emptyList()
)
