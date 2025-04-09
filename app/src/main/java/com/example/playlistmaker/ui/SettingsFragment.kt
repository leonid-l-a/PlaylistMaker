package com.example.playlistmaker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.presentation.settings.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment: Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isDarkModeEnabled.observe(viewLifecycleOwner) { isDarkModeEnabled ->
            binding.switchDarkMode.isChecked = isDarkModeEnabled
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.shareButton.setOnClickListener { viewModel.shareApp() }
        binding.supportButton.setOnClickListener { viewModel.sendSupportEmail() }
        binding.userAgreementButton.setOnClickListener { viewModel.openUserAgreement() }
        binding.switchDarkMode.setOnClickListener { viewModel.toggleDarkMode() }
    }

}