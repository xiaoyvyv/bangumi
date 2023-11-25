package com.xiaoyv.common.helper

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioManager
import android.media.MediaPlayer
import com.blankj.utilcode.util.Utils

class AudioPlayer private constructor(context: Context) {

    private val audioManager: AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var mediaPlayer: MediaPlayer? = null
    private val afChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                // 失去焦点，停止播放
                stop()
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                // 暂时失去焦点，暂停播放
                pause()
            }

            AudioManager.AUDIOFOCUS_GAIN -> {
                // 获得焦点，继续播放
                resume()
            }
        }
    }

    fun play(afd: AssetFileDescriptor) {
        mediaPlayer = mediaPlayer ?: MediaPlayer().also { mediaPlayer = it }

        try {
            // 请求音频焦点
            val result = audioManager.requestAudioFocus(
                afChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )

            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mediaPlayer?.reset()
                mediaPlayer?.setDataSource(afd)
                mediaPlayer?.prepare()
                mediaPlayer?.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun resume() {
        mediaPlayer?.start()
    }

    @Suppress("DEPRECATION")
    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        // 释放音频焦点
        audioManager.abandonAudioFocus(afChangeListener)
    }

    companion object {
        val instance by lazy {
            AudioPlayer(Utils.getApp())
        }
    }
}
