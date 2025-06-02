package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.di.favoriteAndPlaylistModule
import com.example.playlistmaker.di.libraryModule
import com.example.playlistmaker.di.mainModule
import com.example.playlistmaker.di.networkModule
import com.example.playlistmaker.di.playerModule
import com.example.playlistmaker.di.searchModule
import com.example.playlistmaker.di.settingsModule
import com.markodevcic.peko.PermissionRequester
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.logger.PrintLogger

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        PermissionRequester.initialize(applicationContext)

        startKoin {
            androidContext(this@App)
            logger(PrintLogger(Level.DEBUG))

            modules(
                networkModule,
                playerModule,
                settingsModule,
                searchModule,
                libraryModule,
                mainModule,
                favoriteAndPlaylistModule,
            )
        }
    }
}