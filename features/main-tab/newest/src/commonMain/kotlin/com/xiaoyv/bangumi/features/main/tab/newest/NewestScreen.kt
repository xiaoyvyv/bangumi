package com.xiaoyv.bangumi.features.main.tab.newest

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.yuc_title
import com.xiaoyv.bangumi.features.main.tab.newest.business.NewestEvent
import com.xiaoyv.bangumi.features.main.tab.newest.business.NewestState
import com.xiaoyv.bangumi.features.main.tab.newest.business.NewestViewModel
import com.xiaoyv.bangumi.features.subject.page.SubjectPageRoute
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.types.SubjectSortBrowserType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.types.list.ListSubjectType
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.ListSubjectParam
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.SubjectBrowserBody
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmTabHorizontalPager
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun NewestRoute(
    viewModel: NewestViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    NewestScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is NewestEvent.UI.OnNavUp -> onNavUp()
                is NewestEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun NewestScreen(
    baseState: BaseState<NewestState>,
    onUiEvent: (NewestEvent.UI) -> Unit,
    onActionEvent: (NewestEvent.Action) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = baseState.payload.let {
                    if (it == null) stringResource(Res.string.yuc_title) else stringResource(Res.string.yuc_title) + "（${it.year}）"
                },
                onNavigationClick = { onUiEvent(NewestEvent.UI.OnNavUp) }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = { onActionEvent(NewestEvent.Action.OnRefresh(it)) },
            baseState = baseState,
        ) { state ->
            NewestScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun NewestScreenContent(
    state: NewestState,
    onUiEvent: (NewestEvent.UI) -> Unit,
    onActionEvent: (NewestEvent.Action) -> Unit,
) {
    BgmTabHorizontalPager(
        modifier = Modifier.fillMaxSize(),
        tabs = state.tabs,
        initialPage = state.defaultMonth - 1
    ) {
        if (!LocalInspectionMode.current) {
            SubjectPageRoute(
                param = remember {
                    ListSubjectParam(
                        type = ListSubjectType.BROWSER,
                        browser = SubjectBrowserBody(
                            subjectType = SubjectType.ANIME,
                            sort = SubjectSortBrowserType.DATE,
                            year = state.year,
                            month = state.tabs[it].type
                        )
                    )
                },
                onNavScreen = { screen -> onUiEvent(NewestEvent.UI.OnNavScreen(screen)) },
            )
        }
    }
}

