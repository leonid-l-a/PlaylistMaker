package com.example.playlistmaker.di

import androidx.room.Room
import com.example.playlistmaker.data.TrackDbConvertor
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.repository.FavoriteRepositoryImpl
import com.example.playlistmaker.data.repository.ImageRepositoryImpl
import com.example.playlistmaker.data.repository.PlaylistRepositoryImpl
import com.example.playlistmaker.domain.impl.FavoriteInteractorImpl
import com.example.playlistmaker.domain.impl.PlaylistInteractorImpl
import com.example.playlistmaker.domain.interactor.FavoriteInteractor
import com.example.playlistmaker.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.domain.repository.FavoriteRepository
import com.example.playlistmaker.domain.repository.ImageRepository
import com.example.playlistmaker.domain.repository.PlaylistRepository
import com.example.playlistmaker.domain.use_case.settings.impl.CreatePlaylistUseCaseImpl
import com.example.playlistmaker.domain.use_case.settings.impl.SaveImageUseCaseImpl
import com.example.playlistmaker.domain.use_case.settings.inter.CreatePlaylistUseCase
import com.example.playlistmaker.domain.use_case.settings.inter.SaveImageUseCase
import com.example.playlistmaker.presentation.library.PlaylistCreationViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val favoriteAndPlaylistModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "database.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<AppDatabase>().trackDao() }
    single { get<AppDatabase>().playlistDao() }

    factory { TrackDbConvertor() }
    single<FavoriteRepository> { FavoriteRepositoryImpl(get(), get()) }
    single<FavoriteInteractor> { FavoriteInteractorImpl(get()) }

    single<PlaylistRepository> { PlaylistRepositoryImpl(get()) }
    single<PlaylistInteractor> { PlaylistInteractorImpl(get()) }

    single<CreatePlaylistUseCase> { CreatePlaylistUseCaseImpl(get()) }
    single<ImageRepository> { ImageRepositoryImpl() }
    single<SaveImageUseCase> { SaveImageUseCaseImpl(get()) }
    viewModel { PlaylistCreationViewModel(get(), get()) }
}