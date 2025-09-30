package com.xiaoyv.bangumi.features.timeline.detail.business

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.features.timeline.detail.TimelineDetailArguments
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel

/**
 * [TimelineDetailViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class TimelineDetailViewModel(savedStateHandle: SavedStateHandle) :
    BaseViewModel<TimelineDetailState, TimelineDetailSideEffect, TimelineDetailEvent.Action>(savedStateHandle) {
    private val args = TimelineDetailArguments(savedStateHandle)

    override fun initSate(onCreate: Boolean) = TimelineDetailState()

    override fun onEvent(event: TimelineDetailEvent.Action) {
        when (event) {
            is TimelineDetailEvent.Action.OnRefresh -> refresh(loading = event.loading)
        }
    }

}