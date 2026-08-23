package com.xiaoyv.bangumi.features.pixiv.user.edit.business

import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import org.orbitmvi.orbit.syntax.Syntax

/**
 * [PixivUserEditViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class PixivUserEditViewModel :
    BaseViewModel<PixivUserEditState, PixivUserEditSideEffect, PixivUserEditEvent.Action>() {

    override fun createInitialState() = PixivUserEditState()

    override suspend fun Syntax<UiState<PixivUserEditState>, UiSideEffect<PixivUserEditSideEffect>>.refreshSync() {

    }

    override fun onEvent(event: PixivUserEditEvent.Action) {
        when (event) {
            is PixivUserEditEvent.Action.OnRefresh -> refresh(loading = event.loading)
            is PixivUserEditEvent.Action.OnDisplayNameChanged -> onDisplayNameChanged(event.value)
            is PixivUserEditEvent.Action.OnIntroductionChanged -> onIntroductionChanged(event.value)
            is PixivUserEditEvent.Action.OnWebsiteChanged -> onWebsiteChanged(event.value)
        }
    }

    private fun onDisplayNameChanged(value: String) = intent {
        reduceData { state.copy(displayName = value) }
    }

    private fun onIntroductionChanged(value: String) = intent {
        reduceData { state.copy(introduction = value) }
    }

    private fun onWebsiteChanged(value: String) = intent {
        reduceData { state.copy(website = value) }
    }
}
