package com.example.playlistmaker.domain.use_case.settings.impl


import com.example.playlistmaker.domain.repository.SettingsIntentsRepository
import com.example.playlistmaker.domain.use_case.inter.OpenUserAgreementUseCase

class OpenUserAgreementUseCaseImpl(private val repository: SettingsIntentsRepository) : OpenUserAgreementUseCase {
    override fun openUserAgreement() {
        repository.openUserAgreement()
    }
}
