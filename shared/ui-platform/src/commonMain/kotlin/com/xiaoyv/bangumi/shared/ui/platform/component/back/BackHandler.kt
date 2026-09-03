package com.xiaoyv.bangumi.shared.ui.platform.component.back

import androidx.compose.runtime.Composable

/**
 * 拦截系统返回事件的跨平台组件。
 *
 * @param enabled 是否启用拦截。
 * @param onBack 触发返回事件时的回调。
 */
@Composable
expect fun PlatformBackHandler(
    enabled: Boolean = true,
    onBack: () -> Unit,
)
