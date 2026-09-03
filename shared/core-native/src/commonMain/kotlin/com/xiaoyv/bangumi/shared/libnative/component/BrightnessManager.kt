package com.xiaoyv.bangumi.shared.libnative.component

import androidx.compose.runtime.Composable

/**
 * 平台屏幕亮度管理器。
 */
interface BrightnessManager {
    /**
     * 是否依赖上层通过蒙层进行软件调光（如桌面端无系统背光调节接口）。
     */
    val isSoftwareBrightness: Boolean
        get() = false

    /**
     * 当前屏幕亮度，范围为 0 到 1。
     */
    fun getBrightness(): Float

    /**
     * 设置当前屏幕亮度。
     *
     * @param level 亮度值，范围为 0 到 1。
     */
    fun setBrightness(level: Float)
}

/**
 * 创建并记忆当前平台的屏幕亮度管理器。
 */
@Composable
expect fun rememberBrightnessManager(): BrightnessManager
