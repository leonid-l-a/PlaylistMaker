package com.example.playlistmaker.ui

import android.os.Bundle
import android.view.View
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.presentation.search.SearchViewModel
import com.example.playlistmaker.ui.theme.AppTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()

    override fun onCreateView(
        inflater: android.view.LayoutInflater, container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme {
                    SearchScreen(
                        viewModel = viewModel,
                        onTrackClick = { track ->
                            viewModel.addToHistory(track)
                            val action = SearchFragmentDirections
                                .actionSearchFragmentToPlayerFragment(trackData = track)
                            findNavController().navigate(action)
                        }
                    )
                }
            }
        }
    }
}
