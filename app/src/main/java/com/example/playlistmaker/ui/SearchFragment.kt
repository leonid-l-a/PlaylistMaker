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
    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by viewModel()
    private val searchAdapter: TrackAdapter by lazy { TrackAdapter(emptyList()) { track -> openPlayer(track) } }
    private val historyAdapter: TrackAdapter by lazy { TrackAdapter(emptyList()) { track -> openPlayer(track) } }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
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

    private fun setupRecyclerViews() = with(binding) {
        rvListOfTracks.layoutManager = LinearLayoutManager(requireContext())
        rvListOfTracks.adapter = searchAdapter
        rvHistoryList.layoutManager = LinearLayoutManager(requireContext())
        rvHistoryList.adapter = historyAdapter
    }

    private fun openPlayer(track: Track) {
        viewModel.addToHistory(track)
        val action = SearchFragmentDirections.actionSearchFragmentToPlayerFragment(trackData = track)
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
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
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
        progressBar.isVisible = state is SearchState.Loading
        rvListOfTracks.isVisible = state is SearchState.Success
        nothingFoundPlaceholder.isVisible = state is SearchState.Success && state.tracks.isEmpty()
        noConnectionPlaceholder.isVisible = state is SearchState.Error
        historyLayout.isVisible = state is SearchState.History && state.tracks.isNotEmpty()

        when (state) {
            is SearchState.Success -> searchAdapter.updateData(state.tracks)
            is SearchState.History -> historyAdapter.updateData(state.tracks)
            else -> Unit
        }

        clearHistoryButton.isVisible = state is SearchState.History && state.tracks.isNotEmpty()
    }
}
