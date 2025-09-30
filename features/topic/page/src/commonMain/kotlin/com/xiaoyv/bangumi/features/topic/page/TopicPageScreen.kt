package com.xiaoyv.bangumi.features.topic.page

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.features.topic.page.business.TopicPageEvent
import com.xiaoyv.bangumi.features.topic.page.business.TopicPageState
import com.xiaoyv.bangumi.features.topic.page.business.TopicPageViewModel
import com.xiaoyv.bangumi.features.topic.page.business.rememberTopicPageViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.data.model.request.list.topic.ListTopicParam
import com.xiaoyv.bangumi.shared.data.model.request.list.topic.LocalListTopicParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeTopic
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyColumn
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.paging.LazyPagingItems
import com.xiaoyv.bangumi.shared.ui.component.paging.collectAsLazyPagingItems
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.view.topic.TopicPageItem
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun TopicPageRoute(
    param: ListTopicParam,
    onNavScreen: (Screen) -> Unit,
) {
    val viewModel: TopicPageViewModel = rememberTopicPageViewModel(param)
    val baseState by viewModel.collectAsState()
    val pagingItems = viewModel.topic.collectAsLazyPagingItems()

    viewModel.collectBaseSideEffect {

    }

    TopicPageScreen(
        baseState = baseState,
        pagingItems = pagingItems,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is TopicPageEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun TopicPageScreen(
    baseState: BaseState<TopicPageState>,
    pagingItems: LazyPagingItems<ComposeTopic>,
    onUiEvent: (TopicPageEvent.UI) -> Unit,
    onActionEvent: (TopicPageEvent.Action) -> Unit,
) {
    StateLayout(
        modifier = Modifier.fillMaxSize(),
        onRefresh = { onActionEvent(TopicPageEvent.Action.OnRefresh(it)) },
        baseState = baseState,
    ) { state ->
        TopicPageScreenContent(state, pagingItems, onUiEvent, onActionEvent)
    }
}


@Composable
private fun TopicPageScreenContent(
    state: TopicPageState,
    pagingItems: LazyPagingItems<ComposeTopic>,
    onUiEvent: (TopicPageEvent.UI) -> Unit,
    onActionEvent: (TopicPageEvent.Action) -> Unit,
) {
    CompositionLocalProvider(LocalListTopicParam provides state.param) {
        StateLazyColumn(
            modifier = Modifier.fillMaxSize(),
            pagingItems = pagingItems
        ) { item, index ->
            TopicPageItem(
                modifier = Modifier.fillMaxWidth(),
                item = item,
                onClick = { onUiEvent(TopicPageEvent.UI.OnNavScreen(Screen.Article(it.id, it.topicType))) },
                onClickUser = { onUiEvent(TopicPageEvent.UI.OnNavScreen(Screen.UserDetail(it.username))) },
                onClickSubject = { onUiEvent(TopicPageEvent.UI.OnNavScreen(Screen.SubjectDetail(it.id))) },
                onClickMono = { onUiEvent(TopicPageEvent.UI.OnNavScreen(Screen.MonoDetail(it.id, it.type))) },
                onReport = { }
            )
            BgmHorizontalDivider()
        }
    }
}

