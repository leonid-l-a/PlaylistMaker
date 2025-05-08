package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.NetworkResponse

interface NetworkClient {
    suspend fun doRequest(dto: Any): NetworkResponse
}