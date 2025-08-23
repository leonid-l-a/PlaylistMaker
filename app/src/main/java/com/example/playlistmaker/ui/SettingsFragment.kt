package com.example.playlistmaker.ui

import android.os.Bundle
import android.view.View
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.playlistmaker.ui.theme.AppTheme
import com.example.playlistmaker.presentation.settings.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: android.view.LayoutInflater, container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme {
                    SettingsScreen(
                        isDarkModeEnabled = viewModel.isDarkModeEnabled.value == true,
                        onToggleDarkMode = { viewModel.toggleDarkMode() },
                        onShareApp = { viewModel.shareApp() },
                        onSupport = { viewModel.sendSupportEmail() },
                        onUserAgreement = { viewModel.openUserAgreement() }
                    )
                }
            }
        }
    }
}
