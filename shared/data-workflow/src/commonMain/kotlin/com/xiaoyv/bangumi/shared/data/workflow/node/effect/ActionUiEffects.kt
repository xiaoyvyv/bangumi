package com.xiaoyv.bangumi.shared.data.workflow.node.effect

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionSideEffect
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSelectDialogConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSelectOutputMode

/**
 * 请求宿主向用户显示短文本提示的副作用。
 *
 * @param message 已解析的提示内容。
 */
@Immutable
data class ActionShowToastEffect(
    /**
     * 要展示给用户的提示文本。
     */
    val message: String,
) : ActionSideEffect

/**
 * 请求宿主将文本写入系统剪贴板的副作用。
 *
 * @param text 已解析的待复制文本。
 */
@Immutable
data class ActionWriteClipboardEffect(
    /**
     * 要写入系统剪贴板的文本。
     */
    val text: String,
) : ActionSideEffect

/**
 * 请求宿主弹窗向用户进行二次确认的副作用。
 */
@Immutable
data class ActionConfirmEffect(
    val title: String = "",
    val message: String,
    val confirmText: String = "",
    val cancelText: String = "",
) : ActionSideEffect

/**
 * 请求宿主调起系统分享菜单的副作用。
 */
@Immutable
data class ActionShareEffect(
    val title: String = "",
    val text: String,
    val url: String = "",
) : ActionSideEffect

/**
 * 请求宿主发送本地通知的副作用。
 */
@Immutable
data class ActionNotificationEffect(
    val title: String = "",
    val content: String,
) : ActionSideEffect

/**
 * 请求宿主读取系统剪贴板的副作用。
 */
@Immutable
data class ActionReadClipboardEffect(
    val outputKey: String = "result",
) : ActionSideEffect

/**
 * 请求宿主触发设备震动的副作用。
 */
@Immutable
data class ActionVibrateEffect(
    val durationMillis: Long = 100L,
) : ActionSideEffect

/**
 * 请求宿主弹出文本输入对话框的副作用。
 */
@Immutable
data class ActionInputDialogEffect(
    val title: String = "",
    val subtitle: String = "",
    val defaultValue: String = "",
    val outputKey: String = "result",
    val confirmText: String = "",
    val cancelText: String = "",
    val onlyNumber: Boolean = false,
) : ActionSideEffect

/**
 * 请求宿主调起全屏图片预览的副作用。
 *
 * @property index 初始展示的图片索引
 * @property images 图片 URL 列表
 */
@Immutable
data class ActionImagePreviewEffect(
    val index: Int = 0,
    val images: List<String> = emptyList(),
) : ActionSideEffect

/**
 * 列表选择对话框的单项选项模型。
 *
 * @param title 选项展示给用户的标题文本
 * @param value 该选项选定后的唯一取值
 */
@Immutable
@kotlinx.serialization.Serializable
data class ActionSelectDialogOption(
    val title: String = "",
    val value: String = "",
) {
    companion object {
        const val KEY_TITLE = ActionSelectDialogConfigKey.TITLE
        const val KEY_VALUE = ActionSelectDialogConfigKey.VALUE
    }
}

/**
 * 请求宿主弹出单选/多选列表选择对话框的副作用。
 *
 * @param title 弹窗标题
 * @param subtitle 弹窗副标题/提示说明
 * @param options 可供选择的条目列表 [{"title":"xxx", "value":"xxx"}]
 * @param defaultValues 默认选中的 value 集合
 * @param defaultIndices 默认选中的 0-based 索引集合
 * @param isMultiSelect 是否为多选模式。false 为单选模式（直接点击条目提交，无确定/取消按钮）；true 为多选模式（含确定/取消按钮）。
 * @param outputMode 输出结果模式（value: 输出选中的值, index: 输出选中的索引, both: 输出包含值与索引的结构）
 * @param outputKey 提交选定结果的节点输出路径键
 * @param confirmText 自定义确认按钮文本
 * @param cancelText 自定义取消按钮文本
 */
@Immutable
data class ActionSelectDialogEffect(
    val title: String = "",
    val subtitle: String = "",
    val options: List<ActionSelectDialogOption> = emptyList(),
    val defaultValues: List<String> = emptyList(),
    val defaultIndices: List<Int> = emptyList(),
    val isMultiSelect: Boolean = false,
    val outputMode: String = ActionSelectOutputMode.VALUE,
    val outputKey: String = "result",
    val confirmText: String = "",
    val cancelText: String = "",
) : ActionSideEffect

/**
 * 请求宿主弹出 BottomSheet 交互以同步 Web Cookie 到本地 CookieStore 的副作用。
 *
 * @param url 目标的网页地址。
 * @param title 弹窗标题。
 * @param headers 网页请求头。
 * @param userAgent 自定义 User-Agent。
 */
@Immutable
data class ActionSyncCookieEffect(
    val url: String = "",
    val title: String = "同步 Cookie",
    val headers: Map<String, String> = emptyMap(),
    val userAgent: String = "",
) : ActionSideEffect

