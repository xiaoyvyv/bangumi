package com.xiaoyv.bangumi.features.timeline.add

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.timeline_add
import com.xiaoyv.bangumi.features.timeline.add.business.TimelineAddEvent
import com.xiaoyv.bangumi.features.timeline.add.business.TimelineAddState
import com.xiaoyv.bangumi.features.timeline.add.business.TimelineAddViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun TimelineAddRoute(
    viewModel: TimelineAddViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val uiState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    TimelineAddScreen(
        uiState = uiState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is TimelineAddEvent.UI.OnNavUp -> onNavUp()
                is TimelineAddEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun TimelineAddScreen(
    uiState: UiState<TimelineAddState>,
    onUiEvent: (TimelineAddEvent.UI) -> Unit,
    onActionEvent: (TimelineAddEvent.Action) -> Unit
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = stringResource(Res.string.timeline_add),
                onNavigationClick = { onUiEvent(TimelineAddEvent.UI.OnNavUp) }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = { loading -> onActionEvent(TimelineAddEvent.Action.OnRefresh(loading)) },
            uiState = uiState,
        ) { state ->
            TimelineAddScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun TimelineAddScreenContent(
    state: TimelineAddState,
    onUiEvent: (TimelineAddEvent.UI) -> Unit,
    onActionEvent: (TimelineAddEvent.Action) -> Unit
) {

}

