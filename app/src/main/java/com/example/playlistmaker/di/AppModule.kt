package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.data.repository.SearchRepositoryImpl
import com.example.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.data.repository.SettingsIntentsRepositoryImpl
import com.example.playlistmaker.data.SystemThemeProviderImpl
import com.example.playlistmaker.domain.entitie.Track
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.impl.SearchTracksInteractorImpl
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.interactor.PlayerInteractor
import com.example.playlistmaker.domain.interactor.SearchTracksInteractor
import com.example.playlistmaker.domain.interactor.SearchHistoryInteractor
import com.example.playlistmaker.domain.repository.PlayerRepository
import com.example.playlistmaker.domain.repository.SearchRepository
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.domain.repository.SettingsRepository
import com.example.playlistmaker.domain.repository.SettingsIntentsRepository
import com.example.playlistmaker.domain.SystemThemeProvider
import com.example.playlistmaker.domain.use_case.settings.impl.GetDarkModeUseCaseImpl
import com.example.playlistmaker.domain.use_case.settings.impl.OpenUserAgreementUseCaseImpl
import com.example.playlistmaker.domain.use_case.settings.impl.SendSupportEmailUseCaseImpl
import com.example.playlistmaker.domain.use_case.settings.impl.SetDarkModeUseCaseImpl
import com.example.playlistmaker.domain.use_case.settings.impl.ShareAppUseCaseImpl
import com.example.playlistmaker.domain.use_case.settings.inter.GetDarkModeUseCase
import com.example.playlistmaker.domain.use_case.settings.inter.OpenUserAgreementUseCase
import com.example.playlistmaker.domain.use_case.settings.inter.SendSupportEmailUseCase
import com.example.playlistmaker.domain.use_case.settings.inter.SetDarkModeUseCase
import com.example.playlistmaker.domain.use_case.settings.inter.ShareAppUseCase
import com.example.playlistmaker.presentation.main.MainViewModel
import com.example.playlistmaker.presentation.player.PlayerViewModel
import com.example.playlistmaker.presentation.search.SearchViewModel
import com.example.playlistmaker.presentation.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    factory <PlayerInteractor> { PlayerInteractorImpl(get()) }
    single<PlayerRepository> { PlayerRepositoryImpl { get<MediaPlayer>() } }
    factory { MediaPlayer() }
    viewModel { (track: Track) -> PlayerViewModel(get(), track) }
    single { RetrofitNetworkClient(get()) }
    single<SearchRepository> { SearchRepositoryImpl(get()) }
    single<SharedPreferences>(named("settingsPrefs")) {
        androidContext().getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE)
    }
    single<SharedPreferences>(named("historyPrefs")) {
        androidContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
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
    single<SearchHistoryRepository> { SearchHistoryRepositoryImpl(get(named("historyPrefs"))) }
    factory<SearchHistoryInteractor> { SearchHistoryInteractorImpl(get()) }
    factory<SearchTracksInteractor> { SearchTracksInteractorImpl(get()) }
    viewModel { SettingsViewModel(get(), get(), get(), get(), get()) }
    viewModel { (state: androidx.lifecycle.SavedStateHandle) ->
        SearchViewModel(
            get(), get(), state
        )
    }
    viewModel { MainViewModel(get()) }

    single <Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
