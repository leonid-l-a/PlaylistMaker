package com.example.playlistmaker.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.databinding.ItemTrackBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TrackAdapter(
    private var tracks: List<Track>,
    private val onItemClickListener: (Track) -> Unit,
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    private var isClickEnabled = true

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])

        holder.itemView.setOnClickListener {
            if (!isClickEnabled) return@setOnClickListener

            isClickEnabled = false

            onItemClickListener(tracks[position])

            CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                isClickEnabled = true
            }
        }
    }

    override fun getItemCount(): Int = tracks.size

    class TrackViewHolder(private val binding: ItemTrackBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(track: Track) {
            binding.tvTrackName.text = track.trackName
            binding.tvArtistName.text = track.artistName
            binding.tvTrackTime.text = track.trackTimeMillis

            Glide.with(binding.root)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.ph_no_track_image)
                .transform(RoundedCorners(2))
                .into(binding.ivTrackIcon)
        }
    }
}
