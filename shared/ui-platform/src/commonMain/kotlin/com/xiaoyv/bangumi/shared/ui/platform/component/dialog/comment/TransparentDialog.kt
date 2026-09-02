package com.xiaoyv.bangumi.shared.ui.platform.component.dialog.comment

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties

val commentDialogProperties = DialogProperties(
    dismissOnBackPress = true,
    dismissOnClickOutside = true,
    usePlatformDefaultWidth = false,
)

@Composable
expect fun TransparentDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = commentDialogProperties,
    content: @Composable () -> Unit,
)
