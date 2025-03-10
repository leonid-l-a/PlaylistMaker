package com.example.playlistmaker.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.interactor.SettingsInteractor
import com.example.playlistmaker.domain.use_case.impl.main.SetCurrentModeUseCase

class MainViewModel(
    settingsInteractor: SettingsInteractor,
    setCurrentModeUseCase: SetCurrentModeUseCase
) : ViewModel() {

    private val _navigationEvent = MutableLiveData<NavigationEvent>()
    val navigationEvent: LiveData<NavigationEvent> get() = _navigationEvent

    init {
        val isDarkMode = settingsInteractor.getSettings().isDarkModeEnabled
        setCurrentModeUseCase.setCurrentMode(isDarkMode)
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
