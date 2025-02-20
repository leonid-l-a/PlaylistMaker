package com.example.playlistmaker.domain.repository

interface SettingsRepository {
    fun isDarkModeEnabled(): Boolean
    fun setDarkModeEnabled(enabled: Boolean)
}