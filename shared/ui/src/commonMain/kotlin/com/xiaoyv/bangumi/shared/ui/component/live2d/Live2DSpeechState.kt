package com.xiaoyv.bangumi.shared.ui.component.live2d

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.xiaoyv.bangumi.shared.component.Live2DState
import com.xiaoyv.bangumi.shared.component.rememberLive2DState
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.data.model.response.bgm.terminal.ComposeTerminalSpeech
import com.xiaoyv.bangumi.shared.data.repository.TerminalRepository
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import kotlin.time.Duration.Companion.milliseconds

private val defaultTouchSpeeches by lazy {
    listOf(
        "欢迎来到 Bangumi 班固米！(≧∇≦)ﾉ",
        "今天也有在好好看剧/做笔记吗？",
        "有什么需要帮你的吗？",
        "（眨眨眼）喵~ 戳我干嘛？",
        "保持好心情，今天又是元气满满的一天！",
        "快去查看最新的时间线和热评吧！"
    ).map { ComposeTerminalSpeech(speech = it) }
        .toImmutableList()
}

/**
 * 看板娘 Live2D 说话气泡及模型控制器
 */
@Stable
class Live2DSpeechState(
    private val scope: CoroutineScope,
    private val live2DState: Live2DState,
    private val repository: TerminalRepository? = null,
) {
    val state: Live2DState
        get() = live2DState

    /**
     * 当前说话文本
     */
    var text by mutableStateOf("")
        private set

    /**
     * 气泡是否处于显示状态
     */
    var isVisible by mutableStateOf(false)
        private set

    /**
     * 全部的语料库列表
     */
    var speechList by mutableStateOf(defaultTouchSpeeches)
        private set

    /**
     * 语料库加载状态
     */
    var isLoadingSpeeches by mutableStateOf(false)
        private set

    private var autoHideJob: Job? = null

    /**
     * 从服务端异步加载看板娘语料库
     *
     * @param curPsn 角色 Personality ID
     * @param all 是否获取全部语料
     */
    fun fetchSpeeches(curPsn: Long, all: Boolean? = null) {
        val repo = repository ?: return

        scope.launch {
            isLoadingSpeeches = true
            repo.fetchSpeeches(curPsn, all)
                .onSuccess { speeches ->
                    speechList = speeches.toImmutableList()
                }
                .onFailure {
                    debugLog { "Liv2d 语料加载失败了" }
                }
            isLoadingSpeeches = false
        }
    }

    /**
     * 随机抽取语料库中的一条进行说话
     *
     * @param durationMillis 显示持续时间（毫秒）
     */
    fun speakRandom(durationMillis: Long? = 5000L) {
        val message = speechList.randomOrNull()?.speech ?: defaultTouchSpeeches.randomOrNull()?.speech
        if (message.isNullOrBlank().not()) {
            speak(message, durationMillis)
        }
    }

    /**
     * 让看板娘说话
     *
     * @param message 说话文本内容
     * @param durationMillis 气泡显示持续时间（毫秒），为 null 或 <= 0 时持续显示，默认 5000ms (5秒)
     */
    fun speak(message: String, durationMillis: Long? = 5000L) {
        autoHideJob?.cancel()
        val content = message.trim()
        if (content.isBlank()) {
            isVisible = false
            return
        }
        text = content
        isVisible = true

        if (durationMillis != null && durationMillis > 0) {
            autoHideJob = scope.launch {
                delay(durationMillis.milliseconds)
                isVisible = false
            }
        }
    }

    /**
     * 手动隐藏说话气泡
     */
    fun dismiss() {
        autoHideJob?.cancel()
        isVisible = false
    }

    /**
     * 播放指定分组的动作动画
     */
    fun setMotion(group: String, index: Int = 0) {
        live2DState.setMotion(group, index)
    }

    /**
     * 设置表情
     */
    fun setExpression(expressionId: String) {
        live2DState.setExpression(expressionId)
    }
}

/**
 * 记住 Live2DSpeechState，关联 Live2DState 自动注入 TerminalRepository
 */
@Composable
fun rememberLive2DSpeechState(
    live2DState: Live2DState = rememberLive2DState(),
    repository: TerminalRepository = koinInject(),
): Live2DSpeechState {
    val scope = rememberCoroutineScope()
    return remember(scope, repository, live2DState) {
        Live2DSpeechState(
            scope = scope,
            repository = repository,
            live2DState = live2DState
        )
    }
}

/**
 * 提供全局 Compose 上下文中的 Live2D 说话控制器
 */
val LocalLive2DSpeechController = staticCompositionLocalOf {
    Live2DSpeechState(CoroutineScope(Dispatchers.Main), Live2DState())
}
