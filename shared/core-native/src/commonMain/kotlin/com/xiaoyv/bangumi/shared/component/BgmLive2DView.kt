package com.xiaoyv.bangumi.shared.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

typealias BgmLive2DState = Live2DState

@Composable
fun rememberBgmLive2DState(
    workDir: String = "",
    onHitAreaClick: ((hitArea: String) -> Unit)? = null
): BgmLive2DState = rememberLive2DState(workDir, onHitAreaClick)

@Composable
fun BgmLive2DView(
    modifier: Modifier = Modifier,
    state: BgmLive2DState = rememberBgmLive2DState(),
    onHitAreaClick: ((hitArea: String) -> Unit)? = null
) {
    Live2D(modifier = modifier, state = state, onHitAreaClick = onHitAreaClick)
}
