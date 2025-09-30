package com.xiaoyv.bangumi.shared.ui.component.dialog.alert

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_cancel
import com.xiaoyv.bangumi.core_resource.resources.global_confirm
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

/**
 * [BgmAlertDialog]
 *
 * @author why
 * @since 2025/1/14
 */
@Composable
fun BgmAlertDialog(
    confirm: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    state: AlertDialogState = rememberAlertDialogState(),
    cancel: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
) {
    val showing by state.showing.collectAsStateWithLifecycle()
    if (showing) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = { state.dismiss() },
            confirmButton = confirm,
            dismissButton = cancel,
            icon = icon,
            title = title,
            text = text,
            properties = state.properties
        )
    }
}


@Composable
fun BgmAlertDialog(
    text: String,
    confirm: String = stringResource(Res.string.global_confirm),
    cancel: String? = stringResource(Res.string.global_cancel),
    title: String? = null,
    modifier: Modifier = Modifier,
    state: AlertDialogState = rememberAlertDialogState(),
    icon: @Composable (() -> Unit)? = null,
    onConfirm: () -> Unit = { },
    onCancel: () -> Unit = { },
) {
    val scope = rememberCoroutineScope()
    val showing by state.showing.collectAsStateWithLifecycle()
    if (showing) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = { state.dismiss() },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.dismiss()
                        scope.launch {
                            delay(200)
                            onConfirm()
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
            title = title?.let { { Text(it) } },
            text = { Text(text) },
            properties = state.properties
        )
    }
}

