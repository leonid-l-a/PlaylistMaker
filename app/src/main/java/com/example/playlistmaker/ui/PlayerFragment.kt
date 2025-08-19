package com.example.playlistmaker.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
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
import com.example.playlistmaker.service.PlayerService
import com.example.playlistmaker.service.PlayerServiceInterface
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
        }
    }

    private val viewModel: PlayerViewModel by viewModel(parameters = { parametersOf(args.trackData) })

    private var playerService: PlayerServiceInterface? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as PlayerService.PlayerServiceBinder
            playerService = binder.getService()
            isBound = true
            viewModel.setService(playerService!!)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
        }
    }

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
        setupBottomSheet()
        setupClickListeners()
        observePlayerScreenState()
        hideBottomNavView()
        setupBackPressedHandler()

    }

    override fun onStart() {
        super.onStart()
        Intent(requireContext(), PlayerService::class.java).also { intent ->
            intent.putExtra("preview_url", args.trackData.previewUrl)
            intent.putExtra("track_name", args.trackData.trackName)
            intent.putExtra("artist_name", args.trackData.artistName)
            requireActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isBound) {
            playerService?.hideNotification()
        }
    }

    override fun onStop() {
        super.onStop()
        if (isBound && playerService?.isPlaying() == true) {
            playerService?.showNotification()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isBound) {
            requireActivity().unbindService(serviceConnection)
            isBound = false
        }
        viewModel.releasePlayer()
        bottomSheetBehavior.removeBottomSheetCallback(bottomSheetCallback)
    }

    private fun setupUi() {
        binding.searchScreenToolbar.setNavigationOnClickListener { handleBackPressed() }
        binding.ibIsFavorite.setOnClickListener { viewModel.toggleFavorite() }
        binding.rvPlaylists.adapter = playlistsAdapter
        binding.rvPlaylists.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bsPlaylists).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isHideable = true
            addBottomSheetCallback(bottomSheetCallback)
        }
    }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            binding.overlay.visibility = if (newState == BottomSheetBehavior.STATE_HIDDEN) View.GONE else View.VISIBLE
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    private fun setupClickListeners() {
        binding.ibPlay.playbackControlCallback = {
            viewModel.playbackControl()
        }

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

    private fun observePlayerScreenState() {
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playerScreenState.collect { state ->

                    state.trackData?.let { data ->
                        with(binding) {
                            tvTrackName.text = data.trackName
                            tvArtistName.text = data.artistName
                            trackDurability.text = data.duration

                            val albumVisibility = if (data.collectionName.isNullOrEmpty()) View.GONE else View.VISIBLE
                            trackAlbumNameText.visibility = albumVisibility
                            trackAlbumName.visibility = albumVisibility
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

                    state.addTrackResult?.let { result ->
                        val message = if (result.success) {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                            getString(R.string.added_to_playlist) + result.playlistName
                        } else {
                            getString(R.string.already_in_playlist) + result.playlistName
                        }
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        viewModel.resetAddTrackResult()
                    }

                    state.playlists?.let { playlists ->
                        playlistsAdapter.submitList(playlists)
                    }

                    binding.ibPlay.isPlaying = state.isPlaying

                    when (state.status) {
                        PlayerScreenState.Status.PREPARING -> {
                            binding.ibPlay.isEnabled = false
                            binding.timePlayed.text = "00:00"
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
                            binding.timePlayed.text = "00:00"
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

                    binding.ibIsFavorite.setImageResource(
                        if (state.isFavorite) R.drawable.ic_red_heart else R.drawable.ic_white_heart
                    )
                }
            }
        }
    }

    private fun hideBottomNavView() {
        requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.GONE
        requireActivity().findViewById<View>(R.id.divider).visibility = View.GONE
    }

    private fun setupBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackPressed()
                }
            }
        )
    }

    private fun handleBackPressed() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            requireActivity().findViewById<View>(R.id.bottomNavigationView).visibility = View.VISIBLE
            requireActivity().findViewById<View>(R.id.divider).visibility = View.VISIBLE
            findNavController().popBackStack()
        }
    }

    private fun Int.dpToPx(context: Context): Int =
        (this * context.resources.displayMetrics.density).toInt()
}
