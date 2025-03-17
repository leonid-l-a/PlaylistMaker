package com.example.playlistmaker.presentation.main

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.use_case.settings.inter.GetDarkModeUseCase

class MainViewModel(
    getDarkModeUseCase: GetDarkModeUseCase
) : ViewModel() {

    private val _navigationEvent = MutableLiveData<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> get() = _navigationEvent

    init {
        val isDarkMode = getDarkModeUseCase.execute()
        val newMode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        if (AppCompatDelegate.getDefaultNightMode() != newMode) {
            AppCompatDelegate.setDefaultNightMode(newMode)
        }
    }

    fun onSearchButtonClicked() {
        _navigationEvent.value = NavigationEvent.ToSearchActivity
    }

    fun onLibraryButtonClicked() {
        _navigationEvent.value = NavigationEvent.ToLibraryActivity
    }

    fun onSettingsButtonClicked() {
        _navigationEvent.value = NavigationEvent.ToSettingsActivity
    }
}
