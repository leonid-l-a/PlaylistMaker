package com.example.playlistmaker.data.dto

import com.example.playlistmaker.domain.entitie.Track
import com.google.gson.annotations.SerializedName

data class ItunesNetworkResponse(
    @SerializedName("resultCount") val resultCount: Int,
    @SerializedName("results") val results: List<Track>
) : NetworkResponse()
