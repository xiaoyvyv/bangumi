package com.xiaoyv.bangumi.features.groups.detail.business

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.mvi.BaseSyntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.data.model.emnu.GroupMemberRole
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMembership
import com.xiaoyv.bangumi.shared.data.repository.GroupRepository
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf

/**
 * [GroupsDetailViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class GroupsDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val args: Screen.GroupDetail,
    private val groupRepository: GroupRepository,
) : BaseViewModel<GroupsDetailState, GroupsDetailSideEffect, GroupsDetailEvent.Action>(savedStateHandle) {

    override fun initBaseState(): BaseState<GroupsDetailState> = BaseState.Loading()

    override fun initSate(onCreate: Boolean) = GroupsDetailState(
        tabs = persistentListOf(
            ComposeTextTab(0, labelText = "简介"),
            ComposeTextTab(1, labelText = "最新讨论"),
            ComposeTextTab(2, labelText = "成员"),
        ),
        memberFilters = persistentListOf(
            ComposeTextTab(GroupMemberRole.Member, labelText = "成员"),
            ComposeTextTab(GroupMemberRole.Creator, labelText = "创建者"),
            ComposeTextTab(GroupMemberRole.Moderator, labelText = "管理员"),
            ComposeTextTab(GroupMemberRole.Blocked, labelText = "禁言"),
        )
    )

    override fun onEvent(event: GroupsDetailEvent.Action) {
        when (event) {
            is GroupsDetailEvent.Action.OnRefresh -> refresh(event.loading)
            is GroupsDetailEvent.Action.OnToggleJoinGroup -> onToggleJoinGroup()
        }
    }

    private fun onToggleJoinGroup() = action {
        val currentJoined = stateRaw.group.membership != ComposeMembership.Empty
        withActionLoading { groupRepository.submitJoinOrExitGroup(args.name, !currentJoined) }
            .onFailure { postToast { it.errMsg } }
            .onSuccess { reduceContent { state.copy(group = it) } }
    }

    override suspend fun BaseSyntax<GroupsDetailState, GroupsDetailSideEffect>.refreshSync() {
        groupRepository.fetchGroupDetail(args.name)
            .onFailure { reduceError { it } }
            .onSuccess { reduceContent { state.copy(group = it) } }
    }
}