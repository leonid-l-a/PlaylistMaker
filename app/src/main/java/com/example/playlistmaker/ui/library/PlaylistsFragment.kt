package com.example.playlistmaker.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.presentation.library.PlaylistsState
import com.example.playlistmaker.presentation.library.PlaylistsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistsViewModel by viewModel()

    private lateinit var adapter: PlaylistsAdapterForLibraryScreen

    companion object {
        fun newInstance() = PlaylistsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackPressed()
                }


            }
        )
    }

    private fun setupRecyclerView() {
        adapter = PlaylistsAdapterForLibraryScreen { playlist ->
            openPlaylist(playlist)
        }

        binding.rvPlaylists.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@PlaylistsFragment.adapter
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                updateUI(state)
            }
        }
    }

    private fun updateUI(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Empty -> {
                binding.llNoPlaylistCreated.isVisible = true
                binding.rvPlaylists.isVisible = false
            }

            is PlaylistsState.Playlists -> {
                binding.llNoPlaylistCreated.isVisible = false
                binding.rvPlaylists.isVisible = true
                adapter.submitList(state.playlists)
            }
        }
    }

    private fun setupClickListeners() {
        binding.btNewPlaylist.setOnClickListener {
            val action = LibraryFragmentDirections.actionLibraryFragmentToPlaylistCreationFragment()
            findNavController().navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadPlaylists()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleBackPressed() {
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.VISIBLE
    }

    private fun openPlaylist(playlist: PlaylistEntity) {
        val action =
            LibraryFragmentDirections.actionLibraryFragmentToPlaylistFragment(playlist.playlistId!!)
        findNavController().navigate(action)
    }
}