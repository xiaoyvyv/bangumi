package com.xiaoyv.bangumi.shared.libnative.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberMediaVolumeManager(): MediaVolumeManager = remember {
    object : MediaVolumeManager {
        override fun getVolume(): Float = 1f

        override fun setVolume(level: Float) = Unit
    }
}
