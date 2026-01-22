@file:Suppress("KotlinConstantConditions")

package com.live2d.sdk.cubism.view

import android.util.Log
import androidx.core.net.toUri
import com.live2d.sdk.cubism.core.ICubismLogger
import com.live2d.sdk.cubism.view.LCubismSdk.Companion.instance

object LAppPal {
    private val systemNanoTime: Long
        get() = System.nanoTime()

    private var s_currentFrame = 0.0
    private var _lastNanoTime = 0.0
    private var _deltaNanoTime = 0.0

    private const val TAG = "[APP]"

    // デルタタイムの更新
    @JvmStatic
    fun updateTime() {
        s_currentFrame = systemNanoTime.toDouble()
        _deltaNanoTime = s_currentFrame - _lastNanoTime
        _lastNanoTime = s_currentFrame
    }

    // ファイルをバイト列として読み込む
    @JvmStatic
    fun loadFileAsBytes(path: String): ByteArray {
        val asset = "file:///android_asset/"
        return if (path.contains(asset)) {
            instance.applicationContext.assets.open(path.substringAfter(asset))
        } else {
            instance.applicationContext.contentResolver.openInputStream(path.toUri())
        }.use {
            it?.readBytes() ?: byteArrayOf()
        }
    }

    // デルタタイム(前回フレームとの差分)を取得する
    // ナノ秒を秒に変換
    @JvmStatic
    val deltaTime: Float
        get() = (_deltaNanoTime / 1000000000.0f).toFloat()

    /**
     * Logging function
     *
     * @param message log message
     */
    @JvmStatic
    fun printLog(message: String) {
        Log.d(TAG, message)
    }

    /**
     * Logging Function class to be registered in the CubismFramework's logging function.
     */
    class PrintLogFunction : ICubismLogger {
        override fun print(message: String) {
            Log.d(TAG, message)
        }
    }
}
