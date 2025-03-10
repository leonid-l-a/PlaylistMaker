package com.example.playlistmaker.creator

import android.content.Context
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.example.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.data.repository.SearchRepositoryImpl
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.SearchTracksInteractorImpl
import com.example.playlistmaker.domain.repository.SettingsRepository
import com.example.playlistmaker.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.interactor.PlayerInteractor
import com.example.playlistmaker.domain.interactor.SearchHistoryInteractor
import com.example.playlistmaker.domain.interactor.SearchTracksInteractor
import com.example.playlistmaker.domain.interactor.SettingsInteractor
import com.example.playlistmaker.domain.use_case.impl.main.SetCurrentModeUseCase
import com.example.playlistmaker.domain.use_case.impl.main.SetCurrentModeUseCaseImpl
import com.example.playlistmaker.domain.use_case.impl.settings.OpenUserAgreementUseCase
import com.example.playlistmaker.domain.use_case.impl.settings.OpenUserAgreementUseCaseImpl
import com.example.playlistmaker.domain.use_case.impl.settings.SendSupportEmailUseCase
import com.example.playlistmaker.domain.use_case.impl.settings.SendSupportEmailUseCaseImpl
import com.example.playlistmaker.domain.use_case.impl.settings.ShareAppUseCase
import com.example.playlistmaker.domain.use_case.impl.settings.ShareAppUseCaseImpl
import com.example.playlistmaker.domain.use_case.impl.settings.ToggleDarkModeUseCase
import com.example.playlistmaker.domain.use_case.impl.settings.ToggleDarkModeUseCaseImpl
import com.example.playlistmaker.presentation.settings.SettingsViewModel
import com.example.playlistmaker.presentation.main.MainViewModel
import com.example.playlistmaker.presentation.search.SearchViewModel

object Creator {
    private lateinit var appContext: Context

    private fun provideSettingsRepository(): SettingsRepository {
        val sharedPreferences = appContext.getSharedPreferences("APP_PREFERENCES", MODE_PRIVATE)
        return SettingsRepositoryImpl(sharedPreferences)
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(provideSettingsRepository())
    }

    fun initContext(context: Context) {
        appContext = context.applicationContext
    }

    fun provideShareAppUseCase(): ShareAppUseCase {
        return ShareAppUseCaseImpl(appContext)
    }

    fun provideSendSupportEmailUseCase(): SendSupportEmailUseCase {
        return SendSupportEmailUseCaseImpl(appContext)
    }

    fun provideOpenUserAgreementUseCase(): OpenUserAgreementUseCase {
        return OpenUserAgreementUseCaseImpl(appContext)
    }

    fun provideToggleDarkModeUseCase(): ToggleDarkModeUseCase {
        return ToggleDarkModeUseCaseImpl(provideSettingsInteractor())
    }

    fun provideSetCurrentModeUseCase(): SetCurrentModeUseCase {
        return SetCurrentModeUseCaseImpl()
    }

    fun provideSearchSongsInteractor(): SearchTracksInteractor {
        val repository = SearchRepositoryImpl()
        return SearchTracksInteractorImpl(repository)
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        val sharedPreferences =
            context.getSharedPreferences("app_preferences", MODE_PRIVATE)
        val repository = SearchHistoryRepositoryImpl(sharedPreferences)
        return SearchHistoryInteractorImpl(repository)
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(PlayerRepositoryImpl())
    }

    fun provideSettingsViewModelFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return SettingsViewModel(
                        provideSettingsInteractor(),
                        provideShareAppUseCase(),
                        provideSendSupportEmailUseCase(),
                        provideOpenUserAgreementUseCase(),
                        provideToggleDarkModeUseCase()
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
            settingsInteractor = provideSettingsInteractor(),
            setCurrentModeUseCase = provideSetCurrentModeUseCase()
        )
    }
}