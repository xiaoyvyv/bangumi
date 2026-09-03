package com.xiaoyv.bangumi.shared.libnative.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberVideoScreenController(): VideoScreenController = remember {
    object : VideoScreenController {
        override fun enterPortrait() = Unit

        override fun enterFullscreen() = Unit
    }
}
