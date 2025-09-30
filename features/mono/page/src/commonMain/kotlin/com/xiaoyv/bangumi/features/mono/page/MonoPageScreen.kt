package com.xiaoyv.bangumi.features.mono.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.features.mono.page.business.MonoPageEvent
import com.xiaoyv.bangumi.features.mono.page.business.MonoPageState
import com.xiaoyv.bangumi.features.mono.page.business.MonoPageViewModel
import com.xiaoyv.bangumi.features.mono.page.business.koinMonoPageViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.utils.ignoreLazyGridContentPadding
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.ListMonoParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyColumn
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyVerticalGrid
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.paging.LazyPagingItems
import com.xiaoyv.bangumi.shared.ui.component.paging.collectAsLazyPagingItems
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.view.mono.MonoCardItem
import com.xiaoyv.bangumi.shared.ui.view.mono.MonoLineItem
import org.orbitmvi.orbit.compose.collectAsState

private const val CONTENT_TYPE_HEADER = "CONTENT_TYPE_HEADER"
private const val CONTENT_TYPE_MONO_ITEM = "CONTENT_TYPE_MONO_ITEM"

@Composable
fun MonoPageRoute(
    param: ListMonoParam,
    viewModel: MonoPageViewModel = koinMonoPageViewModel(param),
    headerSticky: Boolean = false,
    header: @Composable (() -> Unit)? = null,
    onNavUp: () -> Unit = {},
    onNavScreen: (Screen) -> Unit = {},
) {
    val baseState by viewModel.collectAsState()
    val pagingItems = viewModel.monos.collectAsLazyPagingItems()

    viewModel.collectBaseSideEffect {}

    MonoPageScreen(
        baseState = baseState,
        pagingItems = pagingItems,
        headerSticky = headerSticky,
        header = header,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is MonoPageEvent.UI.OnNavUp -> onNavUp()
                is MonoPageEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun MonoPageScreen(
    baseState: BaseState<MonoPageState>,
    pagingItems: LazyPagingItems<ComposeMonoDisplay>,
    onUiEvent: (MonoPageEvent.UI) -> Unit,
    onActionEvent: (MonoPageEvent.Action) -> Unit,
    headerSticky: Boolean = false,
    header: @Composable (() -> Unit)? = null,
) {
    StateLayout(
        modifier = Modifier.fillMaxSize(),
        onRefresh = { onActionEvent(MonoPageEvent.Action.OnRefresh(it)) },
        baseState = baseState,
    ) { state ->
        if (state.param.ui.gridLayout) {
            MonoPageGridLayout(pagingItems, header, headerSticky, onUiEvent)
        } else {
            MonoPageLineLayout(pagingItems, header, headerSticky, onUiEvent)
        }
    }
}

/**
 * 列表布局
 */
@Composable
private fun MonoPageLineLayout(
    pagingItems: LazyPagingItems<ComposeMonoDisplay>,
    header: @Composable (() -> Unit)?,
    headerSticky: Boolean,
    onUiEvent: (MonoPageEvent.UI) -> Unit,
) {
    StateLazyColumn(
        modifier = Modifier.fillMaxSize(),
        pagingItems = pagingItems,
        header = {
            if (header != null) {
                if (headerSticky) {
                    stickyHeader(
                        key = CONTENT_TYPE_HEADER,
                        contentType = CONTENT_TYPE_HEADER,
                        content = { header() }
                    )
                } else {
                    item(
                        key = CONTENT_TYPE_HEADER,
                        contentType = CONTENT_TYPE_HEADER,
                        content = { header() }
                    )
                }
            }
        },
        showScrollUpBtn = true,
        key = { item, _ -> item.id },
        contentType = { CONTENT_TYPE_MONO_ITEM }
    ) { item, index ->
        MonoLineItem(
            modifier = Modifier.fillMaxWidth(),
            item = item,
            onClick = { id, type ->
                onUiEvent(MonoPageEvent.UI.OnNavScreen(Screen.MonoDetail(id, type)))
            }
        )
    }
}

/**
 * 网格布局
 */
@Composable
private fun MonoPageGridLayout(
    pagingItems: LazyPagingItems<ComposeMonoDisplay>,
    header: @Composable (() -> Unit)?,
    headerSticky: Boolean,
    onUiEvent: (MonoPageEvent.UI) -> Unit,
) {
    StateLazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        pagingItems = pagingItems,
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = LayoutPaddingHalf),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
        header = {
            if (header != null) {
                if (headerSticky) stickyHeader(
                    key = CONTENT_TYPE_HEADER,
                    contentType = CONTENT_TYPE_HEADER,
                    content = { Box(modifier = Modifier.ignoreLazyGridContentPadding(12.dp)) { header() } }
                ) else item(
                    key = CONTENT_TYPE_HEADER,
                    contentType = CONTENT_TYPE_HEADER,
                    span = { GridItemSpan(maxLineSpan) },
                    content = { Box(modifier = Modifier.ignoreLazyGridContentPadding(12.dp)) { header() } }
                )
            }
        },
        showScrollUpBtn = true,
        key = { item, _ -> item.id },
        contentType = { CONTENT_TYPE_MONO_ITEM }
    ) { item, index ->
        MonoCardItem(
            modifier = Modifier.fillMaxWidth(),
            item = item,
            onClick = { id, type ->
                onUiEvent(MonoPageEvent.UI.OnNavScreen(Screen.MonoDetail(id, type)))
            }
        )
    }
}

