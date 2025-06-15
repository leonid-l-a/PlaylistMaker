package com.example.playlistmaker.domain.use_case.settings.impl

import com.example.playlistmaker.domain.SystemThemeProvider
import com.example.playlistmaker.domain.use_case.inter.GetDarkModeUseCase
import com.example.playlistmaker.domain.repository.SettingsRepository

class GetDarkModeUseCaseImpl(
    private val settingsRepository: SettingsRepository,
    private val systemThemeProvider: SystemThemeProvider
) : GetDarkModeUseCase {
    override fun execute(): Boolean {
        return if (settingsRepository.hasDarkModeSetting()) {
            settingsRepository.isDarkModeEnabled()
        } else {
            systemThemeProvider.isSystemInDarkMode()
        }
    }
}

