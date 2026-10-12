package com.xiaoyv.bangumi.shared.ui.component.dialog.alert

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.fillMaxWidth
import com.xiaoyv.bangumi.shared.ui.component.scroll.rememberScrollUpScrollState as rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_cancel
import com.xiaoyv.bangumi.core_resource.resources.global_confirm
import com.xiaoyv.bangumi.shared.ui.theme.isMiuixUi
import com.xiaoyv.bangumi.shared.ui.view.BgmAlertSurface
import com.xiaoyv.bangumi.shared.ui.view.BgmConfirmationButton
import top.yukonga.miuix.kmp.basic.TextButton as MiuixTextButton
import top.yukonga.miuix.kmp.basic.ButtonDefaults as MiuixButtonDefaults
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.milliseconds

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
    BgmAlertSurface(
        show = state.showing,
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


@Composable
fun BgmAlertDialog(
    text: String,
    confirm: String = stringResource(Res.string.global_confirm),
    cancel: String? = stringResource(Res.string.global_cancel),
    title: String? = null,
    modifier: Modifier = Modifier,
    state: AlertDialogState = rememberAlertDialogState(),
    icon: @Composable (() -> Unit)? = null,
    isDestructive: Boolean = false,
    onConfirm: () -> Unit = { },
    onCancel: () -> Unit = { },
) {
    val scope = rememberCoroutineScope()
    BgmAlertSurface(
        show = state.showing,
        modifier = modifier,
        onDismissRequest = { state.dismiss() },
        confirmButton = {
            val onClick: () -> Unit = {
                state.dismiss()
                scope.launch {
                    delay(200.milliseconds)
                    onConfirm()
                }
            }
            BgmConfirmationButton(
                text = confirm,
                isDestructive = isDestructive,
                onClick = onClick,
            )
        },
        dismissButton = cancel?.let {
            {
                val onClick: () -> Unit = {
                    state.dismiss()
                    scope.launch {
                        delay(200.milliseconds)
                        onCancel()
                    }
                }
                if (isMiuixUi()) {
                    MiuixTextButton(text = cancel, modifier = Modifier.fillMaxWidth(), onClick = onClick)
                } else {
                    TextButton(onClick = onClick, content = { Text(cancel) })
                }
            }
        },
        icon = icon,
        title = title?.let { { Text(it) } },
        text = {
            Text(
                modifier = Modifier
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState()),
                text = text
            )
        },
        properties = state.properties
    )
}
