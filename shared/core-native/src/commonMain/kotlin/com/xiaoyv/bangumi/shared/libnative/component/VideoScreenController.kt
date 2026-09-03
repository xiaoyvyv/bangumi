package com.xiaoyv.bangumi.shared.libnative.component

import androidx.compose.runtime.Composable

/**
 * 视频画面的方向与全屏控制器。
 */
interface VideoScreenController {
    /** 切换至竖屏窗口。 */
    fun enterPortrait()

    /** 切换至横向沉浸式全屏窗口。 */
    fun enterFullscreen()
}

/**
 * 创建并记忆当前平台的视频画面控制器。
 */
@Composable
expect fun rememberVideoScreenController(): VideoScreenController
