package com.example.playlistmaker.presentation.player

sealed class PlayerState {
    object Preparing : PlayerState()
    object Ready : PlayerState()
    data class Playing(val elapsedMillis: Long, val remainingMillis: Long) : PlayerState()
    object Paused : PlayerState()
    object Completed : PlayerState()
    data class Error(val message: String) : PlayerState()
}
