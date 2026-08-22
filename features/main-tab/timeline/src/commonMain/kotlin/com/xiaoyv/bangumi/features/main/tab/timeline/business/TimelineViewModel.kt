package com.xiaoyv.bangumi.features.main.tab.timeline.business

import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel

/**
 * [TimelineViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class TimelineViewModel : BaseViewModel<TimelineState, TimelineSideEffect, TimelineEvent.Action>() {

    override fun createInitialState() = TimelineState()

    override fun onEvent(event: TimelineEvent.Action) {
        when (event) {
            is TimelineEvent.Action.OnRefresh -> refresh(loading = event.loading)
        }
    }
}