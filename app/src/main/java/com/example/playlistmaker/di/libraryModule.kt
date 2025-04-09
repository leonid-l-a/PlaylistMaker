package com.example.playlistmaker.di

import com.example.playlistmaker.presentation.library.FavoritesViewModel
import com.example.playlistmaker.presentation.library.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val libraryModule = module {
    viewModel { FavoritesViewModel() }
    viewModel { PlaylistsViewModel() }
}