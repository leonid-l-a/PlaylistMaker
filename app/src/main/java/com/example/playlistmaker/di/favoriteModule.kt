package com.example.playlistmaker.di

import androidx.room.Room
import com.example.playlistmaker.data.TrackDbConvertor
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.repository.FavoriteRepositoryImpl
import com.example.playlistmaker.domain.impl.FavoriteInteractorImpl
import com.example.playlistmaker.domain.interactor.FavoriteInteractor
import com.example.playlistmaker.domain.repository.FavoriteRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val favoriteModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db").build()
    }

    single { get<AppDatabase>().trackDao() }

    factory { TrackDbConvertor() }
    single<FavoriteRepository> { FavoriteRepositoryImpl(get(), get()) }
    single<FavoriteInteractor> { FavoriteInteractorImpl(get()) }
}