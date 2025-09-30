package com.xiaoyv.bangumi.shared.ui.component.popup

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Stable
class PopupTipState(
    private val scope: CoroutineScope,
    val state: SnackbarHostState,
) {
    fun dismiss() {
        state.currentSnackbarData?.dismiss()
    }

    fun showToast(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        duration: SnackbarDuration = if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite,
    ) {
        scope.launch {
            state.currentSnackbarData?.dismiss()
            state.showSnackbar(message, actionLabel, withDismissAction, duration)
        }
    }
}

@Composable
fun rememberPopupTipState(): PopupTipState {
    val hostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    return remember { PopupTipState(scope, hostState) }
}

val LocalPopupTipState = staticCompositionLocalOf<PopupTipState> {
    throw IllegalStateException()
}