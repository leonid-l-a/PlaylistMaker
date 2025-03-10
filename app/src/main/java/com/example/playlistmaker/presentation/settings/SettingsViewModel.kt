package com.example.playlistmaker.presentation.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.interactor.SettingsInteractor
import com.example.playlistmaker.domain.use_case.impl.settings.OpenUserAgreementUseCase
import com.example.playlistmaker.domain.use_case.impl.settings.SendSupportEmailUseCase
import com.example.playlistmaker.domain.use_case.impl.settings.ShareAppUseCase
import com.example.playlistmaker.domain.use_case.impl.settings.ToggleDarkModeUseCase

class SettingsViewModel(
    settingsInteractor: SettingsInteractor,
    private val shareAppUseCase: ShareAppUseCase,
    private val sendSupportEmailUseCase: SendSupportEmailUseCase,
    private val openUserAgreementUseCase: OpenUserAgreementUseCase,
    private val toggleDarkModeUseCase: ToggleDarkModeUseCase
) : ViewModel() {

    private val _isDarkModeEnabled = MutableLiveData<Boolean>()
    val isDarkModeEnabled: LiveData<Boolean> = _isDarkModeEnabled

    init {
        _isDarkModeEnabled.value = settingsInteractor.getSettings().isDarkModeEnabled
    }

    fun toggleDarkMode() {
        val newState = toggleDarkModeUseCase.toggleDarkMode()
        _isDarkModeEnabled.value = newState
    }

    fun shareApp() {
        shareAppUseCase.shareApp()
    }

    fun sendSupportEmail() {
        sendSupportEmailUseCase.sendSupportEmail()
    }

    fun openUserAgreement() {
        openUserAgreementUseCase.openUserAgreement()
    }
}
