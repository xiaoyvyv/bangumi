package com.xiaoyv.bangumi.shared.ui.component.dialog.sheet

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.ui.theme.isMiuixUi
import com.xiaoyv.bangumi.shared.ui.view.BgmBottomSheetDefaults
import com.xiaoyv.bangumi.shared.ui.view.navigation.LocalFloatingContentBottomPadding
import com.xiaoyv.bangumi.shared.ui.view.navigation.LocalMainBottomBarOverlap
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.window.WindowBottomSheet
import top.yukonga.miuix.kmp.theme.LocalContentColor as MiuixLocalContentColor


@Composable
fun BottomSheetDialog(
    modifier: Modifier = Modifier,
    state: BottomSheetDialogState = rememberSheetDialogState(),
    sheetMaxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,
    shape: Shape = BottomSheetDefaults.ExpandedShape,
    containerColor: Color = BgmBottomSheetDefaults.containerColor(),
    contentColor: Color = if (isMiuixUi()) MiuixTheme.colorScheme.onSurface else contentColorFor(containerColor),
    tonalElevation: Dp = 0.dp,
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    sheetGesturesEnabled: Boolean = true,
    dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    contentWindowInsets: @Composable () -> WindowInsets = { BottomSheetDefaults.modalWindowInsets },
    content: @Composable ColumnScope.() -> Unit,
) {
    if (isMiuixUi() && state.dragCancelable && sheetGesturesEnabled && state.skipPartiallyExpanded) {
        WindowBottomSheet(
            show = state.showing,
            modifier = modifier,
            backgroundColor = containerColor,
            sheetMaxWidth = sheetMaxWidth,
            insideMargin = DpSize(0.dp, 0.dp),
            allowDismiss = state.cancelable,
            onDismissRequest = { if (state.cancelable) state.dismiss() },
        ) {
            CompositionLocalProvider(
                LocalContentColor provides contentColor,
                MiuixLocalContentColor provides contentColor,
                LocalFloatingContentBottomPadding provides 0.dp,
                LocalMainBottomBarOverlap provides 0.dp,
            ) {
                Column(modifier = Modifier.fillMaxWidth(), content = content)
            }
        }
    } else if (state.showing) {
        ModalBottomSheet(
            modifier = modifier.statusBarsPadding(),
            onDismissRequest = { state.dismiss() },
            sheetState = state.sheetState,
            sheetGesturesEnabled = sheetGesturesEnabled,
            sheetMaxWidth = sheetMaxWidth,
            shape = shape,
            containerColor = containerColor,
            contentColor = contentColor,
            tonalElevation = tonalElevation,
            scrimColor = scrimColor,
            dragHandle = dragHandle,
            contentWindowInsets = contentWindowInsets,
            properties = ModalBottomSheetProperties(state.cancelable),
        ) {
            CompositionLocalProvider(
                LocalFloatingContentBottomPadding provides 0.dp,
                LocalMainBottomBarOverlap provides 0.dp,
            ) {
                content()
            }
        }
    }
}
