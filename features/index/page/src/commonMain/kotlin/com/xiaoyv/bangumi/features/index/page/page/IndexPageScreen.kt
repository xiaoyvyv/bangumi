package com.xiaoyv.bangumi.features.index.page.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.xiaoyv.bangumi.shared.data.model.request.list.index.ListIndexParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndex
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyVerticalGrid
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.HideInPreview
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import com.xiaoyv.bangumi.shared.ui.view.index.IndexPageItem

const val CONTENT_TYPE_INDEX_ITEM = "CONTENT_TYPE_INDEX_ITEM"

@Composable
fun IndexPageRoute(
    param: ListIndexParam,
    onNavScreen: (Screen) -> Unit,
) = HideInPreview {
    val viewModel: IndexPageViewModel = koinIndexPageViewModel(param)
    val pagingItems = viewModel.index.collectAsLazyPagingItems()

    IndexPageScreenContent(pagingItems, onNavScreen)
}


@Composable
private fun IndexPageScreenContent(
    pagingItems: LazyPagingItems<ComposeIndex>,
    onNavScreen: (Screen) -> Unit,
) {
    StateLazyVerticalGrid(
        pagingItems = pagingItems,
        columns = GridCells.Adaptive(250.dp),
        modifier = Modifier.fillMaxSize(),
        showScrollUpBtn = true,
        contentPadding = PaddingValues(ContentMarginHalf),
        verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
        horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
        key = { item, _ -> item.id },
        contentType = { CONTENT_TYPE_INDEX_ITEM }
    ) { item, _ ->
        IndexPageItem(
            modifier = Modifier.fillMaxWidth(),
            item = item,
            onClick = { onNavScreen(Screen.IndexDetail(item.id)) }
        )
    }
}
