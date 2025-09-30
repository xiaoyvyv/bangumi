package com.xiaoyv.bangumi.shared.ui.component.dialog.comment

import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider

@Composable
actual fun TransparentDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = properties.dismissOnBackPress,
            dismissOnClickOutside = properties.dismissOnClickOutside,
            securePolicy = properties.securePolicy,
            usePlatformDefaultWidth = properties.usePlatformDefaultWidth,
            decorFitsSystemWindows = false
        ),
        content = {
            dialogWindow()?.let { window ->
                SideEffect {
                    window.setDimAmount(0f)
                }
            }

            content()
        }
    )
}

@Composable
@ReadOnlyComposable
fun dialogWindow(): Window? {
    return (LocalView.current.parent as? DialogWindowProvider)?.window
}