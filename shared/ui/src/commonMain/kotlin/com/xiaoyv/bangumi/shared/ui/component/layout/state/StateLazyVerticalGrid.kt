package com.xiaoyv.bangumi.shared.ui.component.layout.state

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.rounded.Rocket
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import com.xiaoyv.bangumi.shared.ui.component.layout.BgmRefreshBox
import com.xiaoyv.bangumi.shared.ui.component.layout.LocalCollapsingPullRefresh
import com.xiaoyv.bangumi.shared.ui.component.paging.LazyPagingItems
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import kotlinx.coroutines.launch

private const val LOAD_MORE = "LOAD_MORE"
private const val REFRESH_LOADING = "REFRESH_LOADING"
private const val REFRESH_EMPTY = "REFRESH_EMPTY"
private const val REFRESH_ERROR = "REFRESH_ERROR"

@Composable
fun <T : Any> StateLazyVerticalGrid(
    columns: GridCells,
    pagingItems: LazyPagingItems<T>,
    modifier: Modifier = Modifier,
    key: ((T, index: Int) -> Any)? = null,
    state: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showContentLoadingWhenPullRefresh: Boolean = false,
    showScrollUpBtn: Boolean = false,
    reverseLayout: Boolean = false,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    header: LazyGridScope.() -> Unit = {},
    footer: LazyGridScope.() -> Unit = {},
    onRefresh: (Boolean) -> Unit = { pagingItems.refresh() },
    emptyLayout: @Composable () -> Unit = { StateEmptyLayout(onRefresh = { onRefresh(true) }) },
    loadLayout: @Composable () -> Unit = { StateLoadingLayout() },
    errorLayout: @Composable (LoadState.Error) -> Unit = {
        StateErrorLayout(
            throwable = it.error,
            onRefresh = onRefresh
        )
    },
    contentType: (index: Int) -> Any? = { null },
    itemContent: @Composable (LazyGridItemScope.(T, Int) -> Unit),
) {
    var refreshing by rememberSaveable { mutableStateOf(false) }
    val refreshPullState = rememberPullToRefreshState()
    val lazyRefreshState = pagingItems.loadState.refresh

    LaunchedEffect(lazyRefreshState) {
        when (lazyRefreshState) {
            is LoadState.Error -> refreshing = false
            is LoadState.NotLoading -> refreshing = false
            else -> Unit
        }
    }

    BgmRefreshBox(
        modifier = modifier,
        isRefreshing = refreshing,
        isRefreshEnabled = LocalCollapsingPullRefresh.current,
        state = refreshPullState,
        indicator = {
            Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = refreshing,
                state = refreshPullState,
                color = MaterialTheme.colorScheme.primary
            )
        },
        onRefresh = {
            refreshing = true
            onRefresh(false)
        }
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val loadingFooter = remember {
                @Composable {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        BgmProgressIndicator(
                            modifier = Modifier
                                .padding(16.dp)
                                .size(32.dp)
                        )
                    }
                }
            }

            val key: ((Int) -> Any)? = if (key == null) null else {
                {
                    pagingItems.peek(it)?.let { item -> key(item, it) } ?: it
                }
            }

            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = columns,
                state = state,
                userScrollEnabled = pagingItems.itemCount > 0,
                contentPadding = contentPadding,
                reverseLayout = reverseLayout,
                verticalArrangement = verticalArrangement,
                horizontalArrangement = horizontalArrangement,
            ) {
                header()

                items(
                    count = pagingItems.itemCount,
                    key = key,
                    contentType = contentType
                ) { index ->
                    pagingItems[index]?.let { item ->
                        itemContent(item, index)
                    }
                }

                footer()

                gridStateLayout(
                    pagingItems = pagingItems,
                    refreshing = refreshing,
                    showContentLoadingWhenRefresh = showContentLoadingWhenPullRefresh,
                    emptyLayout = emptyLayout,
                    loadLayout = loadLayout,
                    errorLayout = errorLayout,
                    loadingFooter = loadingFooter
                )
            }

            if (showScrollUpBtn) ScrollUpButton(state)
        }
    }
}


private fun <T : Any> LazyGridScope.gridStateLayout(
    pagingItems: LazyPagingItems<T>,
    refreshing: Boolean,
    showContentLoadingWhenRefresh: Boolean,
    emptyLayout: @Composable () -> Unit,
    loadLayout: @Composable () -> Unit,
    errorLayout: @Composable (LoadState.Error) -> Unit,
    loadingFooter: @Composable () -> Unit = {},
) {
    val lazyRefreshState = pagingItems.loadState.refresh
    val lazyAppendState = pagingItems.loadState.append

    when {
        // Show loading view only when refreshing and no cached data
        lazyRefreshState is LoadState.Loading && !refreshing -> if (showContentLoadingWhenRefresh || pagingItems.itemCount == 0) {
            item(
                key = REFRESH_LOADING,
                contentType = REFRESH_LOADING,
                span = { GridItemSpan(maxLineSpan) },
                content = { loadLayout() }
            )
        }
        // Refresh failed
        lazyRefreshState is LoadState.Error -> item(
            key = REFRESH_ERROR,
            contentType = REFRESH_ERROR,
            span = { GridItemSpan(maxLineSpan) },
            content = { errorLayout(lazyRefreshState) }
        )
        // Not loading and data count is 0
        lazyRefreshState is LoadState.NotLoading && pagingItems.itemCount == 0 -> item(
            key = REFRESH_EMPTY,
            contentType = REFRESH_EMPTY,
            span = { GridItemSpan(maxLineSpan) },
            content = { emptyLayout() }
        )
        // Loading more
        lazyAppendState is LoadState.Loading -> item(
            key = LOAD_MORE,
            contentType = LOAD_MORE,
            span = { GridItemSpan(maxLineSpan) },
            content = { loadingFooter() }
        )
    }
}

@Composable
private fun BoxScope.ScrollUpButton(state: LazyGridState) {
    val scope = rememberCoroutineScope()
    var fabVisible by remember { mutableStateOf(false) }

    AnimatedVisibility(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp),
        visible = fabVisible,
        enter = scaleIn(),
        exit = scaleOut()
    ) {
        FloatingActionButton(onClick = { scope.launch { state.scrollToItem(0) } }) {
            Icon(BgmIcons.Rocket, contentDescription = null)
        }
    }

    LaunchedEffect(state) {
        snapshotFlow { state.firstVisibleItemIndex > 0 || state.firstVisibleItemScrollOffset > 0 }
            .collect { shouldShow -> fabVisible = shouldShow }
    }
}