package com.xiaoyv.bangumi.features.groups.detail.business

import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.group_member_role_blocked
import com.xiaoyv.bangumi.core_resource.resources.group_member_role_creator
import com.xiaoyv.bangumi.core_resource.resources.group_member_role_member
import com.xiaoyv.bangumi.core_resource.resources.group_member_role_moderator
import com.xiaoyv.bangumi.core_resource.resources.group_tab_intro
import com.xiaoyv.bangumi.core_resource.resources.group_tab_members
import com.xiaoyv.bangumi.core_resource.resources.group_tab_recent_topics
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.reduceError
import com.xiaoyv.bangumi.shared.core.mvi.withActionLoading
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.data.model.emnu.GroupMemberRole
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMembership
import com.xiaoyv.bangumi.shared.data.repository.GroupRepository
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf
import org.orbitmvi.orbit.syntax.Syntax

/**
 * [GroupsDetailViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class GroupsDetailViewModel(
    private val args: Screen.GroupDetail,
    private val groupRepository: GroupRepository,
) : BaseViewModel<GroupsDetailState, GroupsDetailSideEffect, GroupsDetailEvent.Action>() {

    override fun initBaseState(): UiState<GroupsDetailState> = initBaseLoadingState()

    override fun createInitialState() = GroupsDetailState(
        tabs = persistentListOf(
            ComposeTextTab(0, label = Res.string.group_tab_intro),
            ComposeTextTab(1, label = Res.string.group_tab_recent_topics),
            ComposeTextTab(2, label = Res.string.group_tab_members),
        ),
        memberFilters = persistentListOf(
            ComposeTextTab(GroupMemberRole.Member, label = Res.string.group_member_role_member),
            ComposeTextTab(GroupMemberRole.Creator, label = Res.string.group_member_role_creator),
            ComposeTextTab(GroupMemberRole.Moderator, label = Res.string.group_member_role_moderator),
            ComposeTextTab(GroupMemberRole.Blocked, label = Res.string.group_member_role_blocked),
        )
    )

    override fun onEvent(event: GroupsDetailEvent.Action) {
        when (event) {
            is GroupsDetailEvent.Action.OnRefresh -> refresh(event.loading)
            is GroupsDetailEvent.Action.OnToggleJoinGroup -> onToggleJoinGroup()
        }
    }

    private fun onToggleJoinGroup() = intent {
        val currentJoined = state.data.group.membership != ComposeMembership.Empty
        withActionLoading { groupRepository.submitJoinOrExitGroup(args.name, !currentJoined) }
            .onFailure { postToast { it.errMsg } }
            .onSuccess { reduceData { state.copy(group = it) } }
    }

    override suspend fun Syntax<UiState<GroupsDetailState>, UiSideEffect<GroupsDetailSideEffect>>.refreshSync() {
        groupRepository.fetchGroupDetail(args.name)
            .onFailure { reduceError { it } }
            .onSuccess { reduceData { state.copy(group = it) } }
    }
}