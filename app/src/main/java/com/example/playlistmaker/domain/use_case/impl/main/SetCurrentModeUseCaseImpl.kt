package com.example.playlistmaker.domain.use_case.impl.main

import androidx.appcompat.app.AppCompatDelegate

class SetCurrentModeUseCaseImpl : SetCurrentModeUseCase {
    override fun setCurrentMode(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
