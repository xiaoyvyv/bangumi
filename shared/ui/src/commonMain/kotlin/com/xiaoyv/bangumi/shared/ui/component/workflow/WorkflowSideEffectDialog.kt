package com.xiaoyv.bangumi.shared.ui.component.workflow

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetValue.Expanded
import androidx.compose.material3.SheetValue.Hidden
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_cancel
import com.xiaoyv.bangumi.core_resource.resources.global_confirm
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionProgressDialogMode
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionConfirmEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionInputDialogEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionProgressDialogEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionSelectDialogEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionSyncCookieEffect
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.milliseconds

/**
 * 工作流活动进度任务的聚合对话框。
 *
 * 对话框不可通过返回键或点击外部区域关闭，展示当前所有进行中的进度任务。
 *
 * @param tasks 当前活动的进度任务列表
 */
@Composable
fun WorkflowProgressAlertDialog(
    tasks: List<WorkflowSideEffectData<ActionProgressDialogEffect>>,
) {
    AlertDialog(
        onDismissRequest = {},
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                tasks.forEachIndexed { index, task ->
                    val effect = task.effect
                    if (index > 0) {
                        androidx.compose.material3.HorizontalDivider(
                            modifier = Modifier.padding(vertical = ContentMarginHalf),
                        )
                    }
                    if (effect.title.isNotBlank()) {
                        Text(text = effect.title, style = MaterialTheme.typography.titleSmall)
                    }
                    if (effect.message.isNotBlank()) {
                        Text(
                            text = effect.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = ContentMarginHalf),
                        )
                    }
                    if (effect.mode == ActionProgressDialogMode.DETERMINATE) {
                        LinearProgressIndicator(
                            progress = { ((effect.progress ?: 0f) / (effect.maxProgress ?: 1f)).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = ContentMarginHalf),
                        )
                    } else {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = ContentMarginHalf),
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {},
    )
}

/**
 * 工作流内置的原生 Compose Material3 二次确认对话框。
 *
 * @param effect 二次确认 SideEffect 数据模型
 * @param onConfirm 确认按钮点击回调
 * @param onCancel 取消按钮或弹窗关闭回调
 */
@Composable
fun WorkflowConfirmAlertDialog(
    effect: ActionConfirmEffect,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    val defaultConfirmText = stringResource(Res.string.global_confirm)
    val defaultCancelText = stringResource(Res.string.global_cancel)

    AlertDialog(
        onDismissRequest = onCancel,
        title = if (effect.title.isNotBlank()) {
            { Text(text = effect.title) }
        } else null,
        text = if (effect.message.isNotBlank()) {
            { Text(text = effect.message) }
        } else null,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = effect.confirmText.ifBlank { defaultConfirmText })
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(text = effect.cancelText.ifBlank { defaultCancelText })
            }
        },
    )
}

/**
 * 工作流内置的原生 Compose Material3 文本输入对话框。
 *
 * 支持输入法 Insets 自动适配 padding，并在展示时自动获取键盘焦点与绑定键盘完成动作。
 *
 * @param effect 文本输入 SideEffect 数据模型
 * @param onConfirm 确认按钮点击回调（带输入文本参数）
 * @param onCancel 取消按钮或弹窗关闭回调
 */
@Composable
fun WorkflowInputAlertDialog(
    effect: ActionInputDialogEffect,
    onConfirm: (input: String) -> Unit,
    onCancel: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    var inputText by remember(effect) {
        mutableStateOf(TextFieldValue(effect.defaultValue, TextRange(effect.defaultValue.length)))
    }
    val defaultConfirmText = stringResource(Res.string.global_confirm)
    val defaultCancelText = stringResource(Res.string.global_cancel)

    LaunchedEffect(focusRequester) {
        delay(100.milliseconds)
        focusRequester.requestFocus()
    }

    AlertDialog(
        modifier = Modifier.padding(WindowInsets.ime.asPaddingValues()),
        onDismissRequest = onCancel,
        title = if (effect.title.isNotBlank()) {
            { Text(text = effect.title) }
        } else null,
        text = {
            OutlinedTextField(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .fillMaxWidth(),
                value = inputText,
                onValueChange = { inputText = it },
                label = if (effect.subtitle.isNotBlank()) {
                    { Text(text = effect.subtitle) }
                } else null,
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (effect.onlyNumber) KeyboardType.Number else KeyboardType.Text,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onConfirm(inputText.text.trim()) }
                ),
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(inputText.text.trim()) }) {
                Text(text = effect.confirmText.ifBlank { defaultConfirmText })
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(text = effect.cancelText.ifBlank { defaultCancelText })
            }
        },
    )
}

/**
 * 工作流内置的原生 Compose Material3 列表选择对话框（支持单选与多选）。
 *
 * - 单选模式（[ActionSelectDialogEffect.isMultiSelect] = false）：无确定/取消按钮，直接点击条目即选中并提交结果。
 * - 多选模式（[ActionSelectDialogEffect.isMultiSelect] = true）：包含 Checkbox 勾选框与确定/取消按钮，点击确定后提交选中集合。
 *
 * @param effect 列表选择 SideEffect 数据模型
 * @param onConfirm 确认/选择点击回调（同时提供选中 0-based index 集合与 value 集合）
 * @param onCancel 取消按钮或弹窗关闭回调
 */
@Composable
fun WorkflowSelectAlertDialog(
    effect: ActionSelectDialogEffect,
    onConfirm: (selectedIndices: List<Int>, selectedValues: List<String>) -> Unit,
    onCancel: () -> Unit,
) {
    val defaultConfirmText = stringResource(Res.string.global_confirm)
    val defaultCancelText = stringResource(Res.string.global_cancel)

    val initialIndices = remember(effect) {
        if (effect.defaultIndices.isNotEmpty()) {
            effect.defaultIndices.filter { it in effect.options.indices }.toSet()
        } else {
            effect.options.mapIndexedNotNull { index, option ->
                if (option.value in effect.defaultValues) index else null
            }.toSet()
        }
    }

    var selectedIndices by remember(effect) { mutableStateOf(initialIndices) }

    AlertDialog(
        onDismissRequest = onCancel,
        title = if (effect.title.isNotBlank()) {
            { Text(text = effect.title) }
        } else null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                if (effect.subtitle.isNotBlank()) {
                    Text(
                        text = effect.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                effect.options.forEachIndexed { index, option ->
                    val isSelected = selectedIndices.contains(index)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (effect.isMultiSelect) {
                                    selectedIndices = if (isSelected) {
                                        selectedIndices - index
                                    } else {
                                        selectedIndices + index
                                    }
                                } else {
                                    onConfirm(listOf(index), listOf(option.value))
                                }
                            }
                            .padding(vertical = 12.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (effect.isMultiSelect) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = null
                            )
                        } else {
                            RadioButton(
                                selected = isSelected,
                                onClick = null
                            )
                        }

                        Text(
                            text = option.title.ifBlank { option.value },
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (effect.isMultiSelect) {
                TextButton(
                    onClick = {
                        val orderedIndices = selectedIndices.toList()
                        val orderedValues = orderedIndices.map { effect.options[it].value }
                        onConfirm(orderedIndices, orderedValues)
                    }
                ) {
                    Text(text = effect.confirmText.ifBlank { defaultConfirmText })
                }
            }
        },
        dismissButton = {
            if (effect.isMultiSelect) {
                TextButton(onClick = onCancel) {
                    Text(text = effect.cancelText.ifBlank { defaultCancelText })
                }
            }
        },
    )
}

/**
 * 工作流内置的网页 Cookie 同步 BottomSheet 弹窗。
 *
 * 弹出时以 BottomSheet 展现网页同步界面，点击确定或关闭操作后，均会回调触发 Cookie 同步与弹窗关闭。
 *
 * @param effect Cookie 同步 SideEffect 数据模型
 * @param onDismiss 划走关闭回调（触发 Cookie 同步与出队）
 */
@Composable
fun WorkflowSyncCookieBottomSheetDialog(
    effect: ActionSyncCookieEffect,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberBottomSheetState(initialValue = Hidden, setOf(Hidden, Expanded))
    var isTouchInsideWebView by remember { mutableStateOf(false) }

    ModalBottomSheet(
        modifier = Modifier.statusBarsPadding(),
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        sheetGesturesEnabled = !isTouchInsideWebView,
        properties = ModalBottomSheetProperties(
            shouldDismissOnBackPress = true,
            shouldDismissOnClickOutside = false
        )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ContentMargin, vertical = ContentMarginHalf)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Cookie Sync：" + effect.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    modifier = Modifier.padding(top = ContentMarginHalf),
                    text = effect.url,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = ContentMarginHalf, bottom = ContentMarginHalf),
                        text = "适用于处理人机验证、登录网页等，让后续请求（请求节点需声明启用Cookie）持有网页的身份和验证信息，操作完成关闭即可",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }

            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent(PointerEventPass.Initial)
                                val anyPressed = event.changes.any { it.pressed }
                                if (isTouchInsideWebView != anyPressed) {
                                    isTouchInsideWebView = anyPressed
                                }
                            }
                        }
                    }
            ) {
                val navigator = rememberWebViewNavigator()
                val state = rememberWebViewState(
                    url = effect.url,
                    additionalHttpHeaders = effect.headers,
                    extraSettings = {
                        customUserAgentString = effect.userAgent
                    }
                )

                WebView(
                    modifier = Modifier.fillMaxSize(),
                    state = state,
                    captureBackPresses = false,
                    navigator = navigator
                )

                val loadingState = state.loadingState
                if (loadingState is LoadingState.Loading) {
                    val progress by animateFloatAsState(
                        targetValue = loadingState.progress,
                        animationSpec = tween(),
                    )

                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        progress = { progress },
                        gapSize = 0.dp,
                        strokeCap = StrokeCap.Square,
                        drawStopIndicator = {}
                    )
                }
            }
        }
    }
}
