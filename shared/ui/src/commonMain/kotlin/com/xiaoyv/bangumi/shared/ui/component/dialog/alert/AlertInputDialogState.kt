@file:Suppress("PropertyName")

package com.xiaoyv.bangumi.shared.ui.component.dialog.alert

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.window.DialogProperties
import com.xiaoyv.bangumi.shared.core.utils.mutableStateFlowOf
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    val _showing = mutableStateFlowOf(false)
    internal val showing = _showing.asStateFlow()

    val _data = mutableStateFlowOf(Data())
    internal val data = _data.asStateFlow()

    inline fun show(block: (Data) -> Data = { it }) {
        _data.update { block(_data.value) }
        _showing.update { true }
    }

    fun dismiss() = _showing.update { false }

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
            save = { listOf(it.showing.value, json.encodeToString(it.data.value)) },
            restore = {
                AlertInputDialogState(properties = properties).apply {
                    _showing.value = it.first() as Boolean
                    _data.value = json.decodeFromString((it.last() as String))
                }
            }
        )
    }
}
