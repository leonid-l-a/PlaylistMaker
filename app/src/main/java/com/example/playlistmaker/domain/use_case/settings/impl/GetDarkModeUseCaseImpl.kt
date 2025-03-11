package com.example.playlistmaker.domain.use_case.settings.impl

import com.example.playlistmaker.domain.use_case.settings.inter.GetDarkModeUseCase
import com.example.playlistmaker.domain.repository.SettingsRepository

class GetDarkModeUseCaseImpl(
    private val settingsRepository: SettingsRepository
) : GetDarkModeUseCase {
    override fun execute(): Boolean {
        return settingsRepository.isDarkModeEnabled()
    }
}
