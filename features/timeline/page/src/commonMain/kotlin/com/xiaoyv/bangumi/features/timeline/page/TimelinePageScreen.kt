package com.xiaoyv.bangumi.features.timeline.page

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import com.xiaoyv.bangumi.features.timeline.page.business.TimelinePageEvent
import com.xiaoyv.bangumi.features.timeline.page.business.TimelinePageState
import com.xiaoyv.bangumi.features.timeline.page.business.TimelinePageViewModel
import com.xiaoyv.bangumi.features.timeline.page.business.koinTimelinePageViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.data.model.request.list.timeline.ListTimelineParam
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun TimelinePageRoute(
    param: ListTimelineParam,
    onNavScreen: (Screen) -> Unit,
) {
    if (LocalInspectionMode.current) return
    val viewModel: TimelinePageViewModel = koinTimelinePageViewModel(param)
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    TimelinePageScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is TimelinePageEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun TimelinePageScreen(
    baseState: BaseState<TimelinePageState>,
    onUiEvent: (TimelinePageEvent.UI) -> Unit,
    onActionEvent: (TimelinePageEvent.Action) -> Unit,
) {
    StateLayout(
        modifier = Modifier.fillMaxSize(),
        onRefresh = { onActionEvent(TimelinePageEvent.Action.OnRefresh(it)) },
        baseState = baseState,
    ) { state ->
        TimelinePageScreenContent(state, onUiEvent, onActionEvent)
    }
}


@Composable
private fun TimelinePageScreenContent(
    state: TimelinePageState,
    onUiEvent: (TimelinePageEvent.UI) -> Unit,
    onActionEvent: (TimelinePageEvent.Action) -> Unit,
) {

}

