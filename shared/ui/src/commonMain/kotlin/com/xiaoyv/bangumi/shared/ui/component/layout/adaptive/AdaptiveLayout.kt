package com.xiaoyv.bangumi.shared.ui.component.layout.adaptive

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowSizeClass

@Composable
inline fun AdaptiveLayout(
    compat: @Composable () -> Unit,
    medium: @Composable () -> Unit,
    expanded: @Composable () -> Unit,
) {
    when {
        currentWindowAdaptiveInfoV2().windowSizeClass
            .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> expanded()

        currentWindowAdaptiveInfoV2().windowSizeClass
            .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> medium()

        else -> compat()
    }
}

@Composable
inline fun AdaptiveLayout(
    compat: @Composable () -> Unit,
    other: @Composable () -> Unit,
) {
    when {
        currentWindowAdaptiveInfoV2().windowSizeClass
            .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> other()

        else -> compat()
    }
}
