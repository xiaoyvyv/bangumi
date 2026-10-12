package com.xiaoyv.bangumi.shared.ui.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

/**
 * 自定义菜单控件的触感反馈，与原生 Miuix 的打开及确认反馈保持一致。
 *
 * @param type 打开菜单使用 ContextClick，选择选项使用 Confirm。
 */
@Composable
fun rememberMenuHapticFeedback(type: HapticFeedbackType = HapticFeedbackType.ContextClick): () -> Unit {
    val feedback = LocalHapticFeedback.current
    return remember(feedback, type) { { feedback.performHapticFeedback(type) } }
}
