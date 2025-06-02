package com.example.playlistmaker.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistCreationBinding
import com.example.playlistmaker.presentation.library.PlaylistCreationViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistCreationFragment : Fragment() {

    private var _binding: FragmentPlaylistCreationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistCreationViewModel by viewModel()

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                viewModel.onImageSelected(it)
            }
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                imagePickerLauncher.launch("image/*")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlaylistCreationBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun hideBottomNavView() {
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.GONE
        requireActivity().findViewById<View>(R.id.divider).visibility = View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavView()

        binding.playlistImage.setOnClickListener {
            checkAndRequestPermission()
        }

        binding.playlistCreationScreenToolbar.setNavigationOnClickListener {
            handleBackPressed()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackPressed()
                }
            }
        )

        lifecycleScope.launchWhenStarted {
            viewModel.playlistCreationState.collect { result ->
                result?.let {
                    if (it.isSuccess) {
                        parentFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Ошибка создания: ${it.exceptionOrNull()?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        binding.playlistsName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isNotEmpty = !s.isNullOrBlank()
                setCreateButtonState(isNotEmpty)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.buttonCreatePlaylist.setOnClickListener {
            val name = binding.playlistsName.text.toString().trim()
            val description =
                binding.playlistDescription.text.toString().trim().takeIf { it.isNotEmpty() }

            if (name.isNotEmpty()) {
                viewModel.createPlaylist(name, description, requireContext())
            }

            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
                View.VISIBLE
            requireActivity().findViewById<View>(R.id.divider).visibility =
                View.VISIBLE

        }

        lifecycleScope.launchWhenStarted {
            viewModel.selectedImageUri.collectLatest { uri ->
                if (uri != null) {
                    binding.playlistImage.setImageURI(uri)
                }
            }
        }


        setCreateButtonState(false)
    }

    private fun setCreateButtonState(enabled: Boolean) {
        binding.buttonCreatePlaylist.isEnabled = enabled
        val colorRes = if (enabled) R.color.focused_box_color else R.color.unfocused_box_color
        binding.buttonCreatePlaylist.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                colorRes
            )
        )
    }

    private fun checkAndRequestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                imagePickerLauncher.launch("image/*")
            }

            shouldShowRequestPermissionRationale(permission) -> {
                permissionLauncher.launch(permission)
            }

            else -> {
                permissionLauncher.launch(permission)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleBackPressed() {
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.VISIBLE
        requireActivity().findViewById<View>(R.id.divider).visibility = View.VISIBLE
        findNavController().popBackStack()
    }
}
