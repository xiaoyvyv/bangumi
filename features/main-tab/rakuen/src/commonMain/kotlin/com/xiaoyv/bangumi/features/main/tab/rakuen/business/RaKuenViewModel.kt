package com.xiaoyv.bangumi.features.main.tab.rakuen.business

import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_all
import com.xiaoyv.bangumi.core_resource.resources.global_character
import com.xiaoyv.bangumi.core_resource.resources.global_episode
import com.xiaoyv.bangumi.core_resource.resources.global_group
import com.xiaoyv.bangumi.core_resource.resources.global_my_group
import com.xiaoyv.bangumi.core_resource.resources.global_person
import com.xiaoyv.bangumi.core_resource.resources.global_subject
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.types.RakuenType
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf

/**
 * [RaKuenViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class RaKuenViewModel :
    BaseViewModel<RaKuenState, RaKuenSideEffect, RaKuenEvent.Action>() {

    override fun createInitialState() = RaKuenState(
        tabs = persistentListOf(
            ComposeTextTab(RakuenType.ALL, Res.string.global_all),
            ComposeTextTab(RakuenType.GROUP, Res.string.global_group),
            ComposeTextTab(RakuenType.MY_GROUP, Res.string.global_my_group),
            ComposeTextTab(RakuenType.SUBJECT, Res.string.global_subject),
            ComposeTextTab(RakuenType.EP, Res.string.global_episode),
            ComposeTextTab(RakuenType.CHARACTER, Res.string.global_character),
            ComposeTextTab(RakuenType.PERSON, Res.string.global_person),
        ),
        actions = persistentListOf(
//            ComposeTextTab(TimelineTarget.WHOLE, Res.string.timeline_title),
//            ComposeTextTab(TimelineTarget.FRIEND, Res.string.timeline_friend_title),
//            ComposeTextTab(TimelineTarget.ME, Res.string.timeline_mine_title),
        )
    )

    override fun onEvent(event: RaKuenEvent.Action) {
        when (event) {
            is RaKuenEvent.Action.OnRefresh -> refresh(loading = event.loading)
            is RaKuenEvent.Action.OnChangeType -> onChangeType(event.type)
        }
    }

    private fun onChangeType(type: String) = intent {
        reduceData { state.copy(type = type) }
    }
}