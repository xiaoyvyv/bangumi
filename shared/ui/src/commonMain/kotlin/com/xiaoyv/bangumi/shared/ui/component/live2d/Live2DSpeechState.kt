package com.xiaoyv.bangumi.shared.ui.component.live2d

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.component.Live2DState
import com.xiaoyv.bangumi.shared.component.rememberLive2DState
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.data.repository.TerminalRepository
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * 看板娘 Live2D 说话气泡及模型控制器
 *
 * 还原 https://bgm.tv/min/g=js?r771 中 chiiLib.ukagaka 的说话逻辑及场景上下文
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
     * 当前说话的结构化内容 (包含 Live2DPayload)
     */
    var speechContent by mutableStateOf(Live2DSpeechContent(message = ""))
        private set

    /**
     * 当前文本
     */
    val text: String
        get() = speechContent.message

    /**
     * 气泡是否处于显示状态
     */
    var isVisible by mutableStateOf(false)
        private set

    /**
     * 全部的语料库列表
     */
    var speechList by mutableStateOf(Live2DPreset.defaultTouchSpeeches)
        private set

    /**
     * 语料库加载状态
     */
    var isLoadingSpeeches by mutableStateOf(false)
        private set

    /**
     * 是否处于休眠动作状态
     */
    var isInSleepMode by mutableStateOf(false)
        private set

    private var autoHideJob: Job? = null
    private var sleepTimerJob: Job? = null
    private var lastVoiceTime: Long = 0L

    /**
     * 异步加载看板娘语料库
     *
     * @param curPsn 角色 Personality ID
     * @param all 是否获取全部语料
     */
    fun fetchSpeeches(curPsn: Long, all: Boolean? = null) {
        val repo = repository ?: return

        scope.launch {
            isLoadingSpeeches = true
            Live2DPreset.fetchSpeeches(repo, curPsn, all)
                .onSuccess { speeches ->
                    if (speeches.isNotEmpty()) {
                        speechList = speeches.toImmutableList()
                    }
                }
                .onFailure {
                    debugLog { "Live2D 语料加载失败了" }
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
        val message = speechList.randomOrNull()?.speech ?: Live2DPreset.defaultTouchSpeeches.randomOrNull()?.speech
        if (!message.isNullOrBlank()) {
            speak(message = message, payload = Live2DPayload(scene = Live2DScene.General), durationMillis = durationMillis)
        }
    }

    /**
     * 让看板娘说话
     *
     * @param message 说话文本内容
     * @param payload 场景与事件载荷数据模型
     * @param durationMillis 气泡显示持续时间（毫秒），为 null 或 <= 0 时持续显示，默认 5000ms
     */
    fun speak(
        message: String,
        payload: Live2DPayload = Live2DPayload(scene = Live2DScene.General),
        durationMillis: Long? = 5000L
    ) {
        presentSpeechContent(
            Live2DSpeechContent(
                message = message.trim(),
                payload = payload
            ),
            durationMillis = durationMillis
        )
    }

    /**
     * 展示说话内容模型
     */
    fun presentSpeechContent(content: Live2DSpeechContent, durationMillis: Long? = 5000L) {
        wakeUp()
        autoHideJob?.cancel()

        speechContent = content
        isVisible = true

        if (durationMillis != null && durationMillis > 0) {
            autoHideJob = scope.launch {
                delay(durationMillis.milliseconds)
                isVisible = false
            }
        }
    }

    /**
     * 单集收视保存后吐槽提示
     *
     * @param epId 剧集 ID (传入 payload.id)
     * @param statusName 观看状态名称 (例如 "看过")
     */
    fun presentEpCommentPrompt(epId: Long, statusName: String) {
        val promptText = Live2DPreset.epCommentPrompt(statusName)
        speak(
            message = promptText,
            payload = Live2DPayload(
                scene = Live2DScene.EpCommentPrompt,
                id = epId,
                type = statusName
            ),
            durationMillis = 8000L
        )
    }

    /**
     * 保存收视进度中...
     */
    fun triggerProgressSaving() {
        speak(
            message = Live2DPreset.TIP_WAIT_SAVING_PROGRESS,
            payload = Live2DPayload(scene = Live2DScene.ProgressUpdate),
            durationMillis = null
        )
    }

    /**
     * 批量收视更新成功提示
     */
    fun triggerProgressSuccess(epNum: String? = null) {
        val msg = if (epNum.isNullOrBlank()) {
            Live2DPreset.TIP_PROGRESS_SUCCESS
        } else {
            Live2DPreset.progressWatchedTill(epNum)
        }
        speak(
            message = msg,
            payload = Live2DPayload(
                scene = Live2DScene.ProgressUpdate,
                extra = epNum.orEmpty()
            ),
            durationMillis = 3000L
        )
    }

    /**
     * 提交错误提示
     */
    fun triggerActionError(errorMsg: String = Live2DPreset.TIP_ERROR) {
        speak(
            message = errorMsg,
            payload = Live2DPayload(scene = Live2DScene.General),
            durationMillis = 3000L
        )
    }

    /**
     * 提醒动作
     */
    fun triggerNotifyMotion(count: Int) {
        if (count > 0) {
            setMotion("Notify", 0)
            speak(
                message = "您有 $count 条未读提醒消息喔！",
                payload = Live2DPayload(
                    scene = Live2DScene.Notify,
                    count = count
                ),
                durationMillis = 4000L
            )
        } else {
            speak(
                message = Live2DPreset.TIP_NO_NOTIFY,
                payload = Live2DPayload(
                    scene = Live2DScene.Notify,
                    count = 0
                ),
                durationMillis = 3000L
            )
        }
    }

    /**
     * 播放随机音效
     *
     * 随机抽取 29-60 号语音文件
     */
    fun playRandomVoice(): Int {
        val now = System.currentTimeMillis()
        if (now - lastVoiceTime < 180) return -1
        lastVoiceTime = now

        val voiceId = Random.nextInt(29, 61)
        debugLog { "播放 Live2D 语音: wave$voiceId.wav" }
        return voiceId
    }

    /**
     * 启动不操作休眠倒计时 (对应 10 秒空闲 Sleeping motion)
     */
    fun startSleepTimer() {
        sleepTimerJob?.cancel()
        sleepTimerJob = scope.launch {
            delay(10.seconds)
            if (!isInSleepMode) {
                isInSleepMode = true
                setMotion("Sleeping", 0)
            }
        }
    }

    /**
     * 唤醒模型 (唤醒后切回 Idle motion)
     */
    fun wakeUp() {
        sleepTimerJob?.cancel()
        if (isInSleepMode) {
            isInSleepMode = false
            setMotion("Idle", 0)
        }
        startSleepTimer()
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
