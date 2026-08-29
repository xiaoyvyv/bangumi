package com.xiaoyv.bangumi.shared.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass

@Immutable
internal data class ContentMargins(
    val full: Dp,
    val half: Dp,
    val coverWidth: Dp
)

internal val LocalContentMargins = compositionLocalOf {
    ContentMargins(
        full = 16.dp,
        half = 8.dp,
        coverWidth = 85.dp,
    )
}

@Composable
internal fun rememberContentMargins(): ContentMargins {
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass

    return remember(windowSizeClass) {
        when {
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> ContentMargins(full = 24.dp, half = 12.dp, 110.dp)
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> ContentMargins(full = 20.dp, half = 10.dp, 100.dp)
            else -> ContentMargins(full = 16.dp, half = 8.dp, 90.dp)
        }
    }
}

val ContentMargin: Dp
    @Composable @ReadOnlyComposable
    get() = LocalContentMargins.current.full

val ContentMarginHalf: Dp
    @Composable @ReadOnlyComposable
    get() = LocalContentMargins.current.half

val ContentMarginGrid: Dp
    @Composable @ReadOnlyComposable
    get() = LocalContentMargins.current.half + LocalContentMargins.current.half / 2

val ContentCoverWidth: Dp
    @Composable @ReadOnlyComposable
    get() = LocalContentMargins.current.coverWidth

val MinTabWidth: Dp
    @Composable
    get() {
        val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
        return when {
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> 110.dp
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> 90.dp
            else -> 72.dp
        }
    }

val ThinBorderStroke: BorderStroke
    @Composable @ReadOnlyComposable
    get() = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)

val ThinBorderStrokeVariant: BorderStroke
    @Composable @ReadOnlyComposable
    get() = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)

val BorderStroke: BorderStroke
    @Composable @ReadOnlyComposable
    get() = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)

val BorderStrokeVariant: BorderStroke
    @Composable @ReadOnlyComposable
    get() = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)