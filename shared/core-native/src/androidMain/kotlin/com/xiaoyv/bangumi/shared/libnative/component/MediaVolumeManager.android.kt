package com.xiaoyv.bangumi.shared.libnative.component

import android.content.Context
import android.media.AudioManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberMediaVolumeManager(): MediaVolumeManager {
    val context = LocalContext.current
    return remember(context) {
        AndroidMediaVolumeManager(context)
    }
}

private class AndroidMediaVolumeManager(context: Context) : MediaVolumeManager {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    override fun getVolume(): Float {
        val maximum = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        if (maximum == 0) return 0f
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) / maximum.toFloat()
    }

    override fun setVolume(level: Float) {
        val maximum = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            (level.coerceIn(0f, 1f) * maximum).toInt(),
            0,
        )
    }
}
