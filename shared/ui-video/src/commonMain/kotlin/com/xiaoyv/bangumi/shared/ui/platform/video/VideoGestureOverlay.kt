package com.xiaoyv.bangumi.shared.ui.platform.video

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.xiaoyv.bangumi.shared.libnative.component.MediaVolumeManager
import org.openani.mediamp.MediampPlayer
import org.openani.mediamp.features.AudioLevelController
import org.openani.mediamp.features.PlaybackSpeed
import kotlin.math.abs

/**
 * 视频手势响应层：负责监听并分发单击、双击、水平进度滑动、左右垂直亮度/音量滑动以及长按临时加速。
 *
 * @param player 视频播放器实例。
 * @param brightness 当前画面亮度。
 * @param mediaVolumeManager 系统音量管理器。
 * @param onBrightnessChange 亮度调节回调。
 * @param onInteraction 任意交互开始/结束时的通知回调。
 * @param onHideControls 隐藏控制栏的通知回调。
 * @param onTap 单击手势回调（用于切换控制栏显示/隐藏）。
 * @param onDoubleTap 双击手势回调（用于播放/暂停切换）。
 * @param onGestureHint 手势反馈数据变更回调。
 * @param modifier 修饰器。
 */
@Composable
internal fun VideoGestureOverlay(
    player: MediampPlayer,
    brightness: Float,
    mediaVolumeManager: MediaVolumeManager,
    onBrightnessChange: (Float) -> Unit,
    onInteraction: () -> Unit,
    onHideControls: () -> Unit,
    onTap: () -> Unit,
    onDoubleTap: () -> Unit,
    onGestureHint: (VideoGestureHint?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val audioLevelController = player.features[AudioLevelController]
    val playbackSpeed = player.features.getOrFail(PlaybackSpeed)
    val playerState by player.state.collectAsState()
    val currentBrightness by rememberUpdatedState(brightness)
    var isLongPressSpeedActive by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .pointerInput(player, playbackSpeed) {
                detectTapGestures(
                    onPress = {
                        if (tryAwaitRelease()) {
                            if (isLongPressSpeedActive) {
                                isLongPressSpeedActive = false
                                playbackSpeed.set(1f)
                                onGestureHint(null)
                            }
                        }
                    },
                    onLongPress = {
                        if (playerState.playWhenReady) {
                            isLongPressSpeedActive = true
                            onHideControls()
                            onInteraction()
                            playbackSpeed.set(LongPressSpeed)
                            onGestureHint(VideoGestureHint.Speed(LongPressSpeed))
                        }
                    },
                    onTap = { onTap() },
                    onDoubleTap = {
                        onDoubleTap()
                        onInteraction()
                    },
                )
            }
            .pointerInput(player, audioLevelController) {
                var startPositionMillis = 0L
                var startBrightness = brightness
                var startVolume = 0f
                var isHorizontal: Boolean? = null
                var startedOnLeft = false
                var totalDragX = 0f
                var totalDragY = 0f
                var previewPositionMillis = 0L

                detectDragGestures(
                    onDragStart = { offset ->
                        onInteraction()
                        startPositionMillis = player.currentPositionMillis.value
                        startBrightness = currentBrightness
                        startVolume = audioLevelController?.let {
                            it.volume.value / it.maxVolume
                        } ?: mediaVolumeManager.getVolume()
                        startedOnLeft = offset.x < size.width / 2f
                        isHorizontal = null
                        totalDragX = 0f
                        totalDragY = 0f
                        previewPositionMillis = startPositionMillis
                    },
                    onDrag = { change, dragAmount ->
                        totalDragX += dragAmount.x
                        totalDragY += dragAmount.y
                        if (isLongPressSpeedActive) {
                            onGestureHint(VideoGestureHint.Speed(LongPressSpeed))
                            change.consume()
                            return@detectDragGestures
                        }
                        val horizontal = isHorizontal ?: run {
                            if (abs(totalDragX) < 8f && abs(totalDragY) < 8f) return@detectDragGestures
                            (abs(totalDragX) >= abs(totalDragY)).also { isHorizontal = it }
                        }
                        if (horizontal) {
                            val duration = player.mediaProperties.value?.durationMillis
                                ?: return@detectDragGestures
                            val target = startPositionMillis + (totalDragX / size.width * duration).toLong()
                            previewPositionMillis = target.coerceIn(0L, duration)
                            onGestureHint(VideoGestureHint.Seek(previewPositionMillis, duration))
                        } else if (startedOnLeft) {
                            val targetBrightness = (startBrightness - totalDragY / size.height)
                                .coerceIn(MinBrightness, MaxBrightness)
                            onBrightnessChange(targetBrightness)
                            onGestureHint(VideoGestureHint.Brightness(targetBrightness))
                        } else {
                            val targetVolume = (startVolume - totalDragY / size.height).coerceIn(0f, 1f)
                            audioLevelController?.let {
                                it.setVolume(targetVolume * it.maxVolume)
                            } ?: mediaVolumeManager.setVolume(targetVolume)
                            onGestureHint(VideoGestureHint.Volume(targetVolume))
                        }
                        change.consume()
                    },
                    onDragEnd = {
                        val wasLongPressSpeedActive = isLongPressSpeedActive
                        if (wasLongPressSpeedActive) {
                            isLongPressSpeedActive = false
                            playbackSpeed.set(1f)
                        } else if (isHorizontal == true) {
                            player.seekTo(previewPositionMillis)
                        }
                        onGestureHint(null)
                        onInteraction()
                    },
                    onDragCancel = {
                        isLongPressSpeedActive = false
                        playbackSpeed.set(1f)
                        onGestureHint(null)
                        onInteraction()
                    },
                )
            },
    )
}
