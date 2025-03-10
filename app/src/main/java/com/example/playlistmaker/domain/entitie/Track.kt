package com.example.playlistmaker.domain.entitie

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track (
    @SerializedName("trackName")
    val trackName: String,
    @SerializedName("artistName")
    val artistName: String,
    @SerializedName("trackTimeMillis")
    val trackTimeMillis: String,
    @SerializedName("artworkUrl100")
    val artworkUrl100: String?,
    @SerializedName("collectionName")
    val collectionName: String?,
    @SerializedName("releaseDate")
    val releaseDate: String,
    @SerializedName("primaryGenreName")
    val primaryGenreName: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("previewUrl")
    val previewUrl: String
) : Parcelable