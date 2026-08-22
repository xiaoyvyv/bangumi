package com.xiaoyv.bangumi.features.pixiv.main.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import com.xiaoyv.bangumi.features.pixiv.main.business.PixivMainEvent
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyVerticalGrid
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.view.pixiv.PixivRankingItem

private const val CONTENT_TYPE_PIXIV_RANKING = "CONTENT_TYPE_PIXIV_RANKING"

@Composable
fun PixivMainPageScreen(
    content: String,
    viewModel: PixivMainPageViewModel = koinPixivMainPageViewModel(content),
    onUiEvent: (PixivMainEvent.UI) -> Unit,
    onActionEvent: (PixivMainEvent.Action) -> Unit,
) {
    val pagingItems = viewModel.rankingFlow.collectAsLazyPagingItems()

    StateLazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(2),
        pagingItems = pagingItems,
        contentPadding = PaddingValues(ContentMarginHalf),
        horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
        verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
        showScrollUpBtn = true,
        key = { item, _ -> item.illust_id },
        contentType = { CONTENT_TYPE_PIXIV_RANKING }
    ) { item, _ ->
        PixivRankingItem(
            item = item,
            onClick = {
                // Click handler
            }
        )
    }
}
