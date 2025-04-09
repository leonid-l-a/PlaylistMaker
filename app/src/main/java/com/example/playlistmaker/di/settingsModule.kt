package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.data.repository.SettingsIntentsRepositoryImpl
import com.example.playlistmaker.data.SystemThemeProviderImpl
import com.example.playlistmaker.domain.repository.SettingsRepository
import com.example.playlistmaker.domain.repository.SettingsIntentsRepository
import com.example.playlistmaker.domain.SystemThemeProvider
import com.example.playlistmaker.domain.use_case.settings.impl.*
import com.example.playlistmaker.domain.use_case.settings.inter.*
import com.example.playlistmaker.presentation.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val settingsModule = module {
    single<SharedPreferences>(named("settingsPrefs")) {
        androidContext().getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE)
    }

    single<SettingsRepository> { SettingsRepositoryImpl(get(named("settingsPrefs"))) }
    single<SettingsIntentsRepository> { SettingsIntentsRepositoryImpl(androidContext()) }

    single<ShareAppUseCase> { ShareAppUseCaseImpl(get<SettingsIntentsRepository>()) }
    single<SendSupportEmailUseCase> { SendSupportEmailUseCaseImpl(get<SettingsIntentsRepository>()) }
    single<OpenUserAgreementUseCase> { OpenUserAgreementUseCaseImpl(get<SettingsIntentsRepository>()) }

    single<SystemThemeProvider> { SystemThemeProviderImpl(androidContext()) }
    single<GetDarkModeUseCase> {
        GetDarkModeUseCaseImpl(
            get<SettingsRepository>(), get<SystemThemeProvider>()
        )
    }
    single<SetDarkModeUseCase> { SetDarkModeUseCaseImpl(get<SettingsRepository>()) }

    viewModel { SettingsViewModel(get(), get(), get(), get(), get()) }
}