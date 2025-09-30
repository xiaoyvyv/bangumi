package com.xiaoyv.bangumi.shared.ui.component.dialog.alert

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.window.DialogProperties
import com.xiaoyv.bangumi.shared.core.utils.mutableStateFlowOf
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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
    private val _showing = mutableStateFlowOf(false)
    internal val showing = _showing.asStateFlow()

    fun show() = _showing.update { true }
    fun dismiss() = _showing.update { false }

    companion object {
        fun Saver(properties: DialogProperties): Saver<AlertDialogState, *> = Saver(
            save = { listOf(it.showing.value) },
            restore = {
                AlertDialogState(properties = properties).apply {
                    _showing.value = it.first()
                }
            }
        )
    }
}
