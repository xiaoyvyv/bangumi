package com.xiaoyv.bangumi.shared.ui.kts

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.Lifecycle
import com.xiaoyv.bangumi.shared.core.mvi.BaseSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.ui.component.popup.LocalPopupLoadingState
import com.xiaoyv.bangumi.shared.ui.component.popup.LocalPopupTipState
import com.xiaoyv.bangumi.shared.ui.component.popup.PopupLoadingState
import com.xiaoyv.bangumi.shared.ui.component.popup.PopupTipState
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.compose.collectSideEffect


@Composable
fun <STATE : Any, SIDE_EFFECT : Any> ContainerHost<BaseState<STATE>, BaseSideEffect<SIDE_EFFECT>>.collectBaseSideEffect(
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    onToastEffect: suspend (PopupTipState, BaseSideEffect.Toast<SIDE_EFFECT>) -> Unit = { state, effect ->
        state.showToast(effect.message)
    },
    onLoadingEffect: suspend (PopupLoadingState, BaseSideEffect.Loading<SIDE_EFFECT>) -> Unit = { state, effect ->
        if (effect.isLoading) state.show() else state.dismiss()
    },
    sideEffect: (suspend (sideEffect: SIDE_EFFECT) -> Unit),
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val popupTipState = LocalPopupTipState.current
    val loadingState = LocalPopupLoadingState.current

    collectSideEffect(lifecycleState) {
        keyboardController?.hide()
        when (it) {
            is BaseSideEffect.Loading -> onLoadingEffect(loadingState, it)
            is BaseSideEffect.Toast -> onToastEffect(popupTipState, it)
            is BaseSideEffect.Wrapped -> sideEffect(it.effect)
        }
    }
}