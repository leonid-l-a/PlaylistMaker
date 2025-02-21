package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.ItunesNetworkResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitApi {
    @GET("/search?entity=song")
    fun searchSongs(@Query("term") expression: String): Call<ItunesNetworkResponse>
}