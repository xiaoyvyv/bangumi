package com.xiaoyv.bangumi.shared.ui.component.live2d

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.model.response.bgm.terminal.ComposeTerminalSpeech
import com.xiaoyv.bangumi.shared.data.repository.TerminalRepository
import kotlinx.collections.immutable.toImmutableList

/**
 * 邦邦 Web 看板娘/春菜 (chiiLib.ukagaka) 提示文本及预设语料
 *
 * 对应 https://bgm.tv/min/g=js?r771 中的 AJAXtip 及 ukagaka 话术逻辑
 */
object Live2DPreset {
    const val TIP_WAIT_SAVING = "正在保存..."
    const val TIP_WAIT_SAVING_PROGRESS = "正在为你保存收视进度..."
    const val TIP_WAIT_ADDING_FRIEND = "正在添加好友..."
    const val TIP_WAIT_DELETING_TIMELINE = "请稍候，正在删除时间线..."
    const val TIP_WAIT_DELETING_RELATION = "请稍候，正在删除关联条目..."
    const val TIP_WAIT_DELETING_REPLY = "正在删除回复..."

    const val TIP_ADD_SAY_SUCCESS = "恭喜恭喜，吐槽成功咯～\n你可以在时光机里看到自己和好友们的吐槽哟。"
    const val TIP_PROGRESS_SUCCESS = "恭喜恭喜，进度更新成功～"
    const val TIP_ADD_FRIEND_SUCCESS = "恭喜恭喜，好友添加成功咯～"
    const val TIP_DELETE_TIMELINE_SUCCESS = "你选择的时间线已经删除咯～"
    const val TIP_DELETE_RELATION_SUCCESS = "你选择的关联条目已经删除咯～"
    const val TIP_DELETE_REPLY_SUCCESS = "你选择的回复已经删除咯～"
    const val TIP_NO_NOTIFY = "已经没有新提醒咯"
    const val TIP_ERROR = "呜咕，提交出现了一些问题，请稍候再试..."
    const val TIP_NO_SUBJECT = "呜咕，似乎没有这个条目，请检查URL是否正确或者换一个条目关联..."

    /**
     * 默认保底的触摸对话语料库
     */
    val defaultTouchSpeeches by lazy {
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
     * 挂起加载服务端看板娘语料库方法
     *
     * @param repository TerminalRepository 仓库实例
     * @param curPsn 角色 Personality ID
     * @param all 是否获取全部语料
     */
    suspend fun fetchSpeeches(
        repository: TerminalRepository,
        curPsn: Long,
        all: Boolean? = null
    ): Result<List<ComposeTerminalSpeech>> {
        return repository.fetchSpeeches(curPsn, all)
    }

    /**
     * 批量更新完成进度话术模板
     */
    fun progressWatchedTill(epNum: String): String = "恭喜恭喜，你完成 1 - $epNum 话咯～"

    /**
     * 单集看后吐槽提示模板
     */
    fun epCommentPrompt(statusName: String): String = "观看状态已保存为「$statusName」，要不要稍微吐槽一下?"
}

/**
 * 看板娘 Live2D 说话场景枚举
 */
enum class Live2DScene {
    /**
     * 随机/通用说话
     */
    General,

    /**
     * 单集看后吐槽提示
     */
    EpCommentPrompt,

    /**
     * 进度更新中/完成
     */
    ProgressUpdate,

    /**
     * 添加好友
     */
    FriendAdd,

    /**
     * 删除（时间线/回复/关联条目）
     */
    DeleteAction,

    /**
     * 提醒消息
     */
    Notify
}

/**
 * 看板娘 Live2D 场景事件数据载荷
 */
@Immutable
data class Live2DPayload(
    /**
     * 说话场景
     */
    val scene: Live2DScene = Live2DScene.General,

    /**
     * 通用目标 ID (例如 剧集 epId, 条目 subjectId, 动态 tmlId 等)
     */
    val id: Long = 0L,

    /**
     * 数量统计 (例如 未读提醒数 count 等)
     */
    val count: Int = 0,

    /**
     * 业务状态/类型描述 (例如 "看过", "say" 等)
     */
    val type: String = "",

    /**
     * 额外扩展属性
     */
    val extra: String = ""
)

/**
 * 看板娘 Live2D 说话气泡内容模型 (包含文本与数据载荷)
 */
@Immutable
data class Live2DSpeechContent(
    val message: String,
    val payload: Live2DPayload = Live2DPayload()
) {
    val scene: Live2DScene
        get() = payload.scene
}
