package com.xiaoyv.bangumi.shared.ui.platform.video

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import org.openani.mediamp.MediampPlayer
import org.openani.mediamp.togglePlayWhenReady
import kotlin.math.roundToInt

/**
 * 视频底部控制栏：包含进度条、时间提示、播放/暂停、倍速菜单及全屏切换按钮。
 *
 * @param player 视频播放器实例。
 * @param playbackSpeed 当前播放倍速。
 * @param previewPositionMillis 手势拖动中的预览位置；`null` 时显示播放器当前位置。
 * @param isSpeedMenuVisible 倍速浮窗是否显示。
 * @param isFullscreen 当前是否为全屏状态。
 * @param onPlaybackSpeedChange 倍速变更回调。
 * @param onSpeedMenuVisibilityChange 倍速浮窗显示状态变更回调。
 * @param onToggleFullscreen 点击全屏/退出全屏按钮的回调。
 * @param onInteraction 任意交互触发时的通知回调。
 * @param modifier 修饰器。
 */
@Composable
fun VideoControllerBar(
    player: MediampPlayer,
    playbackSpeed: Float,
    previewPositionMillis: Long?,
    isSpeedMenuVisible: Boolean,
    isFullscreen: Boolean,
    onPlaybackSpeedChange: (Float) -> Unit,
    onSpeedMenuVisibilityChange: (Boolean) -> Unit,
    onToggleFullscreen: () -> Unit,
    onInteraction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val playerState by player.state.collectAsState()
    val positionMillis by player.currentPositionMillis.collectAsState()
    val properties by player.mediaProperties.collectAsState()
    var sliderPreviewPositionMillis by remember { mutableStateOf<Long?>(null) }
    val duration = properties?.durationMillis ?: 0L
    val displayPositionMillis = previewPositionMillis ?: sliderPreviewPositionMillis ?: positionMillis
    val progress = if (duration > 0L) displayPositionMillis.toFloat() / duration else 0f

    Column(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.72f)),
                ),
            )
            .pointerInput(onInteraction) {
                detectTapGestures(
                    onPress = {
                        onInteraction()
                        tryAwaitRelease()
                    },
                )
            }
            .padding(VideoMargin),
        verticalArrangement = Arrangement.spacedBy(VideoMarginHalf),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = displayPositionMillis.toVideoTime(duration >= OneHourMillis),
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.labelMedium,
            )
            Slider(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = VideoMarginHalf),
                value = progress.coerceIn(0f, 1f),
                onValueChange = {
                    if (duration > 0L) sliderPreviewPositionMillis = (duration * it).toLong()
                    onInteraction()
                },
                onValueChangeFinished = {
                    sliderPreviewPositionMillis?.let(player::seekTo)
                    sliderPreviewPositionMillis = null
                    onInteraction()
                },
                thumb = {},
                track = { sliderState ->
                    VideoProgressSliderTrack(
                        sliderState = sliderState,
                        progressColor = Color.White,
                        trackColor = Color.White.copy(alpha = 0.3f),
                    )
                },
            )
            Text(
                text = duration.toVideoTime(duration >= OneHourMillis),
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.labelMedium,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = {
                    player.togglePlayWhenReady()
                    onInteraction()
                },
            ) {
                Icon(
                    imageVector = if (playerState.playWhenReady) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box {
                    TextButton(
                        onClick = {
                            onSpeedMenuVisibilityChange(true)
                            onInteraction()
                        },
                    ) {
                        Text(playbackSpeed.toSpeedLabel(), color = Color.White)
                    }
                    DropdownMenu(
                        expanded = isSpeedMenuVisible,
                        onDismissRequest = { onSpeedMenuVisibilityChange(false) },
                        shape = CircleShape,
                        modifier = Modifier
                            .widthIn(min = 220.dp)
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                    ) {
                        Slider(
                            modifier = Modifier.heightIn(max = 32.dp),
                            value = playbackSpeed,
                            onValueChange = {
                                onPlaybackSpeedChange(it.roundToPlaybackSpeedStep())
                                onInteraction()
                            },
                            onValueChangeFinished = onInteraction,
                            valueRange = MinPlaybackSpeed..MaxPlaybackSpeed,
                            steps = ((MaxPlaybackSpeed - MinPlaybackSpeed) / PlaybackSpeedStep).roundToInt() - 1,
                        )
                    }
                }
                IconButton(
                    onClick = {
                        onToggleFullscreen()
                        onInteraction()
                    },
                ) {
                    Icon(
                        imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                        contentDescription = null,
                        tint = Color.White,
                    )
                }
            }
        }
    }
}

/**
 * 进度条轨道绘制容器。
 */
@Composable
internal fun VideoProgressSliderTrack(
    sliderState: SliderState,
    progressColor: Color,
    trackColor: Color,
) {
    val valueRange = sliderState.valueRange
    val progress = if (valueRange.endInclusive > valueRange.start) {
        (sliderState.value - valueRange.start) / (valueRange.endInclusive - valueRange.start)
    } else {
        0f
    }
    VideoRoundedProgressTrack(
        progress = progress,
        progressColor = progressColor,
        trackColor = trackColor,
    )
}

/**
 * 绘制圆角胶囊进度条，防止极小进度时圆角因宽度过窄而等比压缩退化为方框。
 */
@Composable
internal fun VideoRoundedProgressTrack(
    progress: Float,
    progressColor: Color,
    trackColor: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp),
    ) {
        val fraction = progress.coerceIn(0f, 1f)
        val cornerRadius = CornerRadius(size.height / 2f)

        drawRoundRect(
            color = trackColor,
            cornerRadius = cornerRadius,
        )
        if (fraction > 0f) {
            val progressWidth = if (size.width > size.height) {
                (size.width * fraction).coerceIn(size.height, size.width)
            } else {
                size.width * fraction
            }
            drawRoundRect(
                color = progressColor,
                size = Size(width = progressWidth, height = size.height),
                cornerRadius = cornerRadius,
            )
        }
    }
}
