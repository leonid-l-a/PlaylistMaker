package com.example.playlistmaker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.presentation.player.PlayerState
import com.example.playlistmaker.presentation.player.PlayerViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerFragment : Fragment() {
    private lateinit var binding: FragmentPlayerBinding

    private val args: PlayerFragmentArgs by navArgs()

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(args.trackData)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        setupObservers()
        hideBottomNavView()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackPressed()
                }
            })
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.releasePlayer()
    }

    private fun setupUi() {
        binding.searchScreenToolbar.setNavigationOnClickListener { handleBackPressed() }
        binding.ibPlay.setOnClickListener { viewModel.playbackControl() }

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED)
            {
                viewModel.trackData.collect { trackData ->
                    trackData?.let { data ->
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
                                .load(data.artworkUrl ?: R.drawable.ph_no_track_image)
                                .placeholder(R.drawable.ph_no_track_image)
                                .transform(RoundedCorners(8))
                                .into(trackImage)
                        }
                    }
                }

            }
        }

    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED)
            {
                viewModel.playerState.collect { state ->
                    when (state) {
                        is PlayerState.Preparing -> {
                            binding.ibPlay.isEnabled = false
                            binding.timePlayed.text = getString(R.string.time_start)
                        }

                        is PlayerState.Ready -> {
                            binding.ibPlay.isEnabled = true
                        }

                        is PlayerState.Playing -> {
                            binding.timePlayed.text = SimpleDateFormat(
                                "mm:ss",
                                Locale.getDefault()
                            ).format(state.remainingMillis)
                            binding.ibPlay.setImageResource(R.drawable.ic_pause)
                        }

                        is PlayerState.Paused -> {
                            binding.ibPlay.setImageResource(R.drawable.ic_play)
                        }

                        is PlayerState.Completed -> {
                            binding.timePlayed.text = getString(R.string.time_zero)
                            binding.ibPlay.setImageResource(R.drawable.ic_play)
                        }

                        is PlayerState.Error -> {
                            findNavController().popBackStack()
                            Toast.makeText(requireContext(), "Unexpected error", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
            }
        }

    }

    private fun hideBottomNavView() {
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.GONE
        requireActivity().findViewById<View>(R.id.divider).visibility =
            View.GONE
    }

    private fun handleBackPressed() {
        val bottomNav =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.visibility = View.VISIBLE
        val divider = requireActivity().findViewById<View>(R.id.divider)
        divider.visibility = View.VISIBLE
        findNavController().popBackStack()
    }
}