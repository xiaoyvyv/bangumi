package com.xiaoyv.bangumi.features.timeline.add.business

import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel

/**
 * [TimelineAddViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class TimelineAddViewModel : BaseViewModel<TimelineAddState, TimelineAddSideEffect, TimelineAddEvent.Action>() {

    override fun createInitialState() = TimelineAddState()

    override fun onEvent(event: TimelineAddEvent.Action) {
        when (event) {
            is TimelineAddEvent.Action.OnRefresh -> refresh(loading = event.loading)
        }
    }

}