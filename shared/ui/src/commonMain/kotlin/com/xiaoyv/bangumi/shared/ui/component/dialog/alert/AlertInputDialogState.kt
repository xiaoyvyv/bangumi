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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.mp.KoinPlatform

@Composable
fun rememberAlertInputDialogState(
    properties: DialogProperties = DialogProperties(),
) = rememberSaveable(saver = AlertInputDialogState.Saver(properties)) {
    AlertInputDialogState(properties)
}

/**
 * [AlertDialogState]
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
    @Serializable
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
        private val json get() = KoinPlatform.getKoin().get<Json>()

        fun Saver(properties: DialogProperties): Saver<AlertInputDialogState, *> = Saver(
            save = { listOf(it.showing, json.encodeToString(it.data)) },
            restore = {
                AlertInputDialogState(properties = properties).apply {
                    showing = it.first() as Boolean
                    data = json.decodeFromString(it.last() as String)
                }
            }
        )
    }
}
