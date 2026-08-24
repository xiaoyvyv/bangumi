package com.xiaoyv.bangumi.shared.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

typealias BgmLive2DState = Live2DState

@Composable
fun rememberBgmLive2DState(): BgmLive2DState = rememberLive2DState()

@Composable
fun BgmLive2DView(
    modifier: Modifier = Modifier,
    state: BgmLive2DState = rememberBgmLive2DState(),
) {
    Live2D(modifier = modifier, state = state)
}
