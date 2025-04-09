package com.example.playlistmaker.presentation

import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.use_case.settings.inter.GetDarkModeUseCase

class MainViewModel(
    private val getDarkModeUseCase: GetDarkModeUseCase,
) : ViewModel() {

    fun setCurrentMode() {
        val isDarkMode = getDarkModeUseCase.execute()
        AppCompatDelegate.setDefaultNightMode(if (isDarkMode) MODE_NIGHT_YES else MODE_NIGHT_NO)
    }
}