package com.xiaoyv.bangumi.features.main.tab.home.page.group

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.group_hot
import com.xiaoyv.bangumi.core_resource.resources.group_newest
import com.xiaoyv.bangumi.core_resource.resources.group_newest_topic
import com.xiaoyv.bangumi.features.groups.page.GroupsPageRoute
import com.xiaoyv.bangumi.features.main.tab.home.business.HomeEvent
import com.xiaoyv.bangumi.features.main.tab.home.business.HomeState
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.layout.state.rememberCacheWindowLazyListState
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmChipHorizontalPager
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.text.SectionTitle
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens.TAB_GROUP_HOMEPAGE
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens.mainHomeGroupFilters
import com.xiaoyv.bangumi.shared.ui.view.group.GroupPageItem
import com.xiaoyv.bangumi.shared.ui.view.topic.TopicPageItem
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState


const val CONTENT_TYPE_HEADER = "CONTENT_TYPE_HEADER"
const val CONTENT_TYPE_GROUP_ITEM = "CONTENT_TYPE_GROUP_ITEM"
const val CONTENT_TYPE_TOPIC_ITEM = "CONTENT_TYPE_TOPIC_ITEM"

@Composable
fun HomeGroupScreen(
    state: HomeState,
    onUiEvent: (HomeEvent.UI) -> Unit,
    onActionEvent: (HomeEvent.Action) -> Unit,
) {
    BgmChipHorizontalPager(
        modifier = Modifier.fillMaxSize(),
        tabs = mainHomeGroupFilters,
    ) {
        val type = mainHomeGroupFilters[it].type
        if (type == TAB_GROUP_HOMEPAGE) {
            HomeGroupHotScreen(
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )
        } else {
            GroupsPageRoute(
                param = state.rememberListGroupParam(type),
                onNavScreen = { screen ->
                    onUiEvent(HomeEvent.UI.OnNavScreen(screen))
                }
            )
        }
    }
}


@Composable
private fun HomeGroupHotScreen(
    onUiEvent: (HomeEvent.UI) -> Unit,
    onActionEvent: (HomeEvent.Action) -> Unit,
) {
    if (LocalInspectionMode.current) return
    val viewModel = koinViewModel<HomeGroupViewModel>()
    val baseState by viewModel.collectAsState()

    StateLayout(
        modifier = Modifier.fillMaxSize(),
        baseState = baseState,
        onRefresh = { viewModel.refresh(it) },
        enablePullRefresh = true
    ) {
        HomeGroupHotScreenContent(
            state = it,
            onUiEvent = onUiEvent,
            onActionEvent = onActionEvent
        )
    }
}

@Composable
private fun HomeGroupHotScreenContent(
    state: HomeGroupState,
    onUiEvent: (HomeEvent.UI) -> Unit,
    onActionEvent: (HomeEvent.Action) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = rememberCacheWindowLazyListState()
    ) {
        stickyHeader(key = "h1", contentType = CONTENT_TYPE_HEADER) {
            SectionTitle(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = LayoutPadding, vertical = LayoutPaddingHalf),
                text = stringResource(Res.string.group_hot),
                showMore = false
            )
        }

        items(state.hotGroups, key = { it.name }, contentType = { CONTENT_TYPE_GROUP_ITEM }) { item ->
            GroupPageItem(
                item = item,
                onClick = { onUiEvent(HomeEvent.UI.OnNavScreen(Screen.GroupDetail(it.name))) }
            )
        }

        stickyHeader(key = "h2", contentType = CONTENT_TYPE_HEADER) {
            SectionTitle(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = LayoutPadding, vertical = LayoutPaddingHalf),
                text = stringResource(Res.string.group_newest),
                showMore = false
            )
        }

        items(state.newestGroups, key = { it.name }, contentType = { CONTENT_TYPE_GROUP_ITEM }) { item ->
            GroupPageItem(
                item = item,
                onClick = { onUiEvent(HomeEvent.UI.OnNavScreen(Screen.GroupDetail(it.name))) }
            )
        }

        stickyHeader(key = "h3", contentType = CONTENT_TYPE_HEADER) {
            SectionTitle(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = LayoutPadding, vertical = LayoutPaddingHalf),
                text = stringResource(Res.string.group_newest_topic),
                showMore = false
            )
        }

        items(state.newestTopics, key = { it.id }, contentType = { CONTENT_TYPE_TOPIC_ITEM }) { item ->
            TopicPageItem(
                modifier = Modifier.fillMaxWidth(),
                item = item,
                onClick = { onUiEvent(HomeEvent.UI.OnNavScreen(Screen.Article(it.id, it.topicType))) },
                onClickUser = { onUiEvent(HomeEvent.UI.OnNavScreen(Screen.UserDetail(it.username))) },
                onClickSubject = { onUiEvent(HomeEvent.UI.OnNavScreen(Screen.SubjectDetail(it.id))) },
                onClickMono = { onUiEvent(HomeEvent.UI.OnNavScreen(Screen.MonoDetail(it.id, it.type))) },
                onReport = { }
            )
        }
    }
}