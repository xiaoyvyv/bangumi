package com.xiaoyv.bangumi.shared.ui.platform.video

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Brightness1
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.Brightness3
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness5
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * 手势交互反馈提示浮层（快进定位/长按倍速/亮度音量）。
 *
 * @param hint 当前触发的手势提示对象；为 `null` 时不渲染。
 * @param isTopHint 是否需要在顶部渲染（如亮度、音量）。
 * @param topPadding 顶部提示距离屏幕顶部的间距，动态匹配标题栏高度。
 */
@Composable
internal fun BoxScope.VideoGestureHintOverlay(
    hint: VideoGestureHint?,
    isTopHint: Boolean,
    topPadding: Dp,
) {
    if (hint != null) {
        if (isTopHint) {
            VideoLevelHint(hint, topPadding)
            return
        }
        if (hint is VideoGestureHint.Speed) {
            VideoSpeedHint(hint.value)
            return
        }
        val text = when (hint) {
            is VideoGestureHint.Seek -> {
                val showHours = hint.durationMillis >= OneHourMillis
                "${hint.positionMillis.toVideoTime(showHours)}/${hint.durationMillis.toVideoTime(showHours)}"
            }

            is VideoGestureHint.Speed,
            is VideoGestureHint.Brightness,
            is VideoGestureHint.Volume,
                -> return
        }
        Text(
            text = text,
            modifier = Modifier
                .align(Alignment.Center)
                .background(Color.Black, RoundedCornerShape(VideoMarginHalf))
                .padding(horizontal = VideoMargin, vertical = VideoMarginHalf),
            color = Color.White,
            fontFamily = FontFamily.Monospace,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

/**
 * 长按临时加速播放的居中提示（含流光三角形动画）。
 *
 * @param speed 当前临时加速倍率。
 */
@Composable
private fun BoxScope.VideoSpeedHint(speed: Float) {
    val transition = rememberInfiniteTransition(label = "video-speed-hint")
    val animationProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 420, easing = LinearEasing),
        ),
        label = "video-speed-triangles",
    )
    Row(
        modifier = Modifier
            .align(Alignment.Center)
            .background(Color.Black, RoundedCornerShape(VideoMarginHalf))
            .padding(horizontal = VideoMargin, vertical = VideoMarginHalf),
        horizontalArrangement = Arrangement.spacedBy(VideoMarginHalf),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = speed.toSpeedText(),
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
        )
        Canvas(
            modifier = Modifier
                .width(30.dp)
                .height(16.dp),
        ) {
            val triangleWidth = size.width / 4f
            val triangleHeight = size.height * 0.7f
            val top = (size.height - triangleHeight) / 2f

            repeat(3) { index ->
                val alpha = 1f - 2f * abs(
                    ((animationProgress - index * 0.25f + 0.5f) % 1f) - 0.5f,
                )
                val startX = index * triangleWidth
                drawPath(
                    path = Path().apply {
                        moveTo(startX, top)
                        lineTo(startX + triangleWidth, size.height / 2f)
                        lineTo(startX, top + triangleHeight)
                        close()
                    },
                    color = Color.White.copy(alpha = alpha),
                )
            }
        }
    }
}

/**
 * 屏幕亮度与音量调节的顶部胶囊提示条。
 *
 * @param hint 亮度或音量手势提示。
 * @param topPadding 距离屏幕顶部的间距。
 */
@Composable
private fun BoxScope.VideoLevelHint(
    hint: VideoGestureHint,
    topPadding: Dp,
) {
    val rawValue = when (hint) {
        is VideoGestureHint.Brightness -> hint.value
        is VideoGestureHint.Volume -> hint.value
        else -> return
    }
    val progress = when (hint) {
        is VideoGestureHint.Brightness -> ((rawValue - MinBrightness) / (MaxBrightness - MinBrightness))
        is VideoGestureHint.Volume -> rawValue
    }.coerceIn(0f, 1f)

    val icon = when (hint) {
        is VideoGestureHint.Brightness -> when ((progress * 6).roundToInt()) {
            0 -> Icons.Default.Brightness1
            1 -> Icons.Default.Brightness2
            2 -> Icons.Default.Brightness3
            3 -> Icons.Default.Brightness4
            4 -> Icons.Default.Brightness5
            5 -> Icons.Default.Brightness6
            else -> Icons.Default.Brightness7
        }

        is VideoGestureHint.Volume -> when {
            progress <= 0f -> Icons.AutoMirrored.Filled.VolumeOff
            progress < 1f / 3f -> Icons.AutoMirrored.Filled.VolumeMute
            progress < 2f / 3f -> Icons.AutoMirrored.Filled.VolumeDown
            else -> Icons.AutoMirrored.Filled.VolumeUp
        }
    }

    Row(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = topPadding)
            .width(180.dp)
            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(percent = 50))
            .padding(VideoMarginHalf),
        horizontalArrangement = Arrangement.spacedBy(VideoMarginHalf),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White)
        VideoRoundedProgressTrack(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            progress = progress,
            progressColor = Color.White,
            trackColor = Color.White.copy(alpha = 0.3f),
        )
    }
}
