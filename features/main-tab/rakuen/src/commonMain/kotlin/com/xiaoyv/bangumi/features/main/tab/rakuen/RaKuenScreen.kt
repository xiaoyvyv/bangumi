package com.xiaoyv.bangumi.features.main.tab.rakuen

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
import com.xiaoyv.bangumi.features.main.tab.rakuen.business.RaKuenEvent
import com.xiaoyv.bangumi.features.main.tab.rakuen.business.RaKuenState
import com.xiaoyv.bangumi.features.main.tab.rakuen.business.RaKuenViewModel
import com.xiaoyv.bangumi.features.main.tab.rakuen.page.RaKuenPageScreen
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuActionButton
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmTabHorizontalPager
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun RaKuenRoute(
    viewModel: RaKuenViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    RaKuenScreen(
        uiState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is RaKuenEvent.UI.OnNavUp -> onNavUp()
                is RaKuenEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun RaKuenScreen(
    uiState: UiState<RaKuenState>,
    onUiEvent: (RaKuenEvent.UI) -> Unit,
    onActionEvent: (RaKuenEvent.Action) -> Unit,
) {
    StateLayout(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
    ) { state ->
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                BgmTopAppBar(
                    title = stringResource(Res.string.global_community),
                    onNavigationClick = { onUiEvent(RaKuenEvent.UI.OnNavUp) },
                    actions = {
                        IconButton(onClick = { onUiEvent(RaKuenEvent.UI.OnNavScreen(Screen.SearchInput())) }) {
                            Icon(
                                BgmIcons.Search,
                                contentDescription = null
                            )
                        }
                        DropMenuActionButton(
                            options = state.actions,
                            onOptionClick = {
                                when (it.type) {
                                    0 -> {
//                                        onActionEvent(RaKuenEvent.Action.OnChangeType(it.type))
                                    }

                                    1 -> {
//                                        onActionEvent(RaKuenEvent.Action.OnChangeType(it.type))
                                    }
                                }
                            }
                        )
                    }
                )
            }
        ) {
            RaKuenScreenContent(
                modifier = Modifier.padding(it),
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )
        }
    }
}


@Composable
private fun RaKuenScreenContent(
    modifier: Modifier,
    state: RaKuenState,
    onUiEvent: (RaKuenEvent.UI) -> Unit,
    onActionEvent: (RaKuenEvent.Action) -> Unit,
) {
    BgmTabHorizontalPager(
        modifier = modifier.fillMaxSize(),
        tabs = state.tabs
    ) {
        RaKuenPageScreen(
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
        RaKuenScreen(
            uiState = UiState(RaKuenState()),
            onActionEvent = {},
            onUiEvent = {}
        )
    }
}

