package com.xiaoyv.bangumi.features.blog.page

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import com.xiaoyv.bangumi.features.blog.page.business.BlogPageEvent
import com.xiaoyv.bangumi.features.blog.page.business.BlogPageState
import com.xiaoyv.bangumi.features.blog.page.business.BlogPageViewModel
import com.xiaoyv.bangumi.features.blog.page.business.koinBlogPageViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.types.RakuenIdType
import com.xiaoyv.bangumi.shared.data.model.request.list.blog.ListBlogParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogDisplay
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyColumn
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.paging.LazyPagingItems
import com.xiaoyv.bangumi.shared.ui.component.paging.collectAsLazyPagingItems
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.view.subject.SubjectBlogItem
import org.orbitmvi.orbit.compose.collectAsState

private const val CONTENT_TYPE_BLOG_ITEM = "CONTENT_TYPE_BLOG_ITEM"

@Composable
fun BlogPageRoute(
    param: ListBlogParam,
    onNavScreen: (Screen) -> Unit,
) {
    if (LocalInspectionMode.current) return
    val viewModel: BlogPageViewModel = koinBlogPageViewModel(param)
    val baseState by viewModel.collectAsState()
    val pagingItems = viewModel.blog.collectAsLazyPagingItems()

    viewModel.collectBaseSideEffect {

    }

    BlogPageScreen(
        baseState = baseState,
        pagingItems = pagingItems,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is BlogPageEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        }
    )
}

@Composable
private fun BlogPageScreen(
    baseState: BaseState<BlogPageState>,
    pagingItems: LazyPagingItems<ComposeBlogDisplay>,
    onUiEvent: (BlogPageEvent.UI) -> Unit,
    onActionEvent: (BlogPageEvent.Action) -> Unit,
) {
    StateLayout(
        modifier = Modifier.fillMaxSize(),
        onRefresh = { onActionEvent(BlogPageEvent.Action.OnRefresh(it)) },
        baseState = baseState,
    ) { state ->
        BlogPageScreenContent(state, pagingItems, onUiEvent, onActionEvent)
    }
}


@Composable
private fun BlogPageScreenContent(
    state: BlogPageState,
    pagingItems: LazyPagingItems<ComposeBlogDisplay>,
    onUiEvent: (BlogPageEvent.UI) -> Unit,
    onActionEvent: (BlogPageEvent.Action) -> Unit,
) {
    StateLazyColumn(
        pagingItems = pagingItems,
        modifier = Modifier.fillMaxSize(),
        showScrollUpBtn = true,
        key = { item, _ -> item.uniqueKey },
        contentType = { CONTENT_TYPE_BLOG_ITEM }
    ) { item, _ ->
        SubjectBlogItem(
            modifier = Modifier.fillMaxWidth(),
            item = item,
            onClickUser = {
                onUiEvent(BlogPageEvent.UI.OnNavScreen(Screen.UserDetail(it.username)))
            },
            onClick = {
                onUiEvent(BlogPageEvent.UI.OnNavScreen(Screen.Article(item.blog.id, RakuenIdType.TYPE_BLOG)))
            }
        )
    }
}

