package com.example.playlistmaker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = SwitchThumbActiveColor,
    onPrimary = White,
    secondary = UnfocusedBoxColor,
    background = CommonScreenBackgroundTintLight,
    onBackground = CommonTextColorLight,
    surface = CommonScreenBackgroundTintLight,
    onSurface = SwitchThumbInactiveColor
)

private val DarkColors = darkColorScheme(
    primary = SwitchThumbActiveColor,
    onPrimary = White,
    secondary = UnfocusedBoxColor,
    background = CommonScreenBackgroundTintDark,
    onBackground = CommonTextColorDark,
    surface = CommonScreenBackgroundTintDark,
    onSurface = SwitchThumbInactiveColor
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content
    )
}
