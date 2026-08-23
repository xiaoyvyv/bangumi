package com.xiaoyv.bangumi.features.pixiv.user.edit.business

import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
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
        }
    }
}
