package com.example.playlistmaker.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.presentation.library.FavoritesState
import com.example.playlistmaker.presentation.library.FavoritesViewModel
import com.example.playlistmaker.ui.TrackAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesViewModel by viewModel()

    companion object {
        fun newInstance() = FavoritesFragment()
    }

    private val favoritesAdapter: TrackAdapter by lazy {
        TrackAdapter(emptyList()) { track ->
            openPlayer(
                track
            )
        }
    }

    private fun openPlayer(track: Track) {
        val action =
            LibraryFragmentDirections.actionLibraryFragmentToPlayerFragment(trackData = track)
        findNavController().navigate(action)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvFavoritesList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavoritesList.adapter = favoritesAdapter
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoritesState.collect { state ->
                    when(state) {
                        is FavoritesState.Empty -> {
                            binding.phEmpty.visibility = View.VISIBLE
                            binding.rvFavoritesList.visibility = View.GONE
                        }

                        is FavoritesState.Favorites -> {
                            binding.phEmpty.visibility = View.GONE
                            binding.rvFavoritesList.visibility = View.VISIBLE
                            favoritesAdapter.updateData(state.tracks)

                        }
                    }
                }
            }
        }
    }
}
