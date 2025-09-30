package com.xiaoyv.bangumi.shared.ui.component.layout.adaptive

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowWidthSizeClass

@Composable
inline fun AdaptiveLayout(
    compat: @Composable () -> Unit,
    medium: @Composable () -> Unit,
    expanded: @Composable () -> Unit,
) {
    when (currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass) {
        WindowWidthSizeClass.COMPACT -> compat()
        WindowWidthSizeClass.MEDIUM -> medium()
        WindowWidthSizeClass.EXPANDED -> expanded()
        else -> compat()
    }
}

@Composable
inline fun AdaptiveLayout(
    compat: @Composable () -> Unit,
    other: @Composable () -> Unit,
) {
    when (currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass) {
        WindowWidthSizeClass.COMPACT -> compat()
        WindowWidthSizeClass.MEDIUM -> other()
        WindowWidthSizeClass.EXPANDED -> other()
        else -> other()
    }
}