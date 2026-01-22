package com.xiaoyv.bangumi.shared.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier

@Stable
actual class BgmLive2DState actual constructor() {
    actual fun loadModel(modelName: String, modelDir: String) {
    }
}

@Composable
actual fun BgmLive2DView(modifier: Modifier, state: BgmLive2DState) {
}
