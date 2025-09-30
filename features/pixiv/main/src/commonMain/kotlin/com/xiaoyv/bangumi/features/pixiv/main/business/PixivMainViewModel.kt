package com.xiaoyv.bangumi.features.pixiv.main.business

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel

/**
 * [PixivMainViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class PixivMainViewModel(savedStateHandle: SavedStateHandle) :
    BaseViewModel<PixivMainState, PixivMainSideEffect, PixivMainEvent.Action>(savedStateHandle) {

    override fun initSate(onCreate: Boolean) = PixivMainState()

    override fun onEvent(event: PixivMainEvent.Action) {
        when (event) {
            is PixivMainEvent.Action.OnRefresh -> refresh(loading = event.loading)
        }
    }

}