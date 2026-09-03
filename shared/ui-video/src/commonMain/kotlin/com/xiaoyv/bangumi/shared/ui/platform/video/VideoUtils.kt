package com.xiaoyv.bangumi.shared.ui.platform.video

import androidx.compose.runtime.Composable
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.video_speed
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

/**
 * 将毫秒时间格式化为视频播放时间格式（如 `03:25` 或 `01:12:45`）。
 *
 * @param showHours 是否强制包含小时部分，默认当时间大于等于 1 小时时自动展示。
 */
internal fun Long.toVideoTime(showHours: Boolean = this >= OneHourMillis): String {
    val totalSeconds = (this / 1_000).coerceAtLeast(0)
    val seconds = totalSeconds % 60
    val minutes = totalSeconds / 60 % 60
    val hours = totalSeconds / 3_600
    return if (showHours) {
        "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    } else {
        "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    }
}

/**
 * 将浮点数倍速按预定义步长进行舍入对齐。
 */
internal fun Float.roundToPlaybackSpeedStep(): Float =
    (this / PlaybackSpeedStep).roundToInt() * PlaybackSpeedStep

/**
 * 获取倍速展示文案：1.0X 时展示多语言“倍速”，其余展示数值（如 `1.5X`）。
 */
@Composable
internal fun Float.toSpeedLabel(): String =
    if (this == 1f) stringResource(Res.string.video_speed) else toSpeedText()

/**
 * 将倍速数值格式化为文本（如 `1.0X`、`1.25X`、`2.0X`）。
 */
internal fun Float.toSpeedText(): String {
    val hundredths = (this * 100).roundToInt()
    val integer = hundredths / 100
    val fractional = (hundredths % 100).coerceAtLeast(0)
    val decimal = when {
        fractional == 0 -> "0"
        fractional % 10 == 0 -> (fractional / 10).toString()
        else -> fractional.toString().padStart(2, '0')
    }
    return "$integer.$decimal" + "X"
}
