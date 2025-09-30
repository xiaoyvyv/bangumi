package com.xiaoyv.bangumi.features.subject.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.features.subject.page.business.SubjectPageEvent
import com.xiaoyv.bangumi.features.subject.page.business.SubjectPageState
import com.xiaoyv.bangumi.features.subject.page.business.SubjectPageViewModel
import com.xiaoyv.bangumi.features.subject.page.business.koinSubjectPageViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.utils.ignoreLazyGridContentPadding
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.ListSubjectParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSubjectDisplay
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyColumn
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyVerticalGrid
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.paging.LazyPagingItems
import com.xiaoyv.bangumi.shared.ui.component.paging.collectAsLazyPagingItems
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.kts.isExtraSmallScreen
import com.xiaoyv.bangumi.shared.ui.view.subject.SubjectCardItem
import com.xiaoyv.bangumi.shared.ui.view.subject.SubjectLineItem
import org.orbitmvi.orbit.compose.collectAsState

private const val ITEM_HEADER = "Header"

@Composable
fun SubjectPageRoute(
    param: ListSubjectParam,
    header: (@Composable () -> Unit)? = null,
    headerSticky: Boolean = false,
    onNavScreen: (Screen) -> Unit,
) {
    if (LocalInspectionMode.current) return
    val viewModel: SubjectPageViewModel = koinSubjectPageViewModel(param)
    val baseState by viewModel.collectAsState()
    val pagingItems = viewModel.subjects.collectAsLazyPagingItems()

    viewModel.collectBaseSideEffect {

    }

    SubjectPageScreen(
        baseState = baseState,
        pagingItems = pagingItems,
        header = header,
        headerSticky = headerSticky,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is SubjectPageEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun SubjectPageScreen(
    baseState: BaseState<SubjectPageState>,
    pagingItems: LazyPagingItems<ComposeSubjectDisplay>,
    header: (@Composable () -> Unit)? = null,
    headerSticky: Boolean = false,
    onUiEvent: (SubjectPageEvent.UI) -> Unit,
    onActionEvent: (SubjectPageEvent.Action) -> Unit,
) {
    StateLayout(
        modifier = Modifier.fillMaxSize(),
        onRefresh = { onActionEvent(SubjectPageEvent.Action.OnRefresh(it)) },
        baseState = baseState,
    ) { state ->
        SubjectPageScreenContent(
            state = state,
            pagingItems = pagingItems,
            header = header,
            headerSticky = headerSticky,
            onUiEvent = onUiEvent,
            onActionEvent = onActionEvent
        )
    }
}


@Composable
private fun SubjectPageScreenContent(
    state: SubjectPageState,
    pagingItems: LazyPagingItems<ComposeSubjectDisplay>,
    header: (@Composable () -> Unit)? = null,
    headerSticky: Boolean = false,
    onUiEvent: (SubjectPageEvent.UI) -> Unit,
    onActionEvent: (SubjectPageEvent.Action) -> Unit,
) {

    if (state.param.ui.gridLayout) {
        val gridCells = if (isExtraSmallScreen) GridCells.Fixed(3) else GridCells.Adaptive(100.dp)

        StateLazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = gridCells,
            pagingItems = pagingItems,
            header = {
                if (header != null) {
                    if (headerSticky) stickyHeader(
                        key = ITEM_HEADER,
                        contentType = ITEM_HEADER,
                        content = { Box(modifier = Modifier.ignoreLazyGridContentPadding(12.dp)) { header() } }
                    )
                    else item(
                        key = ITEM_HEADER,
                        contentType = ITEM_HEADER,
                        content = { Box(modifier = Modifier.ignoreLazyGridContentPadding(12.dp)) { header() } }
                    )
                }
            },
            showScrollUpBtn = true,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(12.dp)
        ) { item, _ ->
            SubjectCardItem(
                modifier = Modifier.fillMaxWidth(),
                display = item,
                style = MaterialTheme.typography.bodyMedium,
                onClick = { onUiEvent(SubjectPageEvent.UI.OnNavScreen(Screen.SubjectDetail(item.subject.id))) }
            )
        }
    } else {
        val lazyListState = rememberLazyListState()

        StateLazyColumn(
            state = lazyListState,
            pagingItems = pagingItems,
            header = {
                if (header != null) {
                    if (headerSticky) stickyHeader(key = ITEM_HEADER, contentType = ITEM_HEADER) { header() }
                    else item(key = ITEM_HEADER, contentType = ITEM_HEADER) { header() }
                }
            },
            showScrollUpBtn = true,
            modifier = Modifier.fillMaxSize()
        ) { item, _ ->
            SubjectLineItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = LayoutPadding, vertical = 12.dp),
                display = item,
                onClick = { onUiEvent(SubjectPageEvent.UI.OnNavScreen(Screen.SubjectDetail(item.subject.id))) }
            )
        }
    }
}

