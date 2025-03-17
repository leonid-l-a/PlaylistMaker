package com.example.playlistmaker.data

import android.content.Context
import android.content.res.Configuration
import com.example.playlistmaker.domain.SystemThemeProvider

class SystemThemeProviderImpl(private val context: Context) : SystemThemeProvider {
    override fun isSystemInDarkMode(): Boolean {
        val currentNightMode = context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }
}
