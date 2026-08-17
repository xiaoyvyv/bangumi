package com.xiaoyv.bangumi.shared.ui.component.dialog.alert

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.DialogProperties

@Composable
fun rememberAlertDialogState(
    properties: DialogProperties = DialogProperties(),
) = rememberSaveable(saver = AlertDialogState.Saver(properties)) {
    AlertDialogState(properties)
}

/**
 * [AlertDialogState]
 *
 * @author why
 * @since 2025/1/14
 */
@Stable
class AlertDialogState(val properties: DialogProperties) {
    internal var showing by mutableStateOf(false)
        private set

    fun show() {
        showing = true
    }

    fun dismiss() {
        showing = false
    }

    companion object {
        fun Saver(properties: DialogProperties): Saver<AlertDialogState, Boolean> = Saver(
            save = { it.showing },
            restore = {
                AlertDialogState(properties = properties).apply {
                    showing = it
                }
            }
        )
    }
}
