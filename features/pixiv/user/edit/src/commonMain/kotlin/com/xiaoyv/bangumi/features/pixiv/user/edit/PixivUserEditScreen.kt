package com.xiaoyv.bangumi.features.pixiv.user.edit

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_edit
import com.xiaoyv.bangumi.features.pixiv.user.edit.business.PixivUserEditEvent
import com.xiaoyv.bangumi.features.pixiv.user.edit.business.PixivUserEditState
import com.xiaoyv.bangumi.features.pixiv.user.edit.business.PixivUserEditViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun PixivUserEditRoute(
    viewModel: PixivUserEditViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    PixivUserEditScreen(
        uiState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is PixivUserEditEvent.UI.OnNavUp -> onNavUp()
                is PixivUserEditEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun PixivUserEditScreen(
    uiState: UiState<PixivUserEditState>,
    onUiEvent: (PixivUserEditEvent.UI) -> Unit,
    onActionEvent: (PixivUserEditEvent.Action) -> Unit,
) {
    StateLayout(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        onRefresh = { onActionEvent(PixivUserEditEvent.Action.OnRefresh(loading = true)) }
    ) { state ->
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                BgmTopAppBar(
                    title = stringResource(Res.string.global_edit),
                    onNavigationClick = { onUiEvent(PixivUserEditEvent.UI.OnNavUp) }
                )
            }
        ) { paddingValues ->
            PixivUserEditScreenContent(
                modifier = Modifier.padding(paddingValues),
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )
        }
    }
}

@Composable
private fun PixivUserEditScreenContent(
    modifier: Modifier,
    state: PixivUserEditState,
    onUiEvent: (PixivUserEditEvent.UI) -> Unit,
    onActionEvent: (PixivUserEditEvent.Action) -> Unit,
) {

}

@Composable
@Preview
private fun PreviewPixivUserEditScreen() {
    PreviewColumn(modifier = Modifier.fillMaxSize()) {
        PixivUserEditScreen(
            uiState = UiState(PixivUserEditState()),
            onUiEvent = {},
            onActionEvent = {}
        )
    }
}
