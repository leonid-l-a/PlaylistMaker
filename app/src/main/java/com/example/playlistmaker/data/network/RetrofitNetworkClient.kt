package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.ItunesRequest
import com.example.playlistmaker.data.dto.NetworkResponse
import retrofit2.HttpException
import retrofit2.Retrofit

class RetrofitNetworkClient(retrofit: Retrofit) : NetworkClient {

    private val itunesService = retrofit.create(RetrofitApi::class.java)

    override suspend fun doRequest(dto: Any): NetworkResponse {
        if (dto is ItunesRequest) {
            try {
                val response = itunesService.searchSongs(dto.expression)
                return response.apply { resultCode = 200 }
            } catch (e: HttpException) {
                return NetworkResponse().apply { resultCode = e.code() }
            } catch (_: Exception) {
                return  NetworkResponse().apply { resultCode = -1 }
            }
        } else {
            return NetworkResponse().apply {
                resultCode = 400
            }
        }
    }
}