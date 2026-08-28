@file:Suppress("PropertyName")

package com.xiaoyv.bangumi.shared.ui.component.dialog.alert

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.DialogProperties

@Composable
fun rememberAlertInputDialogState(
    properties: DialogProperties = DialogProperties(),
) = rememberSaveable(saver = AlertInputDialogState.Saver(properties)) {
    AlertInputDialogState(properties)
}

/**
 * [AlertInputDialogState]
 *
 * @author why
 * @since 2025/1/14
 */
@Stable
class AlertInputDialogState(val properties: DialogProperties) {
    @PublishedApi
    internal var showing by mutableStateOf(false)

    @PublishedApi
    internal var data by mutableStateOf(Data())

    inline fun show(block: (Data) -> Data = { it }) {
        data = block(data)
        showing = true
    }

    fun dismiss() {
        showing = false
    }

    @Immutable
    data class Data(
        val value: String = "",
        val title: String? = null,
        val singleLine: Boolean = true,
        val onlyNumber: Boolean = false,
        val minLines: Int = 1,
        val maxLines: Int = 8,

        val extraInt: Int = 0,
        val extraString: String = "",
    )

    companion object {

        fun Saver(properties: DialogProperties): Saver<AlertInputDialogState, *> = Saver(
            save = { state ->
                val d = state.data
                listOf(
                    state.showing,
                    d.value,
                    d.title,
                    d.singleLine,
                    d.onlyNumber,
                    d.minLines,
                    d.maxLines,
                    d.extraInt,
                    d.extraString
                )
            },
            restore = { list ->
                AlertInputDialogState(properties = properties).apply {
                    showing = list[0] as Boolean
                    data = Data(
                        value = list[1] as String,
                        title = list[2] as? String,
                        singleLine = list[3] as Boolean,
                        onlyNumber = list[4] as Boolean,
                        minLines = list[5] as Int,
                        maxLines = list[6] as Int,
                        extraInt = list[7] as Int,
                        extraString = list[8] as String
                    )
                }
            }
        )
    }
}
