package com.xiaoyv.bangumi.features.timeline.detail.business

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [TimelineDetailViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class TimelineDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val args: Screen.TimelineDetail,
) : BaseViewModel<TimelineDetailState, TimelineDetailSideEffect, TimelineDetailEvent.Action>(savedStateHandle) {

    override fun initSate(onCreate: Boolean) = TimelineDetailState()

    override fun onEvent(event: TimelineDetailEvent.Action) {
        when (event) {
            is TimelineDetailEvent.Action.OnRefresh -> refresh(loading = event.loading)
        }
    }

}