@file:OptIn(ExperimentalComposeUiApi::class)

package com.xiaoyv.bangumi.shared.ui.component.dialog.comment

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
actual fun TransparentDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties,
    content: @Composable (() -> Unit),
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = properties.dismissOnBackPress,
            dismissOnClickOutside = properties.dismissOnClickOutside,
            usePlatformDefaultWidth = properties.usePlatformDefaultWidth,
            scrimColor = Color.Transparent,
            usePlatformInsets = false
        ),
        content = content
    )
}