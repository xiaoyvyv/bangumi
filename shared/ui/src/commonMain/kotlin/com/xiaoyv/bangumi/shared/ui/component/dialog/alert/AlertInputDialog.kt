package com.xiaoyv.bangumi.shared.ui.component.dialog.alert

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_cancel
import com.xiaoyv.bangumi.core_resource.resources.global_confirm
import com.xiaoyv.bangumi.shared.core.utils.digit
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun BgmAlertInputDialog(
    modifier: Modifier = Modifier,
    confirm: String = stringResource(Res.string.global_confirm),
    cancel: String? = stringResource(Res.string.global_cancel),
    state: AlertInputDialogState = rememberAlertInputDialogState(),
    icon: @Composable (() -> Unit)? = null,
    onConfirm: (AlertInputDialogState.Data) -> Unit = { },
    onCancel: () -> Unit = { },
) {
    val scope = rememberCoroutineScope()
    val showing by state.showing.collectAsStateWithLifecycle()
    if (showing) {
        val focusRequester = remember { FocusRequester() }
        val data by state.data.collectAsStateWithLifecycle()
        var text by remember(data) {
            mutableStateOf(TextFieldValue(data.value, TextRange(data.value.length)))
        }

        AlertDialog(
            modifier = modifier.padding(WindowInsets.ime.asPaddingValues()),
            onDismissRequest = { state.dismiss() },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.dismiss()
                        scope.launch {
                            delay(200)
                            onConfirm(data.copy(value = text.text.trim()))
                        }
                    },
                    content = { Text(confirm) }
                )
            },
            dismissButton = cancel?.let {
                {
                    TextButton(
                        onClick = {
                            state.dismiss()
                            scope.launch {
                                delay(200)
                                onCancel()
                            }
                        },
                        content = { Text(cancel) }
                    )
                }
            },
            icon = icon,
            title = data.title?.let { { Text(it) } },
            text = {
                OutlinedTextField(
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .fillMaxWidth(),
                    value = text,
                    keyboardOptions = KeyboardOptions(keyboardType = if (data.onlyNumber) KeyboardType.Number else KeyboardType.Text),
                    singleLine = data.singleLine,
                    minLines = data.minLines,
                    maxLines = data.maxLines,
                    onValueChange = { text = if (data.onlyNumber) it.digit(text) else it }
                )

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            },
            properties = state.properties
        )
    }
}