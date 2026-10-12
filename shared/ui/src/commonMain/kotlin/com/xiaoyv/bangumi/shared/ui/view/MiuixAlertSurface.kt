package com.xiaoyv.bangumi.shared.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.isMiuixUi
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.window.WindowDialog

/**
 * 为已有弹窗插槽提供 Miuix 窗口外观，保留调用方的可取消约束。
 *
 * @param show 是否显示弹窗。
 * @param properties 原有平台窗口配置，特殊的取消约束交给平台弹窗处理。
 */
@Composable
fun BgmAlertSurface(
    show: Boolean,
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: (@Composable () -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    text: (@Composable () -> Unit)? = null,
    properties: DialogProperties = DialogProperties(),
) {
    if (isMiuixUi() && properties.dismissOnBackPress && properties.dismissOnClickOutside) {
        WindowDialog(
            show = show,
            modifier = modifier,
            onDismissRequest = onDismissRequest,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(ContentMargin)) {
                icon?.invoke()
                if (title != null) {
                    CompositionLocalProvider(
                        LocalContentColor provides MiuixTheme.colorScheme.onSurface,
                        LocalTextStyle provides MiuixTheme.textStyles.title4,
                        content = title,
                    )
                }
                if (text != null) {
                    CompositionLocalProvider(
                        LocalContentColor provides MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        LocalTextStyle provides MiuixTheme.textStyles.body1,
                        content = text,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(ContentMargin),
                ) {
                    if (dismissButton != null) {
                        Column(Modifier.weight(1f)) { dismissButton() }
                    }
                    Column(Modifier.weight(1f)) { confirmButton() }
                }
            }
        }
    } else if (show) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = confirmButton,
            modifier = modifier,
            dismissButton = dismissButton,
            icon = icon,
            title = title,
            text = text,
            properties = properties,
        )
    }
}
