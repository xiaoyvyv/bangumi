package com.xiaoyv.bangumi.shared.ui.platform.video

import androidx.compose.runtime.Immutable

/**
 * 视频手势交互反馈类型定义。
 */
@Immutable
sealed interface VideoGestureHint {
    /**
     * 长按临时倍速提示。
     *
     * @property value 临时加速倍率。
     */
    @Immutable
    data class Speed(val value: Float) : VideoGestureHint

    /**
     * 水平滑动手势进度预览提示。
     *
     * @property positionMillis 预览的目标时间（毫秒）。
     * @property durationMillis 视频总时长（毫秒）。
     */
    @Immutable
    data class Seek(val positionMillis: Long, val durationMillis: Long) : VideoGestureHint

    /**
     * 左侧垂直滑动屏幕亮度提示。
     *
     * @property value 目标亮度值（0f..1f）。
     */
    @Immutable
    data class Brightness(val value: Float) : VideoGestureHint

    /**
     * 右侧垂直滑动媒体音量提示。
     *
     * @property value 目标音量值（0f..1f）。
     */
    @Immutable
    data class Volume(val value: Float) : VideoGestureHint
}
