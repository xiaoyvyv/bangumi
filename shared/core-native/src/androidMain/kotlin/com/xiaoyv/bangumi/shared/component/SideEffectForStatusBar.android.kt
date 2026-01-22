package com.xiaoyv.bangumi.shared.component

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsControllerCompat

/**
 * [SideEffectForStatusBar]
 *
 * @author why
 * @since 2025/1/17
 */
@Composable
actual fun SideEffectForStatusBar(darkTheme: Boolean) {
    val context = LocalContext.current
    val window = (context as? Activity)?.window

    window?.let {
        val controller = remember(window) {
            WindowInsetsControllerCompat(window, window.decorView)
        }
        SideEffect {
            controller.isAppearanceLightStatusBars = !darkTheme
        }
    }
}