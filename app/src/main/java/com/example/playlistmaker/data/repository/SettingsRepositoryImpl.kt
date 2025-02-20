package com.example.playlistmaker.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val sharedPreferences: SharedPreferences) : SettingsRepository {

    override fun isDarkModeEnabled(): Boolean {
        return sharedPreferences.getBoolean("IS_DARK_MODE", false)
    }

    override fun setDarkModeEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("IS_DARK_MODE", enabled).apply()
    }
}
