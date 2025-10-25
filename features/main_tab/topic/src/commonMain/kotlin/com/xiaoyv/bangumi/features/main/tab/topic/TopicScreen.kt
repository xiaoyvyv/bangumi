package com.xiaoyv.bangumi.features.main.tab.topic

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_community
import com.xiaoyv.bangumi.features.main.tab.topic.business.TopicEvent
import com.xiaoyv.bangumi.features.main.tab.topic.business.TopicState
import com.xiaoyv.bangumi.features.main.tab.topic.business.TopicViewModel
import com.xiaoyv.bangumi.features.main.tab.topic.page.TopicPageScreen
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuActionButton
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmTabHorizontalPager
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun TopicRoute(
    viewModel: TopicViewModel = koinViewModel<TopicViewModel>(),
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    TopicScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is TopicEvent.UI.OnNavUp -> onNavUp()
                is TopicEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun TopicScreen(
    baseState: BaseState<TopicState>,
    onUiEvent: (TopicEvent.UI) -> Unit,
    onActionEvent: (TopicEvent.Action) -> Unit,
) {
    StateLayout(
        modifier = Modifier.fillMaxSize(),
        baseState = baseState,
    ) { state ->
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                BgmTopAppBar(
                    title = stringResource(Res.string.global_community),
                    onNavigationClick = { onUiEvent(TopicEvent.UI.OnNavUp) },
                    actions = {
                        IconButton(onClick = { onUiEvent(TopicEvent.UI.OnNavScreen(Screen.SearchInput())) }) {
                            Icon(
                                BgmIcons.Search,
                                contentDescription = null
                            )
                        }
                        DropMenuActionButton(
                            options = state.actions,
                            onOptionClick = {
                                onActionEvent(TopicEvent.Action.OnChangeType(it.type))
                            }
                        )
                    }
                )
            }
        ) {
            TopicScreenContent(
                modifier = Modifier.padding(it),
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )
        }
    }
}


@Composable
private fun TopicScreenContent(
    modifier: Modifier,
    state: TopicState,
    onUiEvent: (TopicEvent.UI) -> Unit,
    onActionEvent: (TopicEvent.Action) -> Unit,
) {
    BgmTabHorizontalPager(
        modifier = modifier.fillMaxSize(),
        tabs = state.tabs
    ) {
        TopicPageScreen(
            type = state.tabs[it].type,
            onUiEvent = onUiEvent,
            onActionEvent = onActionEvent
        )
    }
}

@Preview
@Composable
fun Test() {
    PreviewColumn {
        TopicScreen(
            baseState = BaseState.Success(TopicState()),
            onActionEvent = {},
            onUiEvent = {}
        )
    }
}

