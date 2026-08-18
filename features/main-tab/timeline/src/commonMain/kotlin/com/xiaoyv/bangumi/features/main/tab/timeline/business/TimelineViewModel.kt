package com.xiaoyv.bangumi.features.main.tab.timeline.business

import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import kotlinx.collections.immutable.persistentListOf

/**
 * [TimelineViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class TimelineViewModel(
    private val userManager: UserManager,
) : BaseViewModel<TimelineState, TimelineSideEffect, TimelineEvent.Action>() {

    override fun createInitialState() = TimelineState(
        actions = persistentListOf(),
        username = userManager.userInfo.username
    )

    override fun onEvent(event: TimelineEvent.Action) {
        when (event) {
            is TimelineEvent.Action.OnRefresh -> refresh(loading = event.loading)
        }
    }
}