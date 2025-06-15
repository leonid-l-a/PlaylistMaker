package com.example.playlistmaker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.domain.entitie.Playlist
import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.presentation.playlist.PlaylistViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment(R.layout.fragment_playlist) {

    private val viewModel: PlaylistViewModel by viewModel()
    private val args: PlaylistFragmentArgs by navArgs()

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomSheetMenuBehavior: BottomSheetBehavior<ConstraintLayout>

    private val trackAdapter: TrackAdapter by lazy {
        TrackAdapter(
            emptyList(),
            onItemClickListener = ::openPlayer,
            onItemLongClickListener = ::openDialogWindow
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupObservers()
        setupBackPressHandler()
    }

    private fun setupUI() {
        setupRecyclerView()
        setupBottomSheets()
        setupClickListeners()
        hideBottomNavView()
    }

    private fun setupObservers() {
        viewModel.loadPlaylist(args.playlistData)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                state.playlist?.let { playlist ->
                    with(binding) {
                        tvPlaylistName.text = playlist.name
                        tvTrackDescription.text = playlist.description
                        tvPlaylistDuration.text = resources.getQuantityString(
                            R.plurals.playlist_duration,
                            state.totalDuration,
                            state.totalDuration
                        )
                        tvTrackCount.text = resources.getQuantityString(
                            R.plurals.tracks_count,
                            playlist.trackCount,
                            playlist.trackCount
                        )
                        Glide.with(root)
                            .load(playlist.imagePath)
                            .placeholder(R.drawable.ph_no_image)
                            .into(ivPlaylistImage)
                        trackAdapter.updateData(playlist.tracks)

                        Glide.with(root)
                            .load(playlist.imagePath)
                            .placeholder(R.drawable.ph_no_image)
                            .into(ivMenuImage)
                        tvMenuPlaylistName.text = playlist.name

                        tvMenuPlaylistCount.text = resources.getQuantityString(
                            R.plurals.tracks_count,
                            playlist.trackCount,
                            playlist.trackCount
                        )
                    }
                }

                updateMenuVisibility(state.isMenuVisible)
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvTracks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trackAdapter
        }
    }

    private fun setupBottomSheets() {
        binding.btShare.post {
            val rootView = binding.root
            val screenHeight = rootView.height
            val location = IntArray(2)
            binding.btShare.getLocationOnScreen(location)
            val btShareBottomY = location[1] + binding.btShare.height
            val peekHeight = screenHeight - btShareBottomY - 24

            BottomSheetBehavior.from(binding.bsTracks).peekHeight = peekHeight
        }

        bottomSheetMenuBehavior = BottomSheetBehavior.from(binding.bsMenu).apply {
            peekHeight = resources.getDimensionPixelSize(R.dimen.playlist_menu_peek_height)
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        viewModel.setMenuVisibility(false)
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }
            })
        }
    }

    private fun setupClickListeners() {
        binding.searchScreenToolbar.setNavigationOnClickListener { handleBackPressed() }
        binding.btMore.setOnClickListener { viewModel.setMenuVisibility(true) }
        binding.tvDeletePlaylist.setOnClickListener { showDeletePlaylistDialog() }
        binding.overlay.setOnClickListener { viewModel.setMenuVisibility(false) }
        binding.btShare.setOnClickListener { viewModel.share() }
        binding.tvShare.setOnClickListener { viewModel.share() }
        binding.tvEditPlaylist.setOnClickListener {
            val playlistEntity = viewModel.state.value.playlist!!.toEntity()
            val action = PlaylistFragmentDirections
                .actionPlaylistFragmentToPlaylistEditFragment(playlist = playlistEntity)
            findNavController().navigate(action)
        }
    }

    private fun updateMenuVisibility(isVisible: Boolean) {
        with(binding) {
            overlay.visibility = if (isVisible) View.VISIBLE else View.GONE
            if (isVisible) {
                bottomSheetMenuBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                bottomSheetMenuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
    }

    private fun showDeletePlaylistDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_playlist_dialog_title))
            .setMessage(getString(R.string.delete_playlist_dialog_message))
            .setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.deletePlaylist()
                    findNavController().popBackStack()
                }
            }
            .show()
    }

    private fun setupBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (viewModel.state.value.isMenuVisible) {
                        viewModel.setMenuVisibility(false)
                    } else {
                        handleBackPressed()
                    }
                }
            }
        )
    }

    private fun openPlayer(track: Track) {
        val action =
            PlaylistFragmentDirections.actionPlaylistFragmentToPlayerFragment(trackData = track)
        findNavController().navigate(action)
    }

    private fun openDialogWindow(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.remove_track_dialog_title))
            .setMessage(getString(R.string.remove_track_dialog_message))
            .setNegativeButton(getString(R.string.no)) { dialog, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.removeTrackFromPlaylistWithCleanup(track)
                    viewModel.loadPlaylist(args.playlistData)
                }
            }
            .show()
    }

    private fun hideBottomNavView() {
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.GONE
        requireActivity().findViewById<View>(R.id.divider).visibility = View.GONE
    }

    private fun handleBackPressed() {
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.VISIBLE
        requireActivity().findViewById<View>(R.id.divider).visibility = View.VISIBLE
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun Playlist.toEntity(): PlaylistEntity =
        PlaylistEntity(
            playlistId = this.id,
            playlistName = this.name,
            playlistDescription = this.description,
            imagePath = this.imagePath,
            trackCount = this.trackCount
        )
}