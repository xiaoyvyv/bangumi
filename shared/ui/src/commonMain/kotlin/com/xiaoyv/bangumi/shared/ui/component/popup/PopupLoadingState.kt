package com.xiaoyv.bangumi.shared.ui.component.popup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * [PopupLoadingState]
 *
 * @author why
 * @since 2025/1/14
 */
class PopupLoadingState {
    private val _show = MutableStateFlow(false)
    val isShowingFlow = _show.asStateFlow()

    fun dismiss() = _show.update { false }
    fun show() = _show.update { true }
}

@Composable
fun rememberPopupLoadingState(): PopupLoadingState {
    return remember { PopupLoadingState() }
}


val LocalPopupLoadingState = staticCompositionLocalOf<PopupLoadingState> {
    throw IllegalStateException("Please provide PopupTipState for LocalPopupTipState!")
}