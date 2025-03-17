package com.example.playlistmaker.domain.repository

interface SettingsIntentsRepository {
    fun openUserAgreement()
    fun sendSupportEmail()
    fun shareApp()
}