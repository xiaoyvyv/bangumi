package com.xiaoyv.bangumi.shared.ui.component.dialog.sheet

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity

@Composable
fun rememberSheetDialogState(
    cancelable: Boolean = true,
    dragCancelable: Boolean = true,
    skipPartiallyExpanded: Boolean = true,
): BottomSheetDialogState {
    val density = LocalDensity.current
    val ime = WindowInsets.ime
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded,
        confirmValueChange = { if (dragCancelable && ime.getBottom(density) == 0) true else it != SheetValue.Hidden }
    )
    return remember { BottomSheetDialogState(sheetState, cancelable) }
}

@Stable
class BottomSheetDialogState(val sheetState: SheetState, val cancelable: Boolean) {
    var showing by mutableStateOf(false)
        private set

    fun show() {
        showing = true
    }

    fun dismiss() {
        showing = false
    }

    suspend fun dismissNow() {
        sheetState.hide()
        showing = false
    }
}