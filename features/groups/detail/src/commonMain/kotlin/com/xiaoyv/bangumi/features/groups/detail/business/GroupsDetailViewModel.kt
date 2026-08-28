package com.xiaoyv.bangumi.features.groups.detail.business

import androidx.datastore.preferences.core.byteArrayPreferencesKey
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.reduceError
import com.xiaoyv.bangumi.shared.core.mvi.withActionLoading
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.core.utils.onCompletion
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMembership
import com.xiaoyv.bangumi.shared.data.repository.CacheRepository
import com.xiaoyv.bangumi.shared.data.repository.GroupRepository
import com.xiaoyv.bangumi.shared.data.repository.readViewModelCache
import com.xiaoyv.bangumi.shared.data.repository.writeViewModelCache
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
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
    private val cacheRepository: CacheRepository,
) : BaseViewModel<GroupsDetailState, GroupsDetailSideEffect, GroupsDetailEvent.Action>() {
    private val cacheKey = byteArrayPreferencesKey(name = "group_detail:${args.name}")

    override fun initBaseState(): UiState<GroupsDetailState> = readViewModelCache(
        cacheRepository = cacheRepository,
        cacheKey = cacheKey,
        loadWhenEmpty = true
    )

    override fun createInitialState() = GroupsDetailState()

    override fun onEvent(event: GroupsDetailEvent.Action) {
        when (event) {
            is GroupsDetailEvent.Action.OnRefresh -> refresh(event.loading)
            is GroupsDetailEvent.Action.OnToggleJoinGroup -> onToggleJoinGroup()
        }
    }

    override suspend fun Syntax<UiState<GroupsDetailState>, UiSideEffect<GroupsDetailSideEffect>>.refreshSync() {
        groupRepository.fetchGroupDetail(args.name)
            .onFailure { reduceError { it } }
            .onSuccess { reduceData { state.copy(group = it) } }
            .onCompletion {
                writeViewModelCache(cacheRepository, cacheKey)
            }
    }

    private fun onToggleJoinGroup() = intent {
        val currentJoined = state.data.group.membership != ComposeMembership.Empty
        withActionLoading { groupRepository.submitJoinOrExitGroup(args.name, !currentJoined) }
            .onFailure { postToast { it.errMsg } }
            .onSuccess { reduceData { state.copy(group = it) } }
    }
}
