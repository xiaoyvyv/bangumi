package com.xiaoyv.bangumi.shared.ui.platform.component.back

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.awt.LocalAwtWindow
import java.awt.KeyEventDispatcher
import java.awt.KeyboardFocusManager
import java.awt.event.KeyEvent

@Composable
actual fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) {
    val window = LocalAwtWindow.current
    val currentOnBack by rememberUpdatedState(onBack)

    DisposableEffect(enabled, window) {
        if (!enabled) return@DisposableEffect onDispose {}

        val manager = KeyboardFocusManager.getCurrentKeyboardFocusManager()
        val dispatcher = KeyEventDispatcher { event ->
            val isWindowActive = window == null || manager.activeWindow == window || window.isFocused
            if (isWindowActive && event.keyCode == KeyEvent.VK_ESCAPE) {
                if (event.id == KeyEvent.KEY_PRESSED) {
                    currentOnBack()
                    return@KeyEventDispatcher true
                }
                if (event.id == KeyEvent.KEY_RELEASED) {
                    return@KeyEventDispatcher true
                }
            }
            false
        }

        manager.addKeyEventDispatcher(dispatcher)

        onDispose {
            manager.removeKeyEventDispatcher(dispatcher)
        }
    }
}
