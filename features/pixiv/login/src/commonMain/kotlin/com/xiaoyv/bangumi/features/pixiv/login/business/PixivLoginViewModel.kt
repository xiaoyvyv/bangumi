package com.xiaoyv.bangumi.features.pixiv.login.business

import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel

/**
 * [PixivLoginViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class PixivLoginViewModel :
    BaseViewModel<PixivLoginState, PixivLoginSideEffect, PixivLoginEvent.Action>() {

    override fun createInitialState() = PixivLoginState()

    override fun onEvent(event: PixivLoginEvent.Action) {
        when (event) {
            is PixivLoginEvent.Action.OnRefresh -> refresh(loading = event.loading)
        }
    }

}