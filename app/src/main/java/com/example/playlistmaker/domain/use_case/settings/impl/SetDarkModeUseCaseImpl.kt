package com.example.playlistmaker.domain.use_case.settings.impl

import com.example.playlistmaker.domain.repository.SettingsRepository
import com.example.playlistmaker.domain.use_case.settings.inter.SetDarkModeUseCase

class SetDarkModeUseCaseImpl(
    private val settingsRepository: SettingsRepository
) : SetDarkModeUseCase {
    override fun execute(enabled: Boolean) {
        settingsRepository.setDarkModeEnabled(enabled)
    }
}