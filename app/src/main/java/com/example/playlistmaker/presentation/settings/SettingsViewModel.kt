package com.example.playlistmaker.presentation.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.use_case.settings.inter.GetDarkModeUseCase
import com.example.playlistmaker.domain.use_case.settings.inter.OpenUserAgreementUseCase
import com.example.playlistmaker.domain.use_case.settings.inter.SendSupportEmailUseCase
import com.example.playlistmaker.domain.use_case.settings.inter.SetDarkModeUseCase
import com.example.playlistmaker.domain.use_case.settings.inter.ShareAppUseCase

class SettingsViewModel(
    private val shareAppUseCase: ShareAppUseCase,
    private val sendSupportEmailUseCase: SendSupportEmailUseCase,
    private val openUserAgreementUseCase: OpenUserAgreementUseCase,
    private val getDarkModeUseCase: GetDarkModeUseCase,
    private val setDarkModeUseCase: SetDarkModeUseCase
) : ViewModel() {

    private val _isDarkModeEnabled = MutableLiveData<Boolean>()
    val isDarkModeEnabled: LiveData<Boolean> = _isDarkModeEnabled

    init {
        _isDarkModeEnabled.value = getDarkModeUseCase.execute()
    }

    fun toggleDarkMode() {
        val currentState = getDarkModeUseCase.execute()
        val newState = !currentState
        setDarkModeUseCase.execute(newState)
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