package com.xiaoyv.bangumi.shared.ui.kts

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass


val isExtraSmallScreen: Boolean
    @Composable
    get() = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width <= 400.dp.roundToPx() }

val isSmallScreen: Boolean
    @Composable
    get() = with(currentWindowAdaptiveInfo()) { windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT } ||
            with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width < 640.dp.roundToPx() }

val isMediumScreen: Boolean
    @Composable
    get() = with(currentWindowAdaptiveInfo()) { windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM } ||
            with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width in (640.dp.roundToPx() until 1080.dp.roundToPx()) }

val isExpandScreen: Boolean
    @Composable
    get() = with(currentWindowAdaptiveInfo()) { windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED } ||
            with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width >= 1080.dp.roundToPx() }

val isWideScreen: Boolean
    @Composable
    get() = isMediumScreen || isExpandScreen