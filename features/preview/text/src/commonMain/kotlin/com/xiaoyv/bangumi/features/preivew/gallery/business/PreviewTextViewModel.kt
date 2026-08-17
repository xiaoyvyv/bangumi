package com.xiaoyv.bangumi.features.preivew.gallery.business

import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.PageStatus
import org.orbitmvi.orbit.syntax.Syntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.types.LoadingState
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.core.utils.onCompletion
import com.xiaoyv.bangumi.shared.data.repository.ChoreRepository
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [PreviewTextViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class PreviewTextViewModel(
    savedStateHandle: SavedStateHandle,
    private val args: Screen.PreviewText,
    private val choreRepository: ChoreRepository,
) : BaseViewModel<PreviewTextState, PreviewTextSideEffect, PreviewTextEvent.Action>(savedStateHandle) {
    override fun initBaseState(): UiState<PreviewTextState> = UiState(data = createInitialState(), status = PageStatus.Loading)

    override fun createInitialState() = PreviewTextState()

    override fun onEvent(event: PreviewTextEvent.Action) {
        when (event) {
            is PreviewTextEvent.Action.OnRefresh -> refresh(loading = event.loading)
            is PreviewTextEvent.Action.OnToggleTranslate -> onToggleTranslate()
        }
    }

    private fun onToggleTranslate() = intent {
        if (state.data.showOrigin) {
            if (state.data.translateText.isBlank()) {
                reduceData { state.copy(loading = LoadingState.Loading) }
                choreRepository.translate(text = args.text, true)
                    .onCompletion { reduceData { state.copy(loading = LoadingState.NotLoading) } }
                    .onFailure { postToast { it.errMsg } }
                    .onSuccess {
                        reduceData {
                            state.copy(translateText = it, showOrigin = false)
                        }
                    }
            } else {
                reduceData { state.copy(showOrigin = false) }
            }
        } else {
            reduceData { state.copy(showOrigin = true) }
        }
    }

    override suspend fun Syntax<UiState<PreviewTextState>, UiSideEffect<PreviewTextSideEffect>>.refreshSync() {
        reduceData { state.copy(originText = args.text) }
    }
}