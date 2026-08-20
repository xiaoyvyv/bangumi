package com.xiaoyv.bangumi.features.timeline.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.login_title
import com.xiaoyv.bangumi.features.timeline.detail.business.TimelineDetailEvent
import com.xiaoyv.bangumi.features.timeline.detail.business.TimelineDetailState
import com.xiaoyv.bangumi.features.timeline.detail.business.TimelineDetailViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun TimelineDetailRoute(
    viewModel: TimelineDetailViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val uiState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    TimelineDetailScreen(
        uiState = uiState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is TimelineDetailEvent.UI.OnNavUp -> onNavUp()
                is TimelineDetailEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun TimelineDetailScreen(
    uiState: UiState<TimelineDetailState>,
    onUiEvent: (TimelineDetailEvent.UI) -> Unit,
    onActionEvent: (TimelineDetailEvent.Action) -> Unit
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = stringResource(Res.string.login_title),
                onNavigationClick = { onUiEvent(TimelineDetailEvent.UI.OnNavUp) }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = { loading -> onActionEvent(TimelineDetailEvent.Action.OnRefresh(loading)) },
            uiState = uiState,
        ) { state ->
            TimelineDetailScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun TimelineDetailScreenContent(
    state: TimelineDetailState,
    onUiEvent: (TimelineDetailEvent.UI) -> Unit,
    onActionEvent: (TimelineDetailEvent.Action) -> Unit
) {

}


@Preview
@Composable
private fun PreviewTimelineDetailScreenScreen() {
    PreviewColumn(modifier = Modifier.fillMaxSize()) {
        TimelineDetailScreen(
            uiState = UiState(
                TimelineDetailState()
            ),
            onUiEvent = {},
            onActionEvent = {}
        )
    }
}

