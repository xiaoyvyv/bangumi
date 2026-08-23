package com.xiaoyv.bangumi.features.pixiv.user.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_profile
import com.xiaoyv.bangumi.features.pixiv.user.main.business.PixivUserEvent
import com.xiaoyv.bangumi.features.pixiv.user.main.business.PixivUserState
import com.xiaoyv.bangumi.features.pixiv.user.main.business.PixivUserViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun PixivUserRoute(
    viewModel: PixivUserViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    PixivUserScreen(
        uiState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is PixivUserEvent.UI.OnNavUp -> onNavUp()
                is PixivUserEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun PixivUserScreen(
    uiState: UiState<PixivUserState>,
    onUiEvent: (PixivUserEvent.UI) -> Unit,
    onActionEvent: (PixivUserEvent.Action) -> Unit,
) {
    StateLayout(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        onRefresh = { onActionEvent(PixivUserEvent.Action.OnRefresh(loading = true)) }
    ) { state ->
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                BgmTopAppBar(
                    title = state.userInfo.name,
                    onNavigationClick = { onUiEvent(PixivUserEvent.UI.OnNavUp) }
                )
            }
        ) { paddingValues ->
            PixivUserScreenContent(
                modifier = Modifier.padding(paddingValues),
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )
        }
    }
}

@Composable
private fun PixivUserScreenContent(
    modifier: Modifier,
    state: PixivUserState,
    onUiEvent: (PixivUserEvent.UI) -> Unit,
    onActionEvent: (PixivUserEvent.Action) -> Unit,
) {

}

@Composable
@Preview
private fun PreviewPixivUserScreen() {
    PreviewColumn(modifier = Modifier.fillMaxSize()) {
        PixivUserScreen(
            uiState = UiState(PixivUserState()),
            onUiEvent = {},
            onActionEvent = {}
        )
    }
}
