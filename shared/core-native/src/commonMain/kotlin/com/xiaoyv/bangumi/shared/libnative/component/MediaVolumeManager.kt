package com.xiaoyv.bangumi.shared.libnative.component

import androidx.compose.runtime.Composable

/**
 * 平台媒体音量管理器。
 */
interface MediaVolumeManager {
    /**
     * 获取媒体音量，范围为 0 到 1。
     */
    fun getVolume(): Float

    /**
     * 设置媒体音量。
     *
     * @param level 音量值，范围为 0 到 1。
     */
    fun setVolume(level: Float)
}

/**
 * 创建并记忆当前平台的媒体音量管理器。
 */
@Composable
expect fun rememberMediaVolumeManager(): MediaVolumeManager
