package com.example.playlistmaker.creator

import android.content.Context
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.example.playlistmaker.data.SystemThemeProviderImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository.*
import com.example.playlistmaker.domain.SystemThemeProvider
import com.example.playlistmaker.domain.impl.*
import com.example.playlistmaker.domain.interactor.*
import com.example.playlistmaker.domain.repository.*
import com.example.playlistmaker.domain.use_case.settings.impl.*
import com.example.playlistmaker.domain.use_case.settings.inter.*
import com.example.playlistmaker.presentation.settings.SettingsViewModel
import com.example.playlistmaker.presentation.main.MainViewModel
import com.example.playlistmaker.presentation.search.SearchViewModel

object Creator {
    private lateinit var appContext: Context

    private val networkClient by lazy { RetrofitNetworkClient() }
    private val searchRepository by lazy { SearchRepositoryImpl(networkClient) }

    fun initContext(context: Context) {
        appContext = context.applicationContext
    }

    private fun provideSettingsRepository(): SettingsRepository {
        val sharedPreferences = appContext.getSharedPreferences("APP_PREFERENCES", MODE_PRIVATE)
        return SettingsRepositoryImpl(sharedPreferences)
    }

    private fun provideSettingsIntentsRepository(): SettingsIntentsRepository {
        return SettingsIntentsRepositoryImpl(appContext)
    }

    fun provideShareAppUseCase(): ShareAppUseCase {
        return ShareAppUseCaseImpl(provideSettingsIntentsRepository())
    }

    fun provideSendSupportEmailUseCase(): SendSupportEmailUseCase {
        return SendSupportEmailUseCaseImpl(provideSettingsIntentsRepository())
    }

    fun provideOpenUserAgreementUseCase(): OpenUserAgreementUseCase {
        return OpenUserAgreementUseCaseImpl(provideSettingsIntentsRepository())
    }

    fun provideSearchSongsInteractor(): SearchTracksInteractor {
        return SearchTracksInteractorImpl(searchRepository)
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        val sharedPreferences = context.getSharedPreferences("app_preferences", MODE_PRIVATE)
        val repository = SearchHistoryRepositoryImpl(sharedPreferences)
        return SearchHistoryInteractorImpl(repository)
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(PlayerRepositoryImpl())
    }

    fun provideGetDarkModeUseCase(): GetDarkModeUseCase {
        val settingsRepository = provideSettingsRepository()
        val systemThemeProvider: SystemThemeProvider = SystemThemeProviderImpl(appContext)
        return GetDarkModeUseCaseImpl(settingsRepository, systemThemeProvider)
    }

    fun provideSetDarkModeUseCase(): SetDarkModeUseCase {
        return SetDarkModeUseCaseImpl(provideSettingsRepository())
    }

    fun provideSettingsViewModelFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return SettingsViewModel(
                        getDarkModeUseCase = provideGetDarkModeUseCase(),
                        setDarkModeUseCase = provideSetDarkModeUseCase(),
                        shareAppUseCase = provideShareAppUseCase(),
                        sendSupportEmailUseCase = provideSendSupportEmailUseCase(),
                        openUserAgreementUseCase = provideOpenUserAgreementUseCase()
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    fun provideSearchViewModelFactory(
        context: Context,
        owner: SavedStateRegistryOwner,
    ): ViewModelProvider.Factory {
        return object : AbstractSavedStateViewModelFactory(owner, null) {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle,
            ): T {
                return SearchViewModel(
                    provideSearchHistoryInteractor(context),
                    provideSearchSongsInteractor(),
                    handle
                ) as T
            }
        }
    }

    fun provideMainViewModel(): MainViewModel {
        return MainViewModel(
            getDarkModeUseCase = provideGetDarkModeUseCase()
        )
    }
}
