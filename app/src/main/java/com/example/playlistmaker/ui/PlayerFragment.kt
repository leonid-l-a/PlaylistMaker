package com.example.playlistmaker.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.presentation.player.PlayerScreenState
import com.example.playlistmaker.presentation.player.PlayerViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {

    private lateinit var binding: FragmentPlayerBinding
    private val args: PlayerFragmentArgs by navArgs()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private val playlistsAdapter: PlaylistsAdapterForPlayerScreen by lazy {
        PlaylistsAdapterForPlayerScreen { playlist ->
            viewModel.addTrackToPlaylist(playlist)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private val viewModel: PlayerViewModel by viewModel(parameters = { parametersOf(args.trackData) })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        setupObservers()
        hideBottomNavView()
        setupBottomSheet()
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

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.releasePlayer()
        bottomSheetBehavior.removeBottomSheetCallback(bottomSheetCallback)
    }

    private fun setupUi() {
        binding.searchScreenToolbar.setNavigationOnClickListener { handleBackPressed() }
        binding.ibPlay.setOnClickListener { viewModel.playbackControl() }
        binding.ibIsFavorite.setOnClickListener { viewModel.toggleFavorite() }
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isHideable = true
            addBottomSheetCallback(bottomSheetCallback)
        }

        binding.rvPlaylists.apply {
            adapter = playlistsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            binding.overlay.visibility = when (newState) {
                BottomSheetBehavior.STATE_HIDDEN -> View.GONE
                else -> View.VISIBLE
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
        }
    }

    private fun setupClickListeners() {
        binding.ibAddToPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.btNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_playlistCreationFragment)
        }

        binding.overlay.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playerScreenState.collect { state ->
                    state.trackData?.let { data ->
                        with(binding) {
                            tvTrackName.text = data.trackName
                            tvArtistName.text = data.artistName
                            trackDurability.text = data.duration

                            trackAlbumNameText.visibility =
                                if (data.collectionName.isNullOrEmpty()) View.GONE else View.VISIBLE
                            trackAlbumName.visibility = trackAlbumNameText.visibility
                            trackAlbumName.text = data.collectionName

                            trackYear.text = data.year
                            trackGenre.text = data.genre
                            trackCountry.text = data.country

                            Glide.with(requireContext())
                                .load(data.artworkUrl ?: R.drawable.ph_no_image)
                                .placeholder(R.drawable.ph_no_image)
                                .transform(RoundedCorners(8.dpToPx(requireContext())))
                                .into(trackImage)
                        }
                    }

                    state.playlists?.let { playlists ->
                        println("Playlists data: $playlists")
                        playlistsAdapter.submitList(playlists)
                    }

                    when (state.status) {
                        PlayerScreenState.Status.PREPARING -> {
                            binding.ibPlay.isEnabled = false
                            binding.timePlayed.text = "00:30"
                        }

                        PlayerScreenState.Status.READY -> {
                            binding.ibPlay.isEnabled = true
                        }

                        PlayerScreenState.Status.PLAYING -> {
                            binding.timePlayed.text = state.formattedTime
                        }

                        PlayerScreenState.Status.PAUSED -> {
                        }

                        PlayerScreenState.Status.COMPLETED -> {
                            binding.timePlayed.text = state.formattedTime
                        }

                        PlayerScreenState.Status.ERROR -> {
                            Toast.makeText(
                                requireContext(),
                                state.errorMessage ?: "Unexpected error",
                                Toast.LENGTH_LONG
                            ).show()
                            findNavController().popBackStack()
                        }
                    }

                    binding.ibPlay.setImageResource(
                        if (state.isPlaying) R.drawable.ic_pause
                        else R.drawable.ic_play
                    )

                    binding.ibIsFavorite.setImageResource(
                        if (state.isFavorite) R.drawable.ic_red_heart
                        else R.drawable.ic_white_heart
                    )
                }
            }
        }
    }

    private fun hideBottomNavView() {
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.GONE
        requireActivity().findViewById<View>(R.id.divider).visibility = View.GONE
    }

    private fun handleBackPressed() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
                View.VISIBLE
            requireActivity().findViewById<View>(R.id.divider).visibility = View.VISIBLE
            findNavController().popBackStack()
        }
    }

    private fun Int.dpToPx(context: Context): Int =
        (this * context.resources.displayMetrics.density).toInt()
}
