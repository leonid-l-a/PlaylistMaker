package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.interactor.PlayerInteractor
import com.example.playlistmaker.domain.repository.PlayerRepository
import com.example.playlistmaker.presentation.player.PlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {
    factory<PlayerInteractor> { PlayerInteractorImpl(get()) }
    single<PlayerRepository> { PlayerRepositoryImpl { get<MediaPlayer>() } }
    factory { MediaPlayer() }
    viewModel { (track: Track) -> PlayerViewModel(get(), get(), track) }
}