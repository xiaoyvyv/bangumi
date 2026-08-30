package com.xiaoyv.bangumi.features.groups.page

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.xiaoyv.bangumi.features.groups.page.business.GroupsPageEvent
import com.xiaoyv.bangumi.features.groups.page.business.GroupsPageState
import com.xiaoyv.bangumi.features.groups.page.business.GroupsPageViewModel
import com.xiaoyv.bangumi.features.groups.page.business.koinGroupsPageViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.data.model.request.list.group.ListGroupParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroup
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyColumn
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.HideInPreview
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.view.group.GroupPageItem
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun GroupsPageRoute(
    param: ListGroupParam,
    onNavScreen: (Screen) -> Unit,
) = HideInPreview {
    val viewModel: GroupsPageViewModel = koinGroupsPageViewModel(param)
    val baseState by viewModel.collectAsState()
    val pagingItems = viewModel.group.collectAsLazyPagingItems()

    viewModel.collectBaseSideEffect {

    }

    GroupsPageScreen(
        uiState = baseState,
        pagingItems = pagingItems,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is GroupsPageEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun GroupsPageScreen(
    uiState: UiState<GroupsPageState>,
    pagingItems: LazyPagingItems<ComposeGroup>,
    onUiEvent: (GroupsPageEvent.UI) -> Unit,
    onActionEvent: (GroupsPageEvent.Action) -> Unit,
) {
    StateLayout(
        modifier = Modifier.fillMaxSize(),
        onRefresh = { onActionEvent(GroupsPageEvent.Action.OnRefresh(it)) },
        uiState = uiState,
    ) { state ->
        GroupsPageScreenContent(state, pagingItems, onUiEvent, onActionEvent)
    }
}


@Composable
private fun GroupsPageScreenContent(
    state: GroupsPageState,
    pagingItems: LazyPagingItems<ComposeGroup>,
    onUiEvent: (GroupsPageEvent.UI) -> Unit,
    onActionEvent: (GroupsPageEvent.Action) -> Unit,
) {
    StateLazyColumn(
        state = com.xiaoyv.bangumi.shared.ui.component.scroll.rememberScrollUpLazyListState(),
        modifier = Modifier.fillMaxSize(),
        pagingItems = pagingItems,
    ) { item, index ->
        GroupPageItem(
            item = item,
            onClick = {
                onUiEvent(GroupsPageEvent.UI.OnNavScreen(Screen.GroupDetail(it.name)))
            }
        )
        BgmHorizontalDivider()
    }
}