package com.example.playlistmaker.creator

import android.content.Context
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.example.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.data.repository.SearchRepositoryImpl
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.SearchSongsInteractorImpl
import com.example.playlistmaker.domain.repository.SettingsRepository
import com.example.playlistmaker.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.interactor.PlayerInteractor
import com.example.playlistmaker.domain.interactor.SearchHistoryInteractor
import com.example.playlistmaker.domain.interactor.SearchSongsInteractor
import com.example.playlistmaker.domain.interactor.SettingsInteractor
import com.example.playlistmaker.domain.use_case.main.SetCurrentModeUseCase
import com.example.playlistmaker.domain.use_case.settings.OpenUserAgreementUseCase
import com.example.playlistmaker.domain.use_case.settings.SendSupportEmailUseCase
import com.example.playlistmaker.domain.use_case.settings.ShareAppUseCase
import com.example.playlistmaker.domain.use_case.settings.ToggleDarkModeUseCase

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
        return ShareAppUseCase(appContext)
    }

    fun provideSendSupportEmailUseCase(): SendSupportEmailUseCase {
        return SendSupportEmailUseCase(appContext)
    }

    fun provideOpenUserAgreementUseCase(): OpenUserAgreementUseCase {
        return OpenUserAgreementUseCase(appContext)
    }

    fun provideToggleDarkModeUseCase(): ToggleDarkModeUseCase {
        return ToggleDarkModeUseCase(provideSettingsInteractor())
    }

    fun provideSetCurrentModeUseCase(): SetCurrentModeUseCase {
        return SetCurrentModeUseCase()
    }

    fun createSearchSongsInteractor(): SearchSongsInteractor {
        val repository = SearchRepositoryImpl()
        return SearchSongsInteractorImpl(repository)
    }

    fun createSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        val sharedPreferences =
            context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val repository = SearchHistoryRepositoryImpl(sharedPreferences)
        return SearchHistoryInteractorImpl(repository)
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(PlayerRepositoryImpl())
    }

}