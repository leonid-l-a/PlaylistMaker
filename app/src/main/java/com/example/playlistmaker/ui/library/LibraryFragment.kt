package com.example.playlistmaker.ui.library

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.presentation.library.FavoritesState
import com.example.playlistmaker.presentation.library.PlaylistsState
import com.example.playlistmaker.presentation.library.FavoritesViewModel
import com.example.playlistmaker.presentation.library.PlaylistsViewModel
import com.example.playlistmaker.ui.theme.AppTheme
import androidx.compose.runtime.getValue

import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryFragment : Fragment() {

    private val favoritesViewModel: FavoritesViewModel by viewModel()
    private val playlistsViewModel: PlaylistsViewModel by viewModel()

    override fun onCreateView(
        inflater: android.view.LayoutInflater, container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme {
                    val favoritesState by favoritesViewModel.favoritesState.collectAsState()
                    val playlistsState by playlistsViewModel.state.collectAsState()
                    val tracks = (favoritesState as? FavoritesState.Favorites)?.tracks.orEmpty()
                    val playlists = (playlistsState as? PlaylistsState.Playlists)?.playlists.orEmpty()
                    LibraryScreen(
                        favorites = tracks,
                        playlists = playlists,
                        onTrackClick = { track ->
                            val action = LibraryFragmentDirections
                                .actionLibraryFragmentToPlayerFragment(trackData = track)
                            findNavController().navigate(action)
                        },
                        onNewPlaylistClick = {
                            findNavController().navigate(
                                LibraryFragmentDirections.actionLibraryFragmentToPlaylistCreationFragment()
                            )
                        },
                        onPlaylistClick = { playlist ->
                            val action = LibraryFragmentDirections
                                .actionLibraryFragmentToPlaylistFragment(playlist.playlistId!!)
                            findNavController().navigate(action)
                        }
                    )
                }
            }
        }
    }
}
