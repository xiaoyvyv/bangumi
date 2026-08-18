package com.xiaoyv.bangumi.features.pixiv.main.business

import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel

/**
 * [PixivMainViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class PixivMainViewModel :
    BaseViewModel<PixivMainState, PixivMainSideEffect, PixivMainEvent.Action>() {

    override fun createInitialState() = PixivMainState()

    override fun onEvent(event: PixivMainEvent.Action) {
        when (event) {
            is PixivMainEvent.Action.OnRefresh -> refresh(loading = event.loading)
        }
    }

}