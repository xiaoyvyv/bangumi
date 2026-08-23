package com.xiaoyv.bangumi.features.pixiv.user.setting.business

import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
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
            is PixivUserSettingEvent.Action.OnShowR18Changed -> onShowR18Changed(event.value)
            is PixivUserSettingEvent.Action.OnShowAiWorksChanged -> onShowAiWorksChanged(event.value)
            is PixivUserSettingEvent.Action.OnAutoplayUgoiraChanged -> onAutoplayUgoiraChanged(event.value)
        }
    }

    private fun onShowR18Changed(value: Boolean) = intent {
        reduceData { state.copy(showR18 = value) }
    }

    private fun onShowAiWorksChanged(value: Boolean) = intent {
        reduceData { state.copy(showAiWorks = value) }
    }

    private fun onAutoplayUgoiraChanged(value: Boolean) = intent {
        reduceData { state.copy(autoplayUgoira = value) }
    }
}
