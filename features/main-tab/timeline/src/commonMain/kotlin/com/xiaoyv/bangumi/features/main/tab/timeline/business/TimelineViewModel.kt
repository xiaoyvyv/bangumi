package com.xiaoyv.bangumi.features.main.tab.timeline.business

import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_all
import com.xiaoyv.bangumi.core_resource.resources.global_friend
import com.xiaoyv.bangumi.core_resource.resources.global_self
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.types.TimelineTab
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf

/**
 * [TimelineViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class TimelineViewModel : BaseViewModel<TimelineState, TimelineSideEffect, TimelineEvent.Action>() {

    override fun createInitialState() = TimelineState(
        actions = persistentListOf(
            ComposeTextTab(TimelineTab.TIMELINE_ANYONE, Res.string.global_all),
            ComposeTextTab(TimelineTab.TIMELINE_FRIEND, Res.string.global_friend),
            ComposeTextTab(TimelineTab.TIMELINE_SELF, Res.string.global_self),
        )
    )

    override fun onEvent(event: TimelineEvent.Action) {
        when (event) {
            is TimelineEvent.Action.OnRefresh -> refresh(loading = event.loading)
            is TimelineEvent.Action.OnChangeTimeline -> onChangeTimeline(event.mode)
        }
    }

    private fun onChangeTimeline(mode: Int) = intent {
        reduceData { state.copy(selectedMode = mode) }
    }
}