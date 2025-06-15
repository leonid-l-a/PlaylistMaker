package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.data.db.PlaylistWithTracks

interface ShareRepository {
    suspend fun share(playlist: PlaylistWithTracks)
}