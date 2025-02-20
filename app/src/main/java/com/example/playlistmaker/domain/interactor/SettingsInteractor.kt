package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.entitie.Settings

interface SettingsInteractor {
    fun getSettings(): Settings

    fun updateSettings(enabled: Boolean)
}