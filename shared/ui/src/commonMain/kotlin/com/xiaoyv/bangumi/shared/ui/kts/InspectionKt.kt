package com.xiaoyv.bangumi.shared.ui.kts

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode

/**
 * 在预览模式下隐藏内容
 */
@Composable
inline fun HideInPreview(content: @Composable () -> Unit) {
    if (LocalInspectionMode.current) return
    content()
}
