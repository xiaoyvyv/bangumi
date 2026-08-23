package com.xiaoyv.bangumi.features.pixiv.user.setting.business

import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import org.orbitmvi.orbit.syntax.Syntax

/**
 * [PixivUserSettingViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class PixivUserSettingViewModel :
    BaseViewModel<PixivUserSettingState, PixivUserSettingSideEffect, PixivUserSettingEvent.Action>() {

    override fun createInitialState() = PixivUserSettingState()

    override suspend fun Syntax<UiState<PixivUserSettingState>, UiSideEffect<PixivUserSettingSideEffect>>.refreshSync() {

    }

    override fun onEvent(event: PixivUserSettingEvent.Action) {
        when (event) {
            is PixivUserSettingEvent.Action.OnRefresh -> refresh(loading = event.loading)
        }
    }
}
