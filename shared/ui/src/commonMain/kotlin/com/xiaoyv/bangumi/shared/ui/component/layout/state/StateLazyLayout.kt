@file:OptIn(ExperimentalFoundationApi::class)

package com.xiaoyv.bangumi.shared.ui.component.layout.state

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.rounded.Rocket
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_no_more
import com.xiaoyv.bangumi.shared.ui.component.layout.BgmRefreshBox
import com.xiaoyv.bangumi.shared.ui.component.layout.LocalCollapsingPullRefresh
import com.xiaoyv.bangumi.shared.ui.component.paging.LazyPagingItems
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

private const val LOAD_MORE = "LOAD_MORE"
private const val REFRESH_LOADING = "REFRESH_LOADING"
private const val REFRESH_EMPTY = "REFRESH_EMPTY"
private const val REFRESH_ERROR = "REFRESH_ERROR"

/**
 * A composable function that provides a lazy column with built-in support for paging, loading states, and error handling.
 *
 * @param T The type of the items in the list.
 * @param pagingItems The [LazyPagingItems] instance that provides the data for the list.
 * @param modifier The modifier to be applied to the layout.
 * @param key A function that provides a unique key for each item in the list.
 * @param state The state of the lazy list.
 * @param contentPadding The padding to be applied to the content of the list.
 * @param showContentLoadingWhenRefresh Whether to show the content loading indicator when refreshing.
 * @param reverseLayout Whether the layout should be reversed.
 * @param verticalArrangement The vertical arrangement of the items in the list.
 * @param horizontalAlignment The horizontal alignment of the items in the list.
 * @param header A composable function that defines the header of the list.
 * @param onRefresh A function that is called when the user refreshes the list.
 * @param emptyLayout A composable function that is displayed when the list is empty.
 * @param loadLayout A composable function that is displayed when the list is loading.
 * @param errorLayout A composable function that is displayed when an error occurs.
 * @param itemContent A composable function that defines the content of each item in the list.
 */
@Composable
fun <T : Any> StateLazyColumn(
    pagingItems: LazyPagingItems<T>,
    modifier: Modifier = Modifier,
    key: ((T, index: Int) -> Any)? = null,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showContentLoadingWhenRefresh: Boolean = false,
    showScrollUpBtn: Boolean = false,
    reverseLayout: Boolean = false,
    userScrollEnabled: Boolean = pagingItems.itemCount > 0,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    header: LazyListScope.() -> Unit = {},
    footer: LazyListScope.() -> Unit = {},
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
    itemContent: @Composable (LazyItemScope.(T, Int) -> Unit),
) {
    StateLazyLayoutImpl(
        columns = null,
        pagingItems = pagingItems,
        modifier = modifier,
        key = key,
        state = state,
        contentPadding = contentPadding,
        showContentLoadingWhenRefresh = showContentLoadingWhenRefresh,
        showScrollUpBtn = showScrollUpBtn,
        userScrollEnabled = userScrollEnabled,
        reverseLayout = reverseLayout,
        listVerticalArrangement = verticalArrangement,
        listHorizontalAlignment = horizontalAlignment,
        listHeader = header,
        listFooter = footer,
        onRefresh = onRefresh,
        emptyLayout = emptyLayout,
        loadLayout = loadLayout,
        errorLayout = errorLayout,
        contentType = contentType,
        itemContent = { data, index ->
            with(this as LazyItemScope) {
                itemContent(data, index)
            }
        }
    )
}

/**
 * A composable function that displays a vertically staggered grid using LazyStaggeredGrid with paging support.
 * It handles loading, error, and empty states, and provides customization options for layout and content.
 *
 * @param T The type of items in the paging data.
 * @param columns The number of columns in the grid.
 * @param pagingItems The LazyPagingItems object representing the paging data.
 * @param modifier Modifier to be applied to the layout.
 * @param key A function to generate a unique key for each item in the grid.
 * @param state The state of the LazyStaggeredGrid.
 * @param contentPadding Padding to be applied to the content of the grid.
 * @param showContentLoadingWhenPullRefresh If true, shows a loading indicator while refreshing the data.
 * @param reverseLayout If true, the items are laid out in reverse order.
 * @param verticalItemSpacing The vertical spacing between items in the grid.
 * @param horizontalArrangement The horizontal arrangement of items in the grid.
 * @param header A composable function to display a header in the grid.
 * @param onRefresh A function to be called when the user refreshes the data.
 * @param emptyLayout A composable function to display when the data is empty.
 * @param loadLayout A composable function to display while the data is loading.
 * @param errorLayout A composable function to display when there is an error loading the data.
 * @param itemContent A composable function to display each item in the grid.
 */
@Composable
fun <T : Any> StateLazyVerticalStaggeredGrid(
    columns: StaggeredGridCells,
    pagingItems: LazyPagingItems<T>,
    modifier: Modifier = Modifier,
    key: ((T, index: Int) -> Any)? = null,
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showContentLoadingWhenPullRefresh: Boolean = false,
    showScrollUpBtn: Boolean = false,
    reverseLayout: Boolean = false,
    userScrollEnabled: Boolean = pagingItems.itemCount > 0,
    verticalItemSpacing: Dp = 0.dp,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    header: LazyStaggeredGridScope.() -> Unit = {},
    footer: LazyStaggeredGridScope.() -> Unit = {},
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
    itemContent: @Composable (LazyStaggeredGridItemScope.(T, Int) -> Unit),
) {
    StateLazyLayoutImpl(
        columns = columns,
        pagingItems = pagingItems,
        modifier = modifier,
        key = key,
        state = state,
        contentPadding = contentPadding,
        showContentLoadingWhenRefresh = showContentLoadingWhenPullRefresh,
        showScrollUpBtn = showScrollUpBtn,
        userScrollEnabled = userScrollEnabled,
        reverseLayout = reverseLayout,
        gridVerticalItemSpacing = verticalItemSpacing,
        gridHorizontalArrangement = horizontalArrangement,
        gridHeader = header,
        gridFooter = footer,
        onRefresh = onRefresh,
        emptyLayout = emptyLayout,
        loadLayout = loadLayout,
        errorLayout = errorLayout,
        contentType = contentType,
        itemContent = { data, index ->
            with(this as LazyStaggeredGridItemScope) {
                itemContent(data, index)
            }
        }
    )
}

/**
 * A composable function that displays a lazy layout (either LazyColumn or LazyVerticalStaggeredGrid)
 * based on the provided parameters. It handles loading states, empty states, and errors,
 * as well as provides pull-to-refresh functionality.
 *
 * @param T The type of items in the PagingData.
 * @param columns The number of columns for a StaggeredGrid layout. If null, a LazyColumn is used.
 * @param pagingItems The LazyPagingItems object containing the data to display.
 * @param modifier The modifier to apply to the layout.
 * @param key A function to generate a key for each item in the list.
 * @param state The ScrollableState object for the layout (either LazyListState or LazyStaggeredGridState).
 * @param contentPadding The padding values for the layout content.
 * @param showContentLoadingWhenRefresh Whether to show the loading layout when refreshing and there's cached data.
 * @param onRefresh A function to execute when the user performs a pull-to-refresh gesture.
 * @param emptyLayout A composable function to display when the data is empty.
 * @param loadLayout A composable function to display while the data is loading.
 * @param errorLayout A composable function to display when there's an error loading the data.
 * @param reverseLayout Whether the layout should be reversed.
 * @param listHeader A composable function to display as the header for a LazyColumn.
 * @param listVerticalArrangement The vertical arrangement of items in a LazyColumn.
 * @param listHorizontalAlignment The horizontal alignment of items in a LazyColumn.
 * @param gridHeader A composable function to display as the header for a LazyVerticalStaggeredGrid.
 * @param gridVerticalItemSpacing The vertical spacing between items in a LazyVerticalStaggeredGrid.
 * @param gridHorizontalArrangement The horizontal arrangement of items in a LazyVerticalStaggeredGrid.
 * @param itemContent A composable function to display each item in the list.
 */
@Composable
private fun <T : Any> StateLazyLayoutImpl(
    columns: StaggeredGridCells?,
    pagingItems: LazyPagingItems<T>,
    modifier: Modifier = Modifier,
    key: ((T, index: Int) -> Any)? = null,
    state: ScrollableState,
    contentPadding: PaddingValues,
    showContentLoadingWhenRefresh: Boolean,
    showScrollUpBtn: Boolean,
    onRefresh: (Boolean) -> Unit,
    emptyLayout: @Composable () -> Unit,
    loadLayout: @Composable () -> Unit,
    errorLayout: @Composable (LoadState.Error) -> Unit,
    reverseLayout: Boolean,
    userScrollEnabled: Boolean,
    listHeader: LazyListScope.() -> Unit = {},
    listFooter: LazyListScope.() -> Unit = {},
    listVerticalArrangement: Arrangement.Vertical = Arrangement.Top,
    listHorizontalAlignment: Alignment.Horizontal = Alignment.Start,
    gridHeader: LazyStaggeredGridScope.() -> Unit = {},
    gridFooter: LazyStaggeredGridScope.() -> Unit = {},
    gridVerticalItemSpacing: Dp = 0.dp,
    gridHorizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    contentType: (index: Int) -> Any? = { null },
    itemContent: @Composable (Any.(T, Int) -> Unit),
) {
    var refreshing by rememberSaveable { mutableStateOf(false) }
    val refreshPullState = rememberPullToRefreshState()

    val lazyRefreshState = pagingItems.loadState.refresh

    LaunchedEffect(lazyRefreshState) {
        when {
            lazyRefreshState is LoadState.Loading && pagingItems.itemCount > 0 -> refreshing = true
            lazyRefreshState is LoadState.Error -> refreshing = false
            lazyRefreshState is LoadState.NotLoading -> refreshing = false
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

            if (columns == null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = state as LazyListState,
                    contentPadding = contentPadding,
                    reverseLayout = reverseLayout,
                    userScrollEnabled = userScrollEnabled,
                    verticalArrangement = listVerticalArrangement,
                    horizontalAlignment = listHorizontalAlignment,
                ) {
                    listHeader()

                    items(
                        count = pagingItems.itemCount,
                        key = key,
                        contentType = contentType
                    ) { index ->
                        pagingItems[index]?.let { item ->
                            itemContent(item, index)
                        }
                    }

                    listFooter()

                    listStateLayout(
                        pagingItems = pagingItems,
                        refreshing = refreshing,
                        showContentLoadingWhenRefresh = showContentLoadingWhenRefresh,
                        emptyLayout = emptyLayout,
                        loadLayout = loadLayout,
                        errorLayout = errorLayout,
                        loadingFooter = loadingFooter
                    )
                }

                if (showScrollUpBtn) ScrollUpButton(state)
            } else {
                LazyVerticalStaggeredGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = columns,
                    state = state as LazyStaggeredGridState,
                    contentPadding = contentPadding,
                    userScrollEnabled = userScrollEnabled,
                    reverseLayout = reverseLayout,
                    horizontalArrangement = gridHorizontalArrangement,
                    verticalItemSpacing = gridVerticalItemSpacing,
                ) {
                    gridHeader()

                    items(
                        count = pagingItems.itemCount,
                        key = key,
                        contentType = contentType
                    ) { index ->
                        pagingItems[index]?.let { item ->
                            itemContent(item, index)
                        }
                    }

                    gridFooter()

                    gridStateLayout(
                        pagingItems = pagingItems,
                        refreshing = refreshing,
                        showContentLoadingWhenRefresh = showContentLoadingWhenRefresh,
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
}

@Composable
private fun BoxScope.ScrollUpButton(state: LazyStaggeredGridState) {
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


@Composable
private fun BoxScope.ScrollUpButton(state: LazyListState) {
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


private fun <T : Any> LazyStaggeredGridScope.gridStateLayout(
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
                span = StaggeredGridItemSpan.FullLine,
                content = { loadLayout() }
            )
        }
        // Refresh failed
        lazyRefreshState is LoadState.Error -> item(
            key = REFRESH_ERROR,
            contentType = REFRESH_ERROR,
            span = StaggeredGridItemSpan.FullLine,
            content = { errorLayout(lazyRefreshState) }
        )
        // Not loading and data count is 0
        lazyRefreshState is LoadState.NotLoading && pagingItems.itemCount == 0 -> item(
            key = REFRESH_EMPTY,
            contentType = REFRESH_EMPTY,
            span = StaggeredGridItemSpan.FullLine,
            content = { emptyLayout() }
        )
        // Loading more
        lazyAppendState is LoadState.Loading -> item(
            key = LOAD_MORE,
            contentType = LOAD_MORE,
            span = StaggeredGridItemSpan.FullLine,
            content = { loadingFooter() }
        )
    }
}


private fun <T : Any> LazyListScope.listStateLayout(
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
        lazyRefreshState is LoadState.Loading && !refreshing -> {
            if (showContentLoadingWhenRefresh || pagingItems.itemCount == 0) {
                item(
                    key = REFRESH_LOADING,
                    contentType = REFRESH_LOADING,
                    content = {
//                        loadLayout()
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 240.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            BgmProgressIndicator(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .size(32.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                )
            }
        }
        // Refresh failed
        lazyRefreshState is LoadState.Error && pagingItems.itemCount == 0 -> item(
            key = REFRESH_ERROR,
            contentType = REFRESH_ERROR,
            content = { errorLayout(lazyRefreshState) }
        )
        // Not loading and data count is 0
        lazyRefreshState is LoadState.NotLoading && pagingItems.itemCount == 0 -> item(
            key = REFRESH_EMPTY,
            contentType = REFRESH_EMPTY,
            content = { emptyLayout() }
        )
    }

    when {
        // Loading more
        lazyAppendState is LoadState.Loading -> item(
            key = LOAD_MORE,
            contentType = LOAD_MORE,
            content = { loadingFooter() }
        )

        // No More
        lazyAppendState is LoadState.NotLoading && lazyAppendState.endOfPaginationReached && pagingItems.itemCount != 0 -> item(
            key = LOAD_MORE,
            contentType = LOAD_MORE,
            content = {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    text = stringResource(Res.string.global_no_more),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        )
    }
}


inline fun LazyListScope.itemKey(
    unique: String,
    visible: Boolean = true,
    crossinline content: @Composable LazyItemScope.() -> Unit,
) {
    if (visible) item(key = unique, contentType = unique) {
        content()
    }
}


