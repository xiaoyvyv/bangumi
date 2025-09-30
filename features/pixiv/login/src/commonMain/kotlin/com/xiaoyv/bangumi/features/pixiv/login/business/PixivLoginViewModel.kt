package com.xiaoyv.bangumi.features.pixiv.login.business

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel

/**
 * [PixivLoginViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class PixivLoginViewModel(savedStateHandle: SavedStateHandle) :
    BaseViewModel<PixivLoginState, PixivLoginSideEffect, PixivLoginEvent.Action>(savedStateHandle) {

    override fun initSate(onCreate: Boolean) = PixivLoginState()

    override fun onEvent(event: PixivLoginEvent.Action) {
        when (event) {
            is PixivLoginEvent.Action.OnRefresh -> refresh(loading = event.loading)
        }
    }

}