package com.example.playlistmaker.ui.library

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.databinding.ItemPlaylistLibraryScreenBinding

class PlaylistsAdapterForLibraryScreen(
    private val onPlaylistClicked: (PlaylistEntity)-> Unit
): RecyclerView.Adapter<PlaylistsAdapterForLibraryScreen.PlaylistViewHolder>() {

    private val items = mutableListOf<PlaylistEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPlaylistLibraryScreenBinding.inflate(inflater, parent, false)
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newList: List<PlaylistEntity>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    inner class PlaylistViewHolder(
        private val binding: ItemPlaylistLibraryScreenBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val playlist = items[adapterPosition]
                onPlaylistClicked(playlist)
            }
        }

        fun bind(playlist: PlaylistEntity) = with(binding) {
            tvPlaylistName.text = playlist.playlistName
            tvTrackCount.text = binding.root.resources.getQuantityString(
                R.plurals.tracks_count,
                playlist.trackCount,
                playlist.trackCount
            )

            Glide.with(binding.root)
                .load(playlist.imagePath)
                .placeholder(R.drawable.ph_no_image)
                .transform(RoundedCorners(8))
                .into(binding.ivPlaylistIcon)
        }
    }
}