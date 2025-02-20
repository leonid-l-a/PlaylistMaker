package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.interactor.SettingsInteractor
import com.example.playlistmaker.domain.entitie.Settings
import com.example.playlistmaker.domain.repository.SettingsRepository

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository):
    SettingsInteractor {

    override fun getSettings(): Settings {
        return Settings(isDarkModeEnabled = settingsRepository.isDarkModeEnabled())
    }

    override fun updateSettings(enabled: Boolean) {
        settingsRepository.setDarkModeEnabled(enabled)
    }
}
