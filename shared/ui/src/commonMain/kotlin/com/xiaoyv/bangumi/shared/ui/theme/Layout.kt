package com.xiaoyv.bangumi.shared.ui.theme

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass

val contentMargin: Dp
    @Composable
    get() = when (currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass) {
        WindowWidthSizeClass.COMPACT -> 16.dp
        WindowWidthSizeClass.MEDIUM -> 32.dp
        WindowWidthSizeClass.EXPANDED -> 64.dp
        else -> 32.dp
    }