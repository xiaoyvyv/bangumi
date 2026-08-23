package com.xiaoyv.bangumi.features.pixiv.illust.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.xiaoyv.bangumi.features.pixiv.illust.page.business.IllustPageEvent
import com.xiaoyv.bangumi.features.pixiv.illust.page.business.IllustPageState
import com.xiaoyv.bangumi.features.pixiv.illust.page.business.IllustPageViewModel
import com.xiaoyv.bangumi.features.pixiv.illust.page.business.koinIllustPageViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.utils.ignoreLazyGridContentPadding
import com.xiaoyv.bangumi.shared.data.model.request.list.pixiv.ListIllustParam
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivRankingContent
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyColumn
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyVerticalGrid
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import com.xiaoyv.bangumi.shared.ui.view.pixiv.PixivRankingItem
import androidx.paging.PagingData
import kotlinx.coroutines.flow.flowOf
import androidx.compose.ui.tooling.preview.Preview
import org.orbitmvi.orbit.compose.collectAsState

private const val ITEM_HEADER = "Header"
private const val CONTENT_TYPE_PIXIV_RANKING = "CONTENT_TYPE_PIXIV_RANKING"

@Composable
fun IllustPageRoute(
    param: ListIllustParam,
    header: (@Composable () -> Unit)? = null,
    headerSticky: Boolean = false,
    onNavScreen: (Screen) -> Unit,
) {
    if (LocalInspectionMode.current) return
    val viewModel: IllustPageViewModel = koinIllustPageViewModel(param)
    val baseState by viewModel.collectAsState()
    val pagingItems = viewModel.illusts.collectAsLazyPagingItems()

    IllustPageScreen(
        uiState = baseState,
        pagingItems = pagingItems,
        header = header,
        headerSticky = headerSticky,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is IllustPageEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun IllustPageScreen(
    uiState: UiState<IllustPageState>,
    pagingItems: LazyPagingItems<ComposePixivRankingContent>,
    header: (@Composable () -> Unit)? = null,
    headerSticky: Boolean = false,
    onUiEvent: (IllustPageEvent.UI) -> Unit,
    onActionEvent: (IllustPageEvent.Action) -> Unit,
) {
    StateLayout(
        modifier = Modifier.fillMaxSize(),
        onRefresh = { onActionEvent(IllustPageEvent.Action.OnRefresh(it)) },
        uiState = uiState,
    ) { state ->
        IllustPageScreenContent(
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
private fun IllustPageScreenContent(
    state: IllustPageState,
    pagingItems: LazyPagingItems<ComposePixivRankingContent>,
    header: (@Composable () -> Unit)? = null,
    headerSticky: Boolean = false,
    onUiEvent: (IllustPageEvent.UI) -> Unit,
    onActionEvent: (IllustPageEvent.Action) -> Unit,
) {
    if (state.param.ui.gridLayout) {
        StateLazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(2),
            pagingItems = pagingItems,
            header = {
                if (header != null) {
                    if (headerSticky) stickyHeader(
                        key = ITEM_HEADER,
                        contentType = ITEM_HEADER,
                        content = { Box(modifier = Modifier.ignoreLazyGridContentPadding(ContentMarginHalf)) { header() } }
                    )
                    else item(
                        key = ITEM_HEADER,
                        contentType = ITEM_HEADER,
                        content = { Box(modifier = Modifier.ignoreLazyGridContentPadding(ContentMarginHalf)) { header() } }
                    )
                }
            },
            showScrollUpBtn = true,
            horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
            verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
            contentPadding = PaddingValues(ContentMarginHalf),
            key = { item, _ -> item.illust_id },
            contentType = { CONTENT_TYPE_PIXIV_RANKING }
        ) { item, _ ->
            PixivRankingItem(
                modifier = Modifier.fillMaxWidth(),
                item = item,
                onClick = { clickedItem ->
                    if (clickedItem.illust_id > 0) {
                        onUiEvent(IllustPageEvent.UI.OnNavScreen(Screen.PixivIllust(clickedItem.illust_id)))
                    }
                }
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
            modifier = Modifier.fillMaxSize(),
            key = { item, _ -> item.illust_id },
            contentType = { CONTENT_TYPE_PIXIV_RANKING }
        ) { item, _ ->
            PixivRankingItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ContentMargin, vertical = ContentMarginHalf),
                item = item,
                onClick = { clickedItem ->
                    if (clickedItem.illust_id > 0) {
                        onUiEvent(IllustPageEvent.UI.OnNavScreen(Screen.PixivIllust(clickedItem.illust_id)))
                    }
                }
            )
        }
    }
}

@Composable
@Preview
private fun PreviewIllustPageScreen() {
    PreviewColumn(modifier = Modifier.fillMaxSize()) {
        IllustPageScreen(
            uiState = UiState(IllustPageState()),
            pagingItems = flowOf(PagingData.from(listOf(ComposePixivRankingContent(title = "Preview Illust")))).collectAsLazyPagingItems(),
            onUiEvent = {},
            onActionEvent = {}
        )
    }
}
