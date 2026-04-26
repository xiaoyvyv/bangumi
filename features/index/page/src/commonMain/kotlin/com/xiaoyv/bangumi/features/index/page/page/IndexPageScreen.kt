package com.xiaoyv.bangumi.features.index.page.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.data.model.request.list.index.ListIndexParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndex
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyColumn
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.paging.LazyPagingItems
import com.xiaoyv.bangumi.shared.ui.component.paging.collectAsLazyPagingItems
import com.xiaoyv.bangumi.shared.ui.view.index.IndexPageItem

const val CONTENT_TYPE_INDEX_ITEM = "CONTENT_TYPE_INDEX_ITEM"

val LocalIndexGridLayoutContentPadding = compositionLocalOf { PaddingValues(horizontal = 12.dp, vertical = 4.dp) }

@Composable
fun IndexPageRoute(
    param: ListIndexParam,
    onNavScreen: (Screen) -> Unit,
) {
    if (LocalInspectionMode.current) return
    val viewModel: IndexPageViewModel = koinIndexPageViewModel(param)
    val pagingItems = viewModel.index.collectAsLazyPagingItems()

    IndexPageScreenContent(pagingItems, onNavScreen)
}


@Composable
private fun IndexPageScreenContent(
    pagingItems: LazyPagingItems<ComposeIndex>,
    onNavScreen: (Screen) -> Unit,
) {
    StateLazyColumn(
        pagingItems = pagingItems,
        modifier = Modifier.fillMaxSize(),
        showScrollUpBtn = true,
        contentPadding = LocalIndexGridLayoutContentPadding.current,
        verticalArrangement = Arrangement.spacedBy(12.dp),
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

