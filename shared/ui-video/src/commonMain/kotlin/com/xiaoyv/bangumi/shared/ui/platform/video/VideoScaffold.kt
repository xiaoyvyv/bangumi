package com.xiaoyv.bangumi.shared.ui.platform.video

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.xiaoyv.bangumi.shared.libnative.component.rememberBrightnessManager
import com.xiaoyv.bangumi.shared.libnative.component.rememberMediaVolumeManager
import com.xiaoyv.bangumi.shared.libnative.component.rememberVideoScreenController
import com.xiaoyv.bangumi.shared.ui.platform.component.back.PlatformBackHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.openani.mediamp.MediaStatus
import org.openani.mediamp.MediampPlayer
import org.openani.mediamp.compose.MediampPlayerSurface
import org.openani.mediamp.features.PlaybackSpeed
import org.openani.mediamp.isLoadingOrBuffering
import kotlin.time.Duration.Companion.milliseconds

/**
 * 视频播放画面脚手架，整合播放表面、交互手势、控制栏、全屏控制及生命周期监听。
 *
 * @param player 视频播放器实例。
 * @param title 顶部标题文案。
 * @param onNavUp 返回上一页的回调（若当前为全屏状态，则优先退出全屏）。
 * @param modifier 外部修饰器。
 * @param onRetry 加载失败后的重新尝试回调。
 * @param state 播放器状态持有者，默认使用 [rememberVideoScaffoldState] 并在路由切换时自动持久化恢复。
 * @param isFullscreen 外部可控的全屏状态，为 `null` 时由 [state] 自动维护。
 * @param onPortrait 触发竖屏/退出全屏后的扩展回调。
 * @param onFullscreen 触发全屏后的扩展回调。
 */
@Composable
fun VideoScaffold(
    player: MediampPlayer,
    title: String,
    onNavUp: () -> Unit,
    modifier: Modifier = Modifier,
    onRetry: suspend () -> Unit = {},
    state: VideoScaffoldState = rememberVideoScaffoldState(),
    isFullscreen: Boolean? = null,
    onPortrait: () -> Unit = {},
    onFullscreen: () -> Unit = {},
) {
    val playbackSpeed = player.features.getOrFail(PlaybackSpeed)
    val currentSpeed by playbackSpeed.valueFlow.collectAsState(initial = playbackSpeed.value)
    val playerState by player.state.collectAsState()
    val brightnessManager = rememberBrightnessManager()
    val mediaVolumeManager = rememberMediaVolumeManager()
    val videoScreenController = rememberVideoScreenController()
    val coroutineScope = rememberCoroutineScope()

    val currentIsFullscreen = isFullscreen ?: state.isFullscreen
    val latestIsFullscreen by rememberUpdatedState(currentIsFullscreen)

    val statusBarTop = TopAppBarDefaults.windowInsets.asPaddingValues().calculateTopPadding()
    val topBarHeight = state.measuredTopBarHeight ?: (statusBarTop + 64.dp)
    val topHintPadding = topBarHeight + VideoMarginHalf

    // 页面销毁时若处于全屏状态，自动恢复系统窗口与屏幕方向
    DisposableEffect(videoScreenController) {
        onDispose {
            if (latestIsFullscreen) {
                videoScreenController.enterPortrait()
            }
        }
    }

    // 页面切入后台时暂停播放并记录播放意图，恢复前台时按需恢复
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        state.onPause(player)
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        state.onResume(player)
    }

    // 全屏时拦截系统返回（Android 返回手势/物理按键，桌面端 ESC），优先退出全屏
    PlatformBackHandler(enabled = currentIsFullscreen) {
        state.toggleFullscreen(videoScreenController, onFullscreen, onPortrait, false)
    }

    // 控制栏自动隐藏定时器
    LaunchedEffect(state.controlsVisible, state.isSpeedMenuVisible, state.interactionVersion) {
        if (state.controlsVisible && !state.isSpeedMenuVisible) {
            delay(ControllerAutoHideMillis.milliseconds)
            state.hideControls()
        }
    }

    Box(modifier = modifier) {
        MediampPlayerSurface(
            mediampPlayer = player,
            modifier = Modifier.fillMaxSize(),
        )

        // JVM 桌面端软件调光蒙层
        if (brightnessManager.isSoftwareBrightness && state.brightness < 1f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = (1f - state.brightness).coerceIn(0f, 0.95f))),
            )
        }

        VideoGestureOverlay(
            modifier = Modifier.fillMaxSize(),
            player = player,
            brightness = state.brightness,
            mediaVolumeManager = mediaVolumeManager,
            onBrightnessChange = { state.onBrightnessChange(brightnessManager, it) },
            onInteraction = { state.onInteraction(showControls = false) },
            onHideControls = { state.hideControls() },
            onTap = { state.onTap(coroutineScope) },
            onDoubleTap = { state.onDoubleTap(player) },
            onGestureHint = { hint ->
                if (hint != null) {
                    state.isTopGestureHint = hint is VideoGestureHint.Brightness || hint is VideoGestureHint.Volume
                }
                state.gestureHint = hint
            },
        )

        AnimatedVisibility(
            visible = state.controlsVisible,
            modifier = Modifier.align(Alignment.TopCenter),
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(),
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(),
            ) + fadeOut(),
        ) {
            VideoTopAppBar(
                title = title,
                onNavClick = {
                    if (currentIsFullscreen) {
                        state.toggleFullscreen(videoScreenController, onFullscreen, onPortrait, false)
                    } else {
                        onNavUp()
                    }
                },
                onHeightMeasured = { state.measuredTopBarHeight = it },
            )
        }

        AnimatedVisibility(
            visible = state.controlsVisible,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(),
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(),
            ) + fadeOut(),
        ) {
            VideoControllerBar(
                modifier = Modifier.fillMaxWidth(),
                player = player,
                playbackSpeed = currentSpeed,
                previewPositionMillis = (state.gestureHint as? VideoGestureHint.Seek)?.positionMillis,
                isSpeedMenuVisible = state.isSpeedMenuVisible,
                isFullscreen = currentIsFullscreen,
                onPlaybackSpeedChange = { playbackSpeed.set(it) },
                onSpeedMenuVisibilityChange = { state.isSpeedMenuVisible = it },
                onToggleFullscreen = {
                    state.toggleFullscreen(
                        videoScreenController = videoScreenController,
                        onFullscreen = onFullscreen,
                        onPortrait = onPortrait,
                        toFullscreen = !currentIsFullscreen,
                    )
                },
                onInteraction = { state.onInteraction() },
            )
        }

        VideoSkipControls(
            visible = state.controlsVisible &&
                    playerState.mediaStatus == MediaStatus.Ready &&
                    playerState.playWhenReady,
            onSkipBackward = {
                player.seekTo((player.currentPositionMillis.value - SkipDurationMillis).coerceAtLeast(0L))
                state.onInteraction()
            },
            onSkipForward = {
                val targetPosition = player.currentPositionMillis.value + SkipDurationMillis
                val duration = player.mediaProperties.value?.durationMillis ?: 0L
                player.seekTo(if (duration > 0L) targetPosition.coerceAtMost(duration) else targetPosition)
                state.onInteraction()
            },
        )

        VideoPausedOverlay(
            visible = playerState.mediaStatus == MediaStatus.Ready && !playerState.playWhenReady,
        )

        VideoLoadingOverlay(visible = playerState.isLoadingOrBuffering)

        VideoErrorOverlay(
            visible = playerState.mediaStatus is MediaStatus.Error,
            onRetry = { coroutineScope.launch { onRetry() } },
        )

        VideoGestureHintOverlay(
            hint = state.gestureHint,
            isTopHint = state.isTopGestureHint,
            topPadding = topHintPadding,
        )
    }
}
