package com.xiaoyv.bangumi.shared.ui.component.workflow

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import com.multiplatform.webview.cookie.WebViewCookieManager
import com.xiaoyv.bangumi.shared.data.api.client.cookie.WorkflowCookiesStorage
import com.xiaoyv.bangumi.shared.data.workflow.engine.ActionSideEffectResult
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSelectDialogConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSelectOutputMode
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionConfirmEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionImagePreviewEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionInputDialogEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionNotificationEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionOpenExternalAppEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionOpenExternalUrlEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionOpenInternalWebEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionReadClipboardEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionSelectDialogEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionShareEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionShowToastEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionSyncCookieEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionVibrateEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionWriteClipboardEffect
import com.xiaoyv.bangumi.shared.ui.component.action.LocalActionHandler
import com.xiaoyv.bangumi.shared.ui.component.popup.LocalPopupTipState
import io.ktor.client.plugins.cookies.addCookie
import io.ktor.http.Cookie
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.koin.compose.koinInject

/**
 * 工作流 SideEffect 宿主容器（零参数默认实现版本）。
 *
 * 全自动接入 UI 模块基建，无需传入任何回调参数：
 * - [LocalActionHandler]（剪贴板、浏览器、第三方应用、应用内网页、图片画廊、分享）
 * - [LocalPopupTipState]（Toast 提示）
 * - [LocalClipboardManager]（读取剪贴板）
 * - [LocalHapticFeedback]（震动/触觉反馈）
 * - 默认 M3 二次确认对话框、文本输入对话框与列表选择对话框（[WorkflowConfirmAlertDialog] / [WorkflowInputAlertDialog] / [WorkflowSelectAlertDialog]）
 *
 * @param hostState 宿主控制器
 * @param modifier 修饰符
 */
@Composable
fun WorkflowSideEffectHost(
    hostState: WorkflowSideEffectHostState,
    modifier: Modifier = Modifier,
) {
    val actionHandler = LocalActionHandler.current
    val popupTipState = LocalPopupTipState.current
    val clipboardManager = LocalClipboardManager.current
    val hapticFeedback = LocalHapticFeedback.current

    WorkflowSideEffectHost(
        hostState = hostState,
        onShowToast = { message -> popupTipState.showToast(message) },
        onWriteClipboard = actionHandler::copyContent,
        onReadClipboard = { clipboardManager.getText()?.text },
        onOpenExternalUrl = actionHandler::openInBrowser,
        onOpenExternalApp = { uri, fallbackUrl ->
            val url = if (fallbackUrl.isNullOrBlank()) uri else fallbackUrl
            actionHandler.openInBrowser(url)
        },
        onOpenInternalWeb = { url -> actionHandler.openBgmLink(url) },
        onImagePreview = actionHandler::openImages,
        onShareText = actionHandler::shareContent,
        onSendNotification = { _, _ -> },
        onVibrate = { hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress) },
        confirmDialogSlot = { effect, onConfirm, onCancel ->
            WorkflowConfirmAlertDialog(
                effect = effect,
                onConfirm = onConfirm,
                onCancel = onCancel,
            )
        },
        inputDialogSlot = { effect, onConfirm, onCancel ->
            WorkflowInputAlertDialog(
                effect = effect,
                onConfirm = onConfirm,
                onCancel = onCancel,
            )
        },
        selectDialogSlot = { effect, onConfirm, onCancel ->
            WorkflowSelectAlertDialog(
                effect = effect,
                onConfirm = onConfirm,
                onCancel = onCancel,
            )
        },
        syncCookieDialogSlot = { effect, onDismiss ->
            WorkflowSyncCookieBottomSheetDialog(
                effect = effect,
                onDismiss = onDismiss,
            )
        },
        modifier = modifier,
    )
}

/**
 * 工作流 SideEffect 宿主容器（纯回调版本）。
 *
 * 所有事件响应与平台处理均由调用方显式传入，无任何默认隐式行为，适合完全自定义全量 SideEffect 的场景。
 *
 * @param hostState 宿主控制器
 * @param onShowToast 弹出 Toast 提示
 * @param onWriteClipboard 写入剪贴板
 * @param onReadClipboard 读取剪贴板
 * @param onOpenExternalUrl 打开外部浏览器
 * @param onOpenExternalApp 打开第三方应用
 * @param onOpenInternalWeb 打开应用内网页
 * @param onImagePreview 调起大图预览画廊
 * @param onShareText 分享文本
 * @param onSendNotification 发送通知
 * @param onVibrate 触发震动/触觉反馈
 * @param confirmDialogSlot 自定义二次确认弹窗插槽
 * @param inputDialogSlot 自定义文本输入弹窗插槽
 * @param selectDialogSlot 自定义列表选择弹窗插槽
 * @param syncCookieDialogSlot 自定义 Cookie 同步 BottomSheet 弹窗插槽
 * @param modifier 修饰符
 */
@Composable
fun WorkflowSideEffectHost(
    hostState: WorkflowSideEffectHostState,
    onShowToast: (message: String) -> Unit,
    onWriteClipboard: (text: String) -> Unit,
    onReadClipboard: suspend () -> String?,
    onOpenExternalUrl: (url: String) -> Unit,
    onOpenExternalApp: (uri: String, fallbackUrl: String?) -> Unit,
    onOpenInternalWeb: (url: String) -> Unit,
    onImagePreview: (images: List<String>, index: Int) -> Unit,
    onShareText: (text: String) -> Unit,
    onSendNotification: (title: String, content: String) -> Unit,
    onVibrate: () -> Unit,
    confirmDialogSlot: @Composable (
        effect: ActionConfirmEffect,
        onConfirm: () -> Unit,
        onCancel: () -> Unit,
    ) -> Unit,
    inputDialogSlot: @Composable (
        effect: ActionInputDialogEffect,
        onConfirm: (input: String) -> Unit,
        onCancel: () -> Unit,
    ) -> Unit,
    selectDialogSlot: @Composable (
        effect: ActionSelectDialogEffect,
        onConfirm: (selectedIndices: List<Int>, selectedValues: List<String>) -> Unit,
        onCancel: () -> Unit,
    ) -> Unit,
    syncCookieDialogSlot: @Composable (
        effect: ActionSyncCookieEffect,
        onConfirm: () -> Unit,
    ) -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val currentOneShot = hostState.currentOneShotData

    // 依次安全地由 UI 层处理单向 SideEffect 队列
    LaunchedEffect(currentOneShot?.id) {
        val entry = currentOneShot ?: return@LaunchedEffect
        when (val effect = entry.effect) {
            is ActionShowToastEffect -> {
                onShowToast(effect.message)
                hostState.popOneShotData { data ->
                    data.onResult(ActionSideEffectResult.Success(buildJsonObject { }))
                }
            }

            is ActionWriteClipboardEffect -> {
                onWriteClipboard(effect.text)
                hostState.popOneShotData { data ->
                    data.onResult(ActionSideEffectResult.Success(buildJsonObject { }))
                }
            }

            is ActionReadClipboardEffect -> {
                coroutineScope.launch {
                    val text = onReadClipboard().orEmpty()
                    hostState.popOneShotData { data ->
                        data.onResult(
                            ActionSideEffectResult.Success(
                                buildJsonObject { put(effect.outputKey, JsonPrimitive(text)) }
                            )
                        )
                    }
                }
            }

            is ActionOpenExternalUrlEffect -> {
                onOpenExternalUrl(effect.url)
                hostState.popOneShotData { data ->
                    data.onResult(ActionSideEffectResult.Success(buildJsonObject { }))
                }
            }

            is ActionOpenExternalAppEffect -> {
                onOpenExternalApp(effect.uri, effect.fallbackUrl)
                hostState.popOneShotData { data ->
                    data.onResult(ActionSideEffectResult.Success(buildJsonObject { }))
                }
            }

            is ActionOpenInternalWebEffect -> {
                onOpenInternalWeb(effect.url)
                hostState.popOneShotData { data ->
                    data.onResult(ActionSideEffectResult.Success(buildJsonObject { }))
                }
            }

            is ActionImagePreviewEffect -> {
                onImagePreview(effect.images, effect.index)
                hostState.popOneShotData { data ->
                    data.onResult(ActionSideEffectResult.Success(buildJsonObject { }))
                }
            }

            is ActionShareEffect -> {
                onShareText(effect.text)
                hostState.popOneShotData { data ->
                    data.onResult(ActionSideEffectResult.Success(buildJsonObject { }))
                }
            }

            is ActionNotificationEffect -> {
                onSendNotification(effect.title, effect.content)
                hostState.popOneShotData { data ->
                    data.onResult(ActionSideEffectResult.Success(buildJsonObject { }))
                }
            }

            is ActionVibrateEffect -> {
                onVibrate()
                hostState.popOneShotData { data ->
                    data.onResult(ActionSideEffectResult.Success(buildJsonObject { }))
                }
            }

            else -> {
                hostState.popOneShotData { data ->
                    data.onResult(ActionSideEffectResult.Success(buildJsonObject { }))
                }
            }
        }
    }

    // 由 UI 层渲染二次确认弹窗，并在响应时处理 onResult
    hostState.currentConfirmData?.let { confirmData ->
        confirmDialogSlot(
            confirmData.effect,
            {
                hostState.popConfirmData { data ->
                    data.onResult(ActionSideEffectResult.Success(buildJsonObject { }))
                }
            },
            {
                hostState.popConfirmData { data ->
                    data.onResult(ActionSideEffectResult.Cancelled)
                }
            },
        )
    }

    // 由 UI 层渲染文本输入弹窗，并在按钮响应时处理 onResult
    hostState.currentInputData?.let { inputData ->
        inputDialogSlot(
            inputData.effect,
            { inputText ->
                hostState.popInputData { data ->
                    data.onResult(
                        ActionSideEffectResult.Success(
                            buildJsonObject { put(data.effect.outputKey, JsonPrimitive(inputText)) }
                        )
                    )
                }
            },
            {
                hostState.popInputData { data ->
                    data.onResult(ActionSideEffectResult.Cancelled)
                }
            },
        )
    }

    // 由 UI 层渲染列表选择弹窗，并在响应时处理 onResult
    hostState.currentSelectData?.let { selectData ->
        selectDialogSlot(
            selectData.effect,
            { selectedIndices, selectedValues ->
                hostState.popSelectData { data ->
                    val effect = data.effect
                    val mode = effect.outputMode.ifBlank { ActionSelectOutputMode.VALUE }

                    val resultJson = buildJsonObject {
                        val primaryResult: JsonElement = if (!effect.isMultiSelect) {
                            val selectedIndex = selectedIndices.firstOrNull() ?: -1
                            val selectedValue = selectedValues.firstOrNull().orEmpty()

                            when (mode) {
                                ActionSelectOutputMode.INDEX -> JsonPrimitive(selectedIndex)
                                ActionSelectOutputMode.BOTH -> buildJsonObject {
                                    put(ActionSelectDialogConfigKey.VALUE, JsonPrimitive(selectedValue))
                                    put(ActionSelectDialogConfigKey.INDEX, JsonPrimitive(selectedIndex))
                                }

                                else -> JsonPrimitive(selectedValue)
                            }
                        } else {
                            when (mode) {
                                ActionSelectOutputMode.INDEX -> JsonArray(selectedIndices.map { JsonPrimitive(it) })
                                ActionSelectOutputMode.BOTH -> buildJsonObject {
                                    put(ActionSelectDialogConfigKey.VALUES, JsonArray(selectedValues.map { JsonPrimitive(it) }))
                                    put(ActionSelectDialogConfigKey.INDICES, JsonArray(selectedIndices.map { JsonPrimitive(it) }))
                                }

                                else -> JsonArray(selectedValues.map { JsonPrimitive(it) })
                            }
                        }

                        put(data.effect.outputKey, primaryResult)
                    }
                    data.onResult(ActionSideEffectResult.Success(resultJson))
                }
            },
            {
                hostState.popSelectData { data ->
                    data.onResult(ActionSideEffectResult.Cancelled)
                }
            },
        )
    }

    // 由 UI 层渲染 Cookie 同步 BottomSheet 弹窗，并在关闭时处理 onResult
    hostState.currentSyncCookieData?.let { syncCookieData ->
        val workflowCookiesStorage = koinInject<WorkflowCookiesStorage>()

        val callback = {
            hostState.popSyncCookieData { data ->
                coroutineScope.launch {
                    val webViewCookieManager = WebViewCookieManager()
                    val cookies = webViewCookieManager.getCookies(data.effect.url)
                    cookies.forEach { cookie ->
                        workflowCookiesStorage.addCookie(
                            data.effect.url, Cookie(
                                name = cookie.name,
                                value = cookie.value,
                                path = cookie.path,
                                domain = cookie.domain,
                            )
                        )
                    }
                    data.onResult(ActionSideEffectResult.Success(buildJsonObject { }))
                }
            }
        }

        syncCookieDialogSlot(syncCookieData.effect, callback)
    }

    // 进度框显示
    if (hostState.progressTasks.isNotEmpty()) {
        WorkflowProgressAlertDialog(
            tasks = hostState.progressTasks,
        )
    }
}
