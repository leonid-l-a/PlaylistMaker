package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.SavedStateHandle
import com.example.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.repository.SearchRepositoryImpl
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.SearchTracksInteractorImpl
import com.example.playlistmaker.domain.interactor.SearchHistoryInteractor
import com.example.playlistmaker.domain.interactor.SearchTracksInteractor
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.domain.repository.SearchRepository
import com.example.playlistmaker.presentation.search.SearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val searchModule = module {
    single<SharedPreferences>(named("historyPrefs")) {
        androidContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    single<SearchHistoryRepository> { SearchHistoryRepositoryImpl(get(named("historyPrefs"))) }
    factory<SearchHistoryInteractor> { SearchHistoryInteractorImpl(get()) }
    factory<SearchTracksInteractor> { SearchTracksInteractorImpl(get()) }
    single<SearchRepository> { SearchRepositoryImpl(get(), get()) }

    viewModel { (state: SavedStateHandle) ->
        SearchViewModel(
            get(), get(), state
        )
    }
}