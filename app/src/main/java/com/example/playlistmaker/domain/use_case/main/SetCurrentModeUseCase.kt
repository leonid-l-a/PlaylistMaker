package com.example.playlistmaker.domain.use_case.main

import androidx.appcompat.app.AppCompatDelegate

class SetCurrentModeUseCase {
    fun setCurrentMode(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}