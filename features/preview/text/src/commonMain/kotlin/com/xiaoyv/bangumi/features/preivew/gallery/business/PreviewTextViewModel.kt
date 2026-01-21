package com.xiaoyv.bangumi.features.preivew.gallery.business

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.mvi.BaseSyntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.types.LoadingState
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.core.utils.onCompletion
import com.xiaoyv.bangumi.shared.core.utils.parseAsHtml
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
    override fun initBaseState(): BaseState<PreviewTextState> = BaseState.Loading()

    override fun initSate(onCreate: Boolean) = PreviewTextState()

    override fun onEvent(event: PreviewTextEvent.Action) {
        when (event) {
            is PreviewTextEvent.Action.OnRefresh -> refresh(loading = event.loading)
            is PreviewTextEvent.Action.OnToggleTranslate -> onToggleTranslate()
        }
    }

    private fun onToggleTranslate() = action {
        if (stateRaw.showOrigin) {
            if (stateRaw.translateText.text.isBlank()) {
                reduceContent { state.copy(loading = LoadingState.Loading) }
                choreRepository.translate(text = args.text, true)
                    .onCompletion { reduceContent { state.copy(loading = LoadingState.NotLoading) } }
                    .onFailure { postToast { it.errMsg } }
                    .onSuccess {
                        reduceContent {
                            state.copy(translateText = it.parseAsHtml(), showOrigin = false)
                        }
                    }
            } else {
                reduceContent { state.copy(showOrigin = false) }
            }
        } else {
            reduceContent { state.copy(showOrigin = true) }
        }
    }

    override suspend fun BaseSyntax<PreviewTextState, PreviewTextSideEffect>.refreshSync() {
        reduceContent { state.copy(originText = args.text.parseAsHtml()) }
    }
}