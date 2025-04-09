package com.example.playlistmaker.di

import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository.SearchRepositoryImpl
import com.example.playlistmaker.domain.repository.SearchRepository
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single { RetrofitNetworkClient(get()) }
    single<SearchRepository> { SearchRepositoryImpl(get()) }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}