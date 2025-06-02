package com.example.playlistmaker.presentation.player

import com.example.playlistmaker.data.db.PlaylistEntity

data class PlayerScreenState(
    val status: Status,
    val elapsedMillis: Long = 0L,
    val remainingMillis: Long = 0L,
    val formattedTime: String = "00:00",
    val isPlaying: Boolean = false,
    val isFavorite: Boolean = false,
    val trackData: TrackData? = null,
    val errorMessage: String? = null,
    val playlists: List<PlaylistEntity>? = null
) {
    enum class Status {
        PREPARING,
        READY,
        PLAYING,
        PAUSED,
        COMPLETED,
        ERROR
    }
}
