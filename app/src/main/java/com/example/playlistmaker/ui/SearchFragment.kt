package com.example.playlistmaker.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.presentation.search.SearchState
import com.example.playlistmaker.presentation.search.SearchViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModel()
    private val searchAdapter: TrackAdapter by lazy {
        TrackAdapter(emptyList(), onItemClickListener = { track ->
            openPlayer(
                track
            )
        })
    }

    private val historyAdapter: TrackAdapter by lazy {
        TrackAdapter(emptyList(), onItemClickListener = { track ->
            openPlayer(
                track
            )
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        setupObservers()
        setupClickListeners()
        restoreState(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        updateUIState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerViews() = with(binding) {
        rvListOfTracks.layoutManager = LinearLayoutManager(requireContext())
        rvListOfTracks.adapter = searchAdapter
        rvHistoryList.layoutManager = LinearLayoutManager(requireContext())
        rvHistoryList.adapter = historyAdapter
    }

    private fun openPlayer(track: Track) {
        viewModel.addToHistory(track)
        val action =
            SearchFragmentDirections.actionSearchFragmentToPlayerFragment(trackData = track)
        findNavController().navigate(action)
    }

    private fun hideKeyboard() {
        (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchState.collectLatest { state ->
                    updateUI(state)
                }
            }
        }
    }

    private fun setupClickListeners() = with(binding) {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.onQueryChanged(s.toString())
                clearIcon.isVisible = !s.isNullOrEmpty()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        })

        clearIcon.setOnClickListener {
            searchEditText.setText("")
            hideKeyboard()
            viewModel.loadHistory()
        }

        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        retrySearch.setOnClickListener {
            viewModel.retryLastSearch()
        }
    }

    private fun restoreState(savedInstanceState: Bundle?) {
        savedInstanceState?.getString("searchQuery")?.let {
            binding.searchEditText.setText(it)
        }
        viewModel.restoreState()
    }

    private fun updateUIState() {
        if (binding.searchEditText.text.isNullOrEmpty()) {
            viewModel.loadHistory()
        } else {
            viewModel.restoreSearchResults()
        }
    }

    private fun updateUI(state: SearchState) = with(binding) {
        progressBar.isVisible = false
        rvListOfTracks.isVisible = false
        nothingFoundPlaceholder.isVisible = false
        noConnectionPlaceholder.isVisible = false
        historyLayout.isVisible = false
        clearHistoryButton.isVisible = false

        when (state) {
            is SearchState.Loading -> {
                progressBar.isVisible = true
            }

            is SearchState.Success -> {
                if (state.tracks.isNotEmpty()) {
                    rvListOfTracks.isVisible = true
                    searchAdapter.updateData(state.tracks)
                } else nothingFoundPlaceholder.isVisible = true

            }

            is SearchState.Error -> {
                noConnectionPlaceholder.isVisible = true
            }

            is SearchState.History -> {
                if (state.tracks.isNotEmpty()) {
                    historyLayout.isVisible = true
                    clearHistoryButton.isVisible = true
                }
                historyAdapter.updateData(state.tracks)
            }

            is SearchState.Empty -> {
            }
        }
    }

}
