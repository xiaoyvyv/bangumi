package com.xiaoyv.bangumi.features.main.tab.home.page

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.features.blog.page.BlogPageRoute
import com.xiaoyv.bangumi.features.main.tab.home.business.HomeEvent
import com.xiaoyv.bangumi.features.main.tab.home.business.HomeState
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmChipHorizontalPager
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens.mainHomeBlogFilters

@Composable
fun HomeBlogScreen(
    state: HomeState,
    onUiEvent: (HomeEvent.UI) -> Unit,
    onActionEvent: (HomeEvent.Action) -> Unit,
) {
    BgmChipHorizontalPager(
        modifier = Modifier.fillMaxSize(),
        tabs = mainHomeBlogFilters,
    ) {
        BlogPageRoute(
            param = state.rememberListBlogParam(mainHomeBlogFilters[it].type),
            onNavScreen = { screen -> onUiEvent(HomeEvent.UI.OnNavScreen(screen)) }
        )
    }
}