package com.xiaoyv.bangumi.features.main.tab.timeline

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_all
import com.xiaoyv.bangumi.core_resource.resources.global_friend
import com.xiaoyv.bangumi.core_resource.resources.global_self
import com.xiaoyv.bangumi.core_resource.resources.timeline_title
import com.xiaoyv.bangumi.features.main.tab.timeline.business.TimelineEvent
import com.xiaoyv.bangumi.features.main.tab.timeline.business.TimelineState
import com.xiaoyv.bangumi.features.main.tab.timeline.business.TimelineViewModel
import com.xiaoyv.bangumi.features.timeline.page.TimelinePageRoute
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.types.PublishPostType
import com.xiaoyv.bangumi.shared.core.types.TimelineTab
import com.xiaoyv.bangumi.shared.core.types.TimelineTarget
import com.xiaoyv.bangumi.shared.core.types.list.ListTimelineType
import com.xiaoyv.bangumi.shared.data.manager.shared.currentUser
import com.xiaoyv.bangumi.shared.data.model.request.list.timeline.ListTimelineParam
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuActionButton
import com.xiaoyv.bangumi.shared.ui.component.layout.BgmRequireLogin
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmTabHorizontalPager
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens.timelineTabs
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun TimelineRoute(
    viewModel: TimelineViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    TimelineScreen(
        uiState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is TimelineEvent.UI.OnNavUp -> onNavUp()
                is TimelineEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun TimelineScreen(
    uiState: UiState<TimelineState>,
    onUiEvent: (TimelineEvent.UI) -> Unit,
    onActionEvent: (TimelineEvent.Action) -> Unit,
) {
    StateLayout(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
    ) { state ->
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                BgmTopAppBar(
                    title = stringResource(Res.string.timeline_title) + "-" + when (state.selectedMode) {
                        TimelineTab.TIMELINE_ANYONE -> stringResource(Res.string.global_all)
                        TimelineTab.TIMELINE_FRIEND -> stringResource(Res.string.global_friend)
                        TimelineTab.TIMELINE_SELF -> stringResource(Res.string.global_self)
                        else -> stringResource(Res.string.global_all)
                    },
                    onNavigationClick = { onUiEvent(TimelineEvent.UI.OnNavUp) },
                    actions = {
                        IconButton(onClick = { onUiEvent(TimelineEvent.UI.OnNavScreen(Screen.SearchInput())) }) {
                            Icon(BgmIcons.Search, contentDescription = null)
                        }
                        IconButton(onClick = { onUiEvent(TimelineEvent.UI.OnNavScreen(Screen.PublishMain(PublishPostType.TIMELINE_STATUS))) }) {
                            Icon(BgmIcons.Create, contentDescription = null)
                        }
                        DropMenuActionButton(
                            options = uiState.data.actions,
                            onOptionClick = { mode ->
                                onActionEvent(TimelineEvent.Action.OnChangeTimeline(mode.type))
                            }
                        )
                    }
                )
            }
        ) {
            TimelineScreenContent(
                modifier = Modifier.padding(it),
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )
        }
    }
}


@Composable
private fun TimelineScreenContent(
    modifier: Modifier,
    state: TimelineState,
    onUiEvent: (TimelineEvent.UI) -> Unit,
    onActionEvent: (TimelineEvent.Action) -> Unit,
) {
    BgmTabHorizontalPager(
        modifier = modifier.fillMaxSize(),
        tabs = timelineTabs
    ) {
        val cat = timelineTabs[it].type
        val username = currentUser().username
        val tab = state.selectedMode

        BgmRequireLogin(
            modifier = Modifier.fillMaxSize(),
            enable = tab == TimelineTab.TIMELINE_SELF || tab == TimelineTab.TIMELINE_FRIEND
        ) {
            TimelinePageRoute(
                param = remember(tab, cat, username) {
                    ListTimelineParam(
                        type = ListTimelineType.BROWSER,
                        timelineMode = when (tab) {
                            TimelineTab.TIMELINE_ANYONE -> TimelineTarget.WHOLE
                            TimelineTab.TIMELINE_FRIEND -> TimelineTarget.FRIEND
                            TimelineTab.TIMELINE_SELF -> TimelineTarget.USER
                            else -> TimelineTarget.WHOLE
                        },
                        timelineCat = cat,
                        username = username
                    )
                },
                onNavScreen = { screen ->
                    onUiEvent(TimelineEvent.UI.OnNavScreen(screen))
                }
            )
        }
    }
}

@Preview
@Composable
fun Test() {
    PreviewColumn {
        TimelineScreen(
            uiState = UiState(TimelineState()),
            onActionEvent = {},
            onUiEvent = {}
        )
    }
}
