package com.example.playlistmaker.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.domain.repository.SettingsRepository
import androidx.core.content.edit

class SettingsRepositoryImpl(private val sharedPreferences: SharedPreferences) : SettingsRepository {

    companion object {
        private const val IS_DARK_MODE = "IS_DARK_MODE"
    }

    override fun isDarkModeEnabled(): Boolean {
        return sharedPreferences.getBoolean(IS_DARK_MODE, false)
    }

    override fun setDarkModeEnabled(enabled: Boolean) {
        sharedPreferences.edit { putBoolean(IS_DARK_MODE, enabled) }
    }

    override fun hasDarkModeSetting(): Boolean {
        return sharedPreferences.contains(IS_DARK_MODE)
    }
}

