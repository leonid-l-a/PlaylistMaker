package com.example.playlistmaker.domain.use_case.settings.inter

interface SetDarkModeUseCase {
    fun execute(enabled: Boolean)
}