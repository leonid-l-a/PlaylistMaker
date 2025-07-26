package com.example.playlistmaker.service


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.playlistmaker.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

interface PlayerServiceInterface {
    fun play()
    fun pause()
    fun isPlaying(): Boolean
    fun getCurrentTime(): Int
    fun showNotification()
    fun hideNotification()
    fun getPlayerStateFlow(): StateFlow<PlayerServiceState>
}

data class PlayerServiceState(
    val status: Status,
    val elapsedMillis: Long,
    val remainingMillis: Long,
) {
    enum class Status { PREPARING, PLAYING, PAUSED, COMPLETED }
}


class PlayerService : Service(), PlayerServiceInterface {

    private val binder = PlayerServiceBinder()
    private lateinit var mediaPlayer: MediaPlayer
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var updateJob: Job? = null

    private val _playerStateFlow = MutableStateFlow(
        PlayerServiceState(
            status = PlayerServiceState.Status.PREPARING,
            elapsedMillis = 0L,
            remainingMillis = MAX_TRACK_DURATION
        )
    )

    companion object {
        private const val CHANNEL_ID = "player_channel"
        private const val CHANNEL_NAME = "Playback"
        private const val NOTIFICATION_ID = 1
        private const val MAX_TRACK_DURATION = 30_000L
        private const val UPDATE_INTERVAL = 300L
    }

    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(
            CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW
        )
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent): IBinder {
        val trackName = intent.getStringExtra("track_name") ?: ""
        val artistName = intent.getStringExtra("artist_name") ?: ""
        val previewUrl = intent.getStringExtra("preview_url") ?: ""

        preparePlayer(previewUrl)

        currentTrackName = trackName
        currentArtistName = artistName

        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
        serviceScope.cancel()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
        serviceScope.cancel()
    }

    inner class PlayerServiceBinder : Binder() {
        fun getService(): PlayerServiceInterface = this@PlayerService
    }

    override fun play() {
        mediaPlayer.start()
        _playerStateFlow.value = _playerStateFlow.value.copy(
            status = PlayerServiceState.Status.PLAYING
        )
        updateJob?.cancel()
        updateJob = serviceScope.launch {
            while (isActive && mediaPlayer.isPlaying) {
                val elapsed = mediaPlayer.currentPosition.toLong()
                val remaining = (MAX_TRACK_DURATION - elapsed).coerceAtLeast(0L)
                _playerStateFlow.value = PlayerServiceState(
                    status = PlayerServiceState.Status.PLAYING,
                    elapsedMillis = elapsed,
                    remainingMillis = remaining
                )
                delay(UPDATE_INTERVAL)
            }
        }
    }

    override fun pause() {
        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            _playerStateFlow.value = _playerStateFlow.value.copy(
                status = PlayerServiceState.Status.PAUSED
            )
            updateJob?.cancel()
        }
    }

    override fun isPlaying(): Boolean {
        return ::mediaPlayer.isInitialized && mediaPlayer.isPlaying
    }

    override fun getCurrentTime(): Int {
        return if (::mediaPlayer.isInitialized) mediaPlayer.currentPosition else 0
    }

    override fun showNotification() {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Playlist Maker")
            .setContentText("$currentArtistName - $currentTrackName")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun hideNotification() {
        stopForeground(true)
    }

    override fun getPlayerStateFlow(): StateFlow<PlayerServiceState> {
        return _playerStateFlow
    }

    private fun preparePlayer(url: String) {
        mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(url)
        mediaPlayer.setOnPreparedListener {
            _playerStateFlow.value = PlayerServiceState(
                status = PlayerServiceState.Status.PAUSED,
                elapsedMillis = 0L,
                remainingMillis = MAX_TRACK_DURATION
            )
        }
        mediaPlayer.setOnCompletionListener {
            _playerStateFlow.value = PlayerServiceState(
                status = PlayerServiceState.Status.COMPLETED,
                elapsedMillis = MAX_TRACK_DURATION,
                remainingMillis = 0L
            )
            stopForeground(true)
        }
        mediaPlayer.prepareAsync()
    }
    private var currentTrackName: String = ""
    private var currentArtistName: String = ""
}
