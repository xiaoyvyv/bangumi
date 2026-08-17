package com.xiaoyv.bangumi.shared.ui.theme

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass

@Immutable
internal data class ContentMargins(
    val full: Dp,
    val half: Dp,
)

internal val LocalContentMargins = compositionLocalOf {
    ContentMargins(full = 16.dp, half = 8.dp)
}

@Composable
internal fun rememberContentMargins(): ContentMargins {
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val full = when {
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> 64.dp
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> 32.dp
        else -> 16.dp
    }
    return remember(full) { ContentMargins(full = full, half = full / 2) }
}

val contentMargin: Dp
    @Composable
    get() = LocalContentMargins.current.full

val contentMarginHalf: Dp
    @Composable
    get() = LocalContentMargins.current.half
