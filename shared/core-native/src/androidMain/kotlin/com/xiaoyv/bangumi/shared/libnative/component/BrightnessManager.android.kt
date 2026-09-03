package com.xiaoyv.bangumi.shared.libnative.component

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberBrightnessManager(): BrightnessManager {
    val context = LocalContext.current
    return remember(context) {
        AndroidBrightnessManager(context)
    }
}

private class AndroidBrightnessManager(
    private val context: Context,
) : BrightnessManager {
    private val activity = context.findActivity()

    override fun getBrightness(): Float {
        val windowBrightness = activity?.window?.attributes?.screenBrightness
        if (windowBrightness != null && windowBrightness >= 0f) return windowBrightness
        val systemBrightness = Settings.System.getInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            DefaultSystemBrightness,
        )
        return systemBrightness / MaxSystemBrightness.toFloat()
    }

    override fun setBrightness(level: Float) {
        activity?.window?.attributes = activity.window.attributes.apply {
            screenBrightness = level.coerceIn(MinBrightness, MaxBrightness)
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private const val DefaultSystemBrightness = 128
private const val MaxSystemBrightness = 255
private const val MinBrightness = 0.01f
private const val MaxBrightness = 1f
