package com.example.playlistmaker.domain.use_case.settings.impl

import com.example.playlistmaker.domain.repository.SettingsIntentsRepository
import com.example.playlistmaker.domain.use_case.settings.inter.ShareAppUseCase

class ShareAppUseCaseImpl(private val repository: SettingsIntentsRepository) : ShareAppUseCase {
    override fun shareApp() {
        repository.shareApp()
    }
}