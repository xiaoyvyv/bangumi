package com.xiaoyv.bangumi.shared.ui.component.scroll

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * 当前页面的回顶控制器。
 *
 * Pager 会将当前页的控制器设为子控制器，因此未显示的预加载页不会响应回顶事件。
 */
@Stable
class ScrollUpState {
    private var scrollTopAction: (suspend () -> Unit)? = null
    private var childState: ScrollUpState? = null

    suspend fun scrollTop() {
        childState?.scrollTop() ?: scrollTopAction?.invoke()
    }

    internal fun setScrollTopAction(action: suspend () -> Unit) {
        scrollTopAction = action
    }

    internal fun clearScrollTopAction(action: suspend () -> Unit) {
        if (scrollTopAction === action) scrollTopAction = null
    }

    internal fun setChildState(state: ScrollUpState) {
        childState = state
    }

    internal fun clearChildState(state: ScrollUpState) {
        if (childState === state) childState = null
    }
}

/**
 * 未提供页面回顶控制器时使用的空实现。
 */
private val EmptyScrollUpState = ScrollUpState()

/**
 * 当前组合页面的回顶控制器。
 */
val LocalScrollUpState = staticCompositionLocalOf { EmptyScrollUpState }

@Composable
fun rememberScrollUpState(): ScrollUpState = remember { ScrollUpState() }

/**
 * Pager 的页面回顶状态配置。
 *
 * 仅将 `currentPage` 对应的页面状态绑定到父级，确保预加载或后台页面不会响应回顶。
 */
@Stable
class ScrollUpPagerState internal constructor(
    private val pageStates: List<ScrollUpState>,
) {
    fun pageState(page: Int): ScrollUpState = pageStates[page]
}

/**
 * 创建 Pager 专用的页面回顶状态，并将当前页绑定到当前组合上下文。
 *
 * @param pageCount Pager 页面数量
 * @param currentPage 当前显示的页面下标
 */
@Composable
fun rememberScrollUpPagerState(
    pageCount: Int,
    currentPage: Int,
): ScrollUpPagerState {
    val parentState = LocalScrollUpState.current
    val pagerState = remember(pageCount) {
        ScrollUpPagerState(List(pageCount) { ScrollUpState() })
    }
    val currentPageState = pagerState.pageState(currentPage)

    DisposableEffect(parentState, currentPageState) {
        parentState.setChildState(currentPageState)
        onDispose { parentState.clearChildState(currentPageState) }
    }

    return pagerState
}

/**
 * 创建并注册可回顶的 LazyColumn 状态。
 */
@Composable
fun rememberScrollUpLazyListState(
    state: LazyListState = rememberLazyListState(),
): LazyListState {
    RegisterScrollUpAction { state.animateScrollToItem(0) }
    return state
}

/**
 * 创建并注册可回顶的 LazyVerticalGrid 状态。
 */
@Composable
fun rememberScrollUpLazyGridState(
    state: LazyGridState = rememberLazyGridState(),
): LazyGridState {
    RegisterScrollUpAction { state.scrollToItem(0) }
    return state
}

/**
 * 创建并注册可回顶的 LazyVerticalStaggeredGrid 状态。
 */
@Composable
fun rememberScrollUpLazyStaggeredGridState(
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
): LazyStaggeredGridState {
    RegisterScrollUpAction { state.scrollToItem(0) }
    return state
}

/**
 * 创建并注册可回顶的普通纵向滚动状态。
 */
@Composable
fun rememberScrollUpScrollState(
    state: ScrollState = rememberScrollState(),
): ScrollState {
    RegisterScrollUpAction { state.scrollTo(0) }
    return state
}

@Composable
private fun RegisterScrollUpAction(action: suspend () -> Unit) {
    val scrollUpState = LocalScrollUpState.current

    DisposableEffect(scrollUpState, action) {
        scrollUpState.setScrollTopAction(action)
        onDispose { scrollUpState.clearScrollTopAction(action) }
    }
}
