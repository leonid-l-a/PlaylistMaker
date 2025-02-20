package com.example.playlistmaker.domain.use_case.settings

import com.example.playlistmaker.domain.interactor.SettingsInteractor

class ToggleDarkModeUseCase(private val settingsInteractor: SettingsInteractor) {

    fun toggleDarkMode(): Boolean {
        val currentSettings = settingsInteractor.getSettings()
        val newDarkModeEnabled = !currentSettings.isDarkModeEnabled
        settingsInteractor.updateSettings(newDarkModeEnabled)
        return newDarkModeEnabled
    }
}
