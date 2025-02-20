package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.ItunesRequest
import com.example.playlistmaker.data.dto.NetworkResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {
    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(RetrofitApi::class.java)

    override fun doRequest(dto: Any): NetworkResponse {
        if (dto is ItunesRequest) {
            val resp = itunesService.searchSongs(dto.expression).execute()

            val body = resp.body() ?: NetworkResponse()

            return body.apply { resultCode = resp.code() }
        } else {
            return NetworkResponse().apply { resultCode = 400 }
        }
    }
}