package com.xiaoyv.bangumi.features.timeline.detail.business

import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel

/**
 * [TimelineDetailViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class TimelineDetailViewModel : BaseViewModel<TimelineDetailState, TimelineDetailSideEffect, TimelineDetailEvent.Action>() {

    override fun createInitialState() = TimelineDetailState()

    override fun onEvent(event: TimelineDetailEvent.Action) {
        when (event) {
            is TimelineDetailEvent.Action.OnRefresh -> refresh(loading = event.loading)
        }
    }

}