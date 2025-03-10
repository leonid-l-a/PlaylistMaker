package com.example.playlistmaker.presentation.main

sealed class NavigationEvent {
    object ToSearchActivity : NavigationEvent()
    object ToLibraryActivity : NavigationEvent()
    object ToSettingsActivity : NavigationEvent()
}