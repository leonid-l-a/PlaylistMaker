package com.example.playlistmaker.di

import com.example.playlistmaker.presentation.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {
    viewModel{MainViewModel(get())}
}