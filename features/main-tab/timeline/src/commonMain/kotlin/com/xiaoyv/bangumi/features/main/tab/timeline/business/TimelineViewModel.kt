package com.xiaoyv.bangumi.features.main.tab.timeline.business

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.timeline_friend_title
import com.xiaoyv.bangumi.core_resource.resources.timeline_mine_title
import com.xiaoyv.bangumi.core_resource.resources.timeline_title
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.types.TimelineTarget
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf

/**
 * [TimelineViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class TimelineViewModel(
    savedStateHandle: SavedStateHandle,
    private val userManager: UserManager,
) : BaseViewModel<TimelineState, TimelineSideEffect, TimelineEvent.Action>(savedStateHandle) {

    override fun initSate(onCreate: Boolean) = TimelineState(
        actions = persistentListOf(
            ComposeTextTab(TimelineTarget.WHOLE, Res.string.timeline_title),
            ComposeTextTab(TimelineTarget.FRIEND, Res.string.timeline_friend_title),
            ComposeTextTab(TimelineTarget.USER, Res.string.timeline_mine_title),
        )
    )

    override fun onEvent(event: TimelineEvent.Action) {
        when (event) {
            is TimelineEvent.Action.OnRefresh -> refresh(loading = event.loading)
            is TimelineEvent.Action.OnChangeTarget -> onChangeTarget(event.target)
        }
    }

    private fun onChangeTarget(@TimelineTarget target: String) = action {
        if (target == TimelineTarget.USER) {
            reduceContent { state.copy(target = target, username = userManager.userInfo.username) }
        } else {
            reduceContent { state.copy(target = target, username = "") }
        }
    }
}