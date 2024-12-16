package com.example.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import com.google.gson.annotations.SerializedName



interface ItunesService {
    @GET("/search?entity=song")
    fun searchSongs(@Query("term") term: String): Call<ItunesResponse>
}


data class ItunesResponse(
    @SerializedName("resultCount") val resultCount: Int,
    @SerializedName("results") val results: List<Track>
)

