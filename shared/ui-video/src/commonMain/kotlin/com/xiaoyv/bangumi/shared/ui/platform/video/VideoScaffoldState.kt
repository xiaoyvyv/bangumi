package com.xiaoyv.bangumi.shared.ui.platform.video

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import com.xiaoyv.bangumi.shared.libnative.component.BrightnessManager
import com.xiaoyv.bangumi.shared.libnative.component.VideoScreenController
import com.xiaoyv.bangumi.shared.libnative.component.rememberBrightnessManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.openani.mediamp.MediampPlayer
import org.openani.mediamp.togglePlayWhenReady
import kotlin.time.Duration.Companion.milliseconds

/**
 * 创建并记住 [VideoScaffoldState]，支持跨路由导航返回时通过 SaveableStateRegistry 自动恢复状态。
 *
 * @param initialIsFullscreen 初始全屏状态。
 * @param initialControlsVisible 初始控制栏是否显示。
 * @param brightnessManager 屏幕亮度管理器，用于获取初始默认亮度。
 */
@Composable
fun rememberVideoScaffoldState(
    initialIsFullscreen: Boolean = false,
    initialControlsVisible: Boolean = true,
    brightnessManager: BrightnessManager = rememberBrightnessManager(),
): VideoScaffoldState {
    val defaultBrightness = remember(brightnessManager) { brightnessManager.getBrightness() }

    return rememberSaveable(saver = VideoScaffoldState.Saver) {
        VideoScaffoldState(
            initialIsFullscreen = initialIsFullscreen,
            initialControlsVisible = initialControlsVisible,
            initialBrightness = defaultBrightness,
        )
    }
}

/**
 * 视频播放脚手架状态持有者。
 *
 * 封装控制栏显隐、全屏模式、倍速菜单、手势交互数据、亮度调节及生命周期暂停恢复状态。
 *
 * @param initialIsFullscreen 初始全屏状态。
 * @param initialControlsVisible 初始控制栏显示状态。
 * @param initialBrightness 初始画面亮度值。
 */
@Stable
class VideoScaffoldState(
    initialIsFullscreen: Boolean = false,
    initialControlsVisible: Boolean = true,
    initialBrightness: Float = 1f,
) {
    /**
     * 当前是否处于全屏状态。
     */
    var isFullscreen by mutableStateOf(initialIsFullscreen)

    /**
     * 控制栏（顶部栏、底部控制栏、跳过按钮等）是否处于可见状态。
     */
    var controlsVisible by mutableStateOf(initialControlsVisible)

    /**
     * 倍速选择下拉弹窗是否可见。
     */
    var isSpeedMenuVisible by mutableStateOf(false)

    /**
     * 当前画面亮度（0f..1f）。
     */
    var brightness by mutableFloatStateOf(initialBrightness)

    /**
     * 进入后台暂停前播放器是否正处于播放状态。
     */
    var wasPlayingBeforePause by mutableStateOf(false)

    /**
     * 当前触发的手势交互反馈数据，为 null 时表示无手势进行中。
     */
    var gestureHint by mutableStateOf<VideoGestureHint?>(null)

    /**
     * 当前手势提示是否需要展示在顶部（如亮度与音量提示）。
     */
    var isTopGestureHint by mutableStateOf(false)

    /**
     * 测量得到的顶部标题栏实际渲染高度。
     */
    var measuredTopBarHeight by mutableStateOf<Dp?>(null)

    /**
     * 交互触发版本计数器，用于重置自动隐藏控制栏的倒计时定时器。
     */
    var interactionVersion by mutableIntStateOf(0)
        private set

    /**
     * 轻点手势触发版本计数器，用于轻点防抖与双击冲突判定。
     */
    var tapVersion by mutableIntStateOf(0)
        private set

    /**
     * 触发全屏状态切换。
     *
     * @param videoScreenController 跨平台屏幕控制器。
     * @param onFullscreen 触发全屏后的扩展回调。
     * @param onPortrait 触发退出全屏后的扩展回调。
     * @param toFullscreen 目标全屏状态；默认取当前状态的反值。
     */
    fun toggleFullscreen(
        videoScreenController: VideoScreenController,
        onFullscreen: () -> Unit = {},
        onPortrait: () -> Unit = {},
        toFullscreen: Boolean = !isFullscreen,
    ) {
        if (toFullscreen) {
            videoScreenController.enterFullscreen()
            onFullscreen()
        } else {
            videoScreenController.enterPortrait()
            onPortrait()
        }
        isFullscreen = toFullscreen
    }

    /**
     * 触发任意交互通知，通知脚手架重置自动隐藏定时器。
     *
     * @param showControls 是否同时展开控制栏。
     */
    fun onInteraction(showControls: Boolean = true) {
        if (showControls) {
            controlsVisible = true
        }
        interactionVersion++
    }

    /**
     * 显示控制栏。
     */
    fun showControls() {
        controlsVisible = true
        interactionVersion++
    }

    /**
     * 隐藏控制栏。
     */
    fun hideControls() {
        controlsVisible = false
        interactionVersion++
    }

    /**
     * 切换控制栏显隐。
     */
    fun toggleControls() {
        controlsVisible = !controlsVisible
        interactionVersion++
    }

    /**
     * 更新画面亮度，并同步至系统或平台亮度管理器。
     */
    fun onBrightnessChange(brightnessManager: BrightnessManager, newBrightness: Float) {
        val clamped = newBrightness.coerceIn(MinBrightness, MaxBrightness)
        brightness = clamped
        brightnessManager.setBrightness(clamped)
    }

    /**
     * 处理轻点视频画面手势（带防抖）。
     */
    fun onTap(coroutineScope: CoroutineScope) {
        val version = ++tapVersion
        coroutineScope.launch {
            delay(DoubleTapTimeoutMillis.milliseconds)
            if (tapVersion == version) {
                controlsVisible = !controlsVisible
                interactionVersion++
            }
        }
    }

    /**
     * 处理双击视频画面手势（切换播放/暂停）。
     */
    fun onDoubleTap(player: MediampPlayer) {
        tapVersion++
        player.togglePlayWhenReady()
        onInteraction(showControls = false)
    }

    /**
     * 页面生命周期切入后台暂停时调用，记录当前播放状态并主动暂停。
     */
    fun onPause(player: MediampPlayer) {
        wasPlayingBeforePause = player.state.value.playWhenReady
        if (wasPlayingBeforePause) {
            player.pause()
        }
    }

    /**
     * 页面生命周期恢复可见时调用，若切后台前处于播放中则自动恢复。
     */
    fun onResume(player: MediampPlayer) {
        if (wasPlayingBeforePause) {
            player.play()
            wasPlayingBeforePause = false
        }
    }

    companion object {
        /**
         * [VideoScaffoldState] 的状态持久化 Saver，用于页面跨路由切换返回时恢复播放控制状态。
         */
        val Saver: Saver<VideoScaffoldState, *> = Saver(
            save = { state ->
                listOf(
                    state.isFullscreen,
                    state.controlsVisible,
                    state.isSpeedMenuVisible,
                    state.brightness,
                    state.wasPlayingBeforePause,
                )
            },
            restore = { list ->
                val isFullscreen = list[0] as Boolean
                val controlsVisible = list[1] as Boolean
                val isSpeedMenuVisible = list[2] as Boolean
                val brightness = list[3] as Float
                val wasPlayingBeforePause = list[4] as Boolean

                VideoScaffoldState(
                    initialIsFullscreen = isFullscreen,
                    initialControlsVisible = controlsVisible,
                    initialBrightness = brightness,
                ).apply {
                    this.isSpeedMenuVisible = isSpeedMenuVisible
                    this.wasPlayingBeforePause = wasPlayingBeforePause
                }
            },
        )
    }
}
