package com.xiaoyv.bangumi.features.main.tab.topic.business

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_all
import com.xiaoyv.bangumi.core_resource.resources.global_episode
import com.xiaoyv.bangumi.core_resource.resources.global_group
import com.xiaoyv.bangumi.core_resource.resources.global_mono
import com.xiaoyv.bangumi.core_resource.resources.global_subject
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.types.RakuenTab
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf

/**
 * [TopicViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class TopicViewModel(savedStateHandle: SavedStateHandle) :
    BaseViewModel<TopicState, TopicSideEffect, TopicEvent.Action>(savedStateHandle) {

    override fun initSate(onCreate: Boolean) = TopicState(
        tabs = persistentListOf(
            ComposeTextTab(RakuenTab.ALL, Res.string.global_all),
            ComposeTextTab(RakuenTab.GROUP, Res.string.global_group),
//            ComposeTextTab(RakuenType.TYPE_MY_GROUP, Res.string.global_group),
            ComposeTextTab(RakuenTab.SUBJECT, Res.string.global_subject),
            ComposeTextTab(RakuenTab.EP, Res.string.global_episode),
            ComposeTextTab(RakuenTab.MONO, Res.string.global_mono),
        ),
        actions = persistentListOf(
//            ComposeTextTab(TimelineTarget.WHOLE, Res.string.timeline_title),
//            ComposeTextTab(TimelineTarget.FRIEND, Res.string.timeline_friend_title),
//            ComposeTextTab(TimelineTarget.ME, Res.string.timeline_mine_title),
        )
    )

    override fun onEvent(event: TopicEvent.Action) {
        when (event) {
            is TopicEvent.Action.OnRefresh -> refresh(loading = event.loading)
            is TopicEvent.Action.OnChangeType -> onChangeType(event.type)
        }
    }

    private fun onChangeType(type: String) = action {
        reduceContent { state.copy(type = type) }
    }
}