package com.xiaoyv.bangumi.features.groups.page.business

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.data.model.request.list.group.ListGroupParam
import com.xiaoyv.bangumi.shared.data.repository.GroupRepository
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun koinGroupsPageViewModel(param: ListGroupParam): GroupsPageViewModel {
    return koinViewModel(
        key = param.uniqueKey,
        parameters = { parametersOf(param) }
    )
}

/**
 * [GroupsPageViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class GroupsPageViewModel(
    savedStateHandle: SavedStateHandle,
    val param: ListGroupParam,
    val groupRepository: GroupRepository,
) : BaseViewModel<GroupsPageState, GroupsPageSideEffect, GroupsPageEvent.Action>(savedStateHandle) {
    private val groupPager = groupRepository.fetchGroupPager(param)
    val group = groupPager.flow.cachedIn(viewModelScope)

    override fun initSate(onCreate: Boolean) = GroupsPageState()

    override fun onEvent(event: GroupsPageEvent.Action) {

    }

}