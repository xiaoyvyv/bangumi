package com.xiaoyv.bangumi.features.timeline.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.login_title
import com.xiaoyv.bangumi.features.timeline.detail.business.TimelineDetailEvent
import com.xiaoyv.bangumi.features.timeline.detail.business.TimelineDetailState
import com.xiaoyv.bangumi.features.timeline.detail.business.TimelineDetailViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun TimelineDetailRoute(
    viewModel: TimelineDetailViewModel = koinViewModel<TimelineDetailViewModel>(),
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    TimelineDetailScreen(
        baseState = baseState,
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
    baseState: BaseState<TimelineDetailState>,
    onUiEvent: (TimelineDetailEvent.UI) -> Unit,
    onActionEvent: (TimelineDetailEvent.Action) -> Unit,
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
            onRefresh = { onActionEvent(TimelineDetailEvent.Action.OnRefresh(it)) },
            baseState = baseState,
        ) { state ->
            TimelineDetailScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun TimelineDetailScreenContent(
    state: TimelineDetailState,
    onUiEvent: (TimelineDetailEvent.UI) -> Unit,
    onActionEvent: (TimelineDetailEvent.Action) -> Unit,
) {

}

