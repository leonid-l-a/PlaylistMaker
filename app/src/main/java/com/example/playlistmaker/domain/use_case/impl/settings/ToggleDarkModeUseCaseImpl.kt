package com.example.playlistmaker.domain.use_case.impl.settings

import com.example.playlistmaker.domain.interactor.SettingsInteractor

class ToggleDarkModeUseCaseImpl(private val settingsInteractor: SettingsInteractor) : ToggleDarkModeUseCase {
    override fun toggleDarkMode(): Boolean {
        val currentSettings = settingsInteractor.getSettings()
        val newDarkModeEnabled = !currentSettings.isDarkModeEnabled
        settingsInteractor.updateSettings(newDarkModeEnabled)
        return newDarkModeEnabled
    }
}
