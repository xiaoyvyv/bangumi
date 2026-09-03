package com.xiaoyv.bangumi.shared.libnative.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.awt.LocalAwtWindow
import java.awt.Frame
import java.awt.GraphicsEnvironment
import java.awt.Window

@Composable
actual fun rememberVideoScreenController(): VideoScreenController {
    val window = LocalAwtWindow.current
    return remember(window) {
        JvmVideoScreenController(window)
    }
}

private class JvmVideoScreenController(
    private val window: Window?,
) : VideoScreenController {
    override fun enterPortrait() {
        val device = runCatching {
            GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice
        }.getOrNull()
        if (device != null && device.fullScreenWindow == window) {
            runCatching { device.fullScreenWindow = null }
        }
        val frame = window as? Frame
        if (frame != null && frame.extendedState == Frame.MAXIMIZED_BOTH) {
            runCatching { frame.setExtendedState(Frame.NORMAL) }
        }
    }

    override fun enterFullscreen() {
        val device = runCatching {
            GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice
        }.getOrNull()
        if (device != null && device.isFullScreenSupported && window != null) {
            runCatching { device.fullScreenWindow = window }
        } else {
            val frame = window as? Frame
            if (frame != null) {
                runCatching { frame.setExtendedState(Frame.MAXIMIZED_BOTH) }
            }
        }
    }
}
