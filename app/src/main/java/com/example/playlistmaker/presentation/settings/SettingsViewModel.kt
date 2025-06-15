package com.example.playlistmaker.presentation.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.use_case.inter.GetDarkModeUseCase
import com.example.playlistmaker.domain.use_case.inter.OpenUserAgreementUseCase
import com.example.playlistmaker.domain.use_case.inter.SendSupportEmailUseCase
import com.example.playlistmaker.domain.use_case.inter.SetDarkModeUseCase
import com.example.playlistmaker.domain.use_case.inter.ShareAppUseCase

class SettingsViewModel(
    private val shareAppUseCase: ShareAppUseCase,
    private val sendSupportEmailUseCase: SendSupportEmailUseCase,
    private val openUserAgreementUseCase: OpenUserAgreementUseCase,
    private val getDarkModeUseCase: GetDarkModeUseCase,
    private val setDarkModeUseCase: SetDarkModeUseCase,
) : ViewModel() {

    private val _isDarkModeEnabled = MutableLiveData<Boolean>()
    val isDarkModeEnabled: LiveData<Boolean> = _isDarkModeEnabled

    init {
        val isDarkMode = getDarkModeUseCase.execute()
        _isDarkModeEnabled.value = isDarkMode
    }

    fun toggleDarkMode() {
        val currentState = getDarkModeUseCase.execute()
        val newState = !currentState
        setDarkModeUseCase.execute(newState)
        val newMode =
            if (newState) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(newMode)
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