package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                networkModule,
                playerModule,
                settingsModule,
                searchModule,
                libraryModule,
                mainModule
            )
        }
    }
}