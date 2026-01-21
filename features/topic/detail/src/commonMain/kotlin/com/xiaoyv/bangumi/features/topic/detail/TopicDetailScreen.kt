package com.xiaoyv.bangumi.features.topic.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.login_title
import com.xiaoyv.bangumi.features.topic.detail.business.TopicDetailEvent
import com.xiaoyv.bangumi.features.topic.detail.business.TopicDetailState
import com.xiaoyv.bangumi.features.topic.detail.business.TopicDetailViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun TopicDetailRoute(
    viewModel: TopicDetailViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    TopicDetailScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is TopicDetailEvent.UI.OnNavUp -> onNavUp()
                is TopicDetailEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun TopicDetailScreen(
    baseState: BaseState<TopicDetailState>,
    onUiEvent: (TopicDetailEvent.UI) -> Unit,
    onActionEvent: (TopicDetailEvent.Action) -> Unit,
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = stringResource(Res.string.login_title),
                onNavigationClick = { onUiEvent(TopicDetailEvent.UI.OnNavUp) }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = { onActionEvent(TopicDetailEvent.Action.OnRefresh(it)) },
            baseState = baseState,
        ) { state ->
            TopicDetailScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun TopicDetailScreenContent(
    state: TopicDetailState,
    onUiEvent: (TopicDetailEvent.UI) -> Unit,
    onActionEvent: (TopicDetailEvent.Action) -> Unit,
) {

}

