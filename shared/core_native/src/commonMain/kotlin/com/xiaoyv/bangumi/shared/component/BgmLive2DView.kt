package com.xiaoyv.bangumi.shared.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

val LocalBgmLive2DState = staticCompositionLocalOf { BgmLive2DState() }

@Composable
fun rememberBgmLive2DState(): BgmLive2DState {
    return remember { BgmLive2DState() }
}

@Stable
expect class BgmLive2DState() {
    fun loadModel(modelName: String, modelDir: String)
}

@Composable
expect fun BgmLive2DView(
    modifier: Modifier,
    state: BgmLive2DState = rememberBgmLive2DState(),
)
