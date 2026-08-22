package com.xiaoyv.bangumi.features.timeline.page

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.xiaoyv.bangumi.features.timeline.page.business.TimelinePageEvent
import com.xiaoyv.bangumi.features.timeline.page.business.TimelinePageViewModel
import com.xiaoyv.bangumi.features.timeline.page.business.koinTimelinePageViewModel
import com.xiaoyv.bangumi.shared.data.model.request.list.timeline.ListTimelineParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimeline
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyColumn
import com.xiaoyv.bangumi.shared.ui.component.layout.state.rememberCacheWindowLazyListState
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.view.timeline.TimelinePageItem

private const val CONTENT_TYPE_TIMELINE = "CONTENT_TYPE_TIMELINE"

@Composable
fun TimelinePageRoute(
    param: ListTimelineParam,
    onNavScreen: (Screen) -> Unit,
) {
    if (LocalInspectionMode.current) return

    val viewModel: TimelinePageViewModel = koinTimelinePageViewModel(param)
    val pagingItems = viewModel.timelines.collectAsLazyPagingItems()

    viewModel.collectBaseSideEffect {

    }

    TimelinePageScreen(
        pagingItems = pagingItems,
        onActionEvent = viewModel::onEvent,
        onUiEvent = { event ->
            if (event is TimelinePageEvent.UI.OnNavScreen) onNavScreen(event.screen)
        },
    )
}

@Composable
private fun TimelinePageScreen(
    pagingItems: LazyPagingItems<ComposeTimeline>,
    onUiEvent: (TimelinePageEvent.UI) -> Unit,
    onActionEvent: (TimelinePageEvent.Action) -> Unit,
) {
    StateLazyColumn(
        modifier = Modifier.fillMaxSize(),
        pagingItems = pagingItems,
        state = rememberCacheWindowLazyListState(),
        showScrollUpBtn = true,
        key = { item, _ -> item.id },
        contentType = { CONTENT_TYPE_TIMELINE }
    ) { item, _ ->
        TimelinePageItem(
            modifier = Modifier.fillMaxWidth(),
            item = item,
            onNavigate = { onUiEvent(TimelinePageEvent.UI.OnNavScreen(it)) },
            onReactionClick = { timeline, reaction ->
                onActionEvent(TimelinePageEvent.Action.OnClickRecation(timeline, reaction))
            },
            onDeleteClick = { onActionEvent(TimelinePageEvent.Action.OnDeleteTimeline(it)) },
        )
        HorizontalDivider()
    }
}
