package com.xiaoyv.bangumi.shared.ui.platform.component.back

import androidx.compose.runtime.Composable

@Composable
actual fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) {
    // iOS 没有系统级物理返回按键
}
