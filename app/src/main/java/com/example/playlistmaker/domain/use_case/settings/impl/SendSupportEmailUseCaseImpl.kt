package com.example.playlistmaker.domain.use_case.settings.impl

import com.example.playlistmaker.domain.repository.SettingsIntentsRepository
import com.example.playlistmaker.domain.use_case.inter.SendSupportEmailUseCase

class SendSupportEmailUseCaseImpl(private val repository: SettingsIntentsRepository) : SendSupportEmailUseCase {
    override fun sendSupportEmail() {
        repository.sendSupportEmail()
    }
}

