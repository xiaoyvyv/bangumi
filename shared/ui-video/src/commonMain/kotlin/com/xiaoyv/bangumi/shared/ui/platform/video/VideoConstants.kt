package com.xiaoyv.bangumi.shared.ui.platform.video

import androidx.compose.ui.unit.dp

/**
 * 控制栏在无交互后的自动隐藏延迟时间（毫秒）。
 */
internal const val ControllerAutoHideMillis = 5_000L

/**
 * 轻点防抖与双击判定的等待超时时间（毫秒）。
 */
internal const val DoubleTapTimeoutMillis = 100L

/**
 * 一小时的毫秒数，用于判断时间格式化是否展示小时部分。
 */
internal const val OneHourMillis = 60 * 60 * 1_000L

/**
 * 屏幕长按时触发的临时加速播放倍速。
 */
internal const val LongPressSpeed = 3f

/**
 * 播放器支持的最小播放倍速。
 */
internal const val MinPlaybackSpeed = 0.5f

/**
 * 播放器支持的最大播放倍速。
 */
internal const val MaxPlaybackSpeed = 3f

/**
 * 播放倍速选择步长。
 */
internal const val PlaybackSpeedStep = 0.25f

/**
 * 画面支持设置的最小亮度。
 */
internal const val MinBrightness = 0.01f

/**
 * 画面支持设置的最大亮度。
 */
internal const val MaxBrightness = 1f

/**
 * 点击跳过按钮每次快进/快退的时长（毫秒）。
 */
internal const val SkipDurationMillis = 10_000L

/**
 * 视频界面内边距。
 */
internal val VideoMargin = 16.dp

/**
 * 视频界面半边距。
 */
internal val VideoMarginHalf = 8.dp
