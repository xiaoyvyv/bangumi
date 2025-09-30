package com.xiaoyv.bangumi.features.main.tab.tracking

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_search
import com.xiaoyv.bangumi.core_resource.resources.title_process
import com.xiaoyv.bangumi.features.main.tab.tracking.business.TrackingEvent
import com.xiaoyv.bangumi.features.main.tab.tracking.business.TrackingState
import com.xiaoyv.bangumi.features.main.tab.tracking.business.TrackingViewModel
import com.xiaoyv.bangumi.features.main.tab.tracking.page.TrackingPageScreen
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmTabHorizontalPager
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun TrackingRoute(
    viewModel: TrackingViewModel = koinViewModel<TrackingViewModel>(),
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()
    viewModel.collectBaseSideEffect { }

    TrackingScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is TrackingEvent.UI.OnNavUp -> onNavUp()
                is TrackingEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun TrackingScreen(
    baseState: BaseState<TrackingState>,
    onUiEvent: (TrackingEvent.UI) -> Unit,
    onActionEvent: (TrackingEvent.Action) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = stringResource(Res.string.title_process),
                actions = {
                    IconButton(onClick = { onUiEvent(TrackingEvent.UI.OnNavScreen(Screen.SearchInput())) }) {
                        Icon(
                            imageVector = BgmIcons.Search,
                            contentDescription = stringResource(Res.string.global_search)
                        )
                    }
                },
                onNavigationClick = { onUiEvent(TrackingEvent.UI.OnNavUp) }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            baseState = baseState,
        ) { state ->
            TrackingScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun TrackingScreenContent(
    state: TrackingState,
    onUiEvent: (TrackingEvent.UI) -> Unit,
    onActionEvent: (TrackingEvent.Action) -> Unit,
) {
    BgmTabHorizontalPager(
        modifier = Modifier.fillMaxSize(),
        tabs = state.tabs
    ) {
        if (!LocalInspectionMode.current) {
            TrackingPageScreen(
                subjectType = state.tabs[it].type,
                onUiEvent = onUiEvent,
            )
        }
    }
}

@Preview
@Composable
private fun PreviewTrackingScreen() {
    PreviewColumn(modifier = Modifier.fillMaxSize()) {
        TrackingScreen(
            baseState = BaseState.Success(
                TrackingState(

                )
            ),
            onActionEvent = {},
            onUiEvent = {}
        )
    }
}

