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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.databinding.FragmentPlaylistCreationBinding
import com.example.playlistmaker.presentation.library.PlaylistCreationViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.core.net.toUri

open class PlaylistCreationFragment : Fragment() {

    private var _binding: FragmentPlaylistCreationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistCreationViewModel by viewModel()

    private var isEditMode: Boolean = false

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { viewModel.onImageSelected(it) }
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) imagePickerLauncher.launch("image/*")
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlaylistCreationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNav()

        val playlistArg: PlaylistEntity? = arguments?.getParcelable("playlist")
        playlistArg?.let {
            isEditMode = true
            viewModel.setPlaylist(it)

            binding.playlistsName.setText(it.playlistName)
            binding.playlistDescription.setText(it.playlistDescription)
            it.imagePath?.let { path -> binding.playlistImage.setImageURI(path.toUri()) }

            setCreateButtonColor(it.playlistName)
        }

        configureMode()
        setupClickListeners()
        observeViewModel()
    }

    private fun configureMode() {
        binding.playlistCreationScreenToolbar.title =
            if (isEditMode) getString(R.string.edit_playlist) else getString(R.string.new_playlist)

        binding.buttonCreatePlaylist.text =
            if (isEditMode) getString(R.string.save) else getString(R.string.create)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() =
                    this@PlaylistCreationFragment.handleBackPressed()
            }
        )

        binding.playlistCreationScreenToolbar.setNavigationOnClickListener {
            handleBackPressed()
        }
    }

    private fun handleBackPressed() {
        if (isEditMode) {
            findNavController().popBackStack()
            return
        }

        val isEmptyName = binding.playlistsName.text.isNullOrBlank()
        val isEmptyDesc = binding.playlistDescription.text.isNullOrBlank()
        val isEmptyImage = viewModel.state.value.selectedImageUri == null

        if (isEmptyName && isEmptyDesc && isEmptyImage) {
            restoreBottomNav()
            findNavController().popBackStack()
        } else {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Вы действительно хотите выйти?")
                .setMessage("Все данные будут потеряны")
                .setNegativeButton("Отмена") { dialog, _ -> }
                .setPositiveButton("Завершить") { _, _ ->
                    restoreBottomNav()
                    findNavController().popBackStack()
                }
                .show()
        }
    }

    private fun restoreBottomNav() {
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            .visibility = View.VISIBLE
        requireActivity().findViewById<View>(R.id.divider)
            .visibility = View.VISIBLE
    }

    private fun setupClickListeners() {
        binding.playlistImage.setOnClickListener { checkAndRequestPermission() }

        binding.buttonCreatePlaylist.setOnClickListener {
            val name = binding.playlistsName.text.toString().trim()
            val desc =
                binding.playlistDescription.text.toString().trim().takeIf { it.isNotEmpty() }

            if (isEditMode) {
                viewModel.updatePlaylist(name = name, description = desc, context = requireContext())
            } else {
                if (name.isNotEmpty()) {
                    viewModel.createPlaylist(name, desc, requireContext())
                }
            }
            restoreBottomNav()
        }

        binding.playlistsName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setCreateButtonColor(s)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playlistCreationState.collect { result ->
                    result?.let {
                        if (it.isSuccess) {
                            findNavController().popBackStack()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Что-то пошло не так при ${if (isEditMode) "сохранении" else "создании"} плейлиста",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    state.selectedImageUri?.let { binding.playlistImage.setImageURI(it) }
                }
            }
        }
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

    private fun hideBottomNav() {
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            .visibility = View.GONE
        requireActivity().findViewById<View>(R.id.divider)
            .visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setCreateButtonColor(s: CharSequence?) {
        binding.buttonCreatePlaylist.isEnabled = !s.isNullOrBlank()
        val colorRes =
            if (!s.isNullOrBlank()) R.color.focused_box_color else R.color.unfocused_box_color
        binding.buttonCreatePlaylist.setBackgroundColor(
            ContextCompat.getColor(requireContext(), colorRes)
        )
    }
}
