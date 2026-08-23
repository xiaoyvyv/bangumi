package com.xiaoyv.bangumi.features.pixiv.user.setting

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_settings
import com.xiaoyv.bangumi.features.pixiv.user.setting.business.PixivUserSettingEvent
import com.xiaoyv.bangumi.features.pixiv.user.setting.business.PixivUserSettingState
import com.xiaoyv.bangumi.features.pixiv.user.setting.business.PixivUserSettingViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun PixivUserSettingRoute(
    viewModel: PixivUserSettingViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    PixivUserSettingScreen(
        uiState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is PixivUserSettingEvent.UI.OnNavUp -> onNavUp()
                is PixivUserSettingEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun PixivUserSettingScreen(
    uiState: UiState<PixivUserSettingState>,
    onUiEvent: (PixivUserSettingEvent.UI) -> Unit,
    onActionEvent: (PixivUserSettingEvent.Action) -> Unit,
) {
    StateLayout(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        onRefresh = { onActionEvent(PixivUserSettingEvent.Action.OnRefresh(loading = true)) }
    ) { state ->
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                BgmTopAppBar(
                    title = stringResource(Res.string.global_settings),
                    onNavigationClick = { onUiEvent(PixivUserSettingEvent.UI.OnNavUp) }
                )
            }
        ) { paddingValues ->
            PixivUserSettingScreenContent(
                modifier = Modifier.padding(paddingValues),
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )
        }
    }
}

@Composable
private fun PixivUserSettingScreenContent(
    modifier: Modifier,
    state: PixivUserSettingState,
    onUiEvent: (PixivUserSettingEvent.UI) -> Unit,
    onActionEvent: (PixivUserSettingEvent.Action) -> Unit,
) {

}

@Composable
@Preview
private fun PreviewPixivUserSettingScreen() {
    PreviewColumn(modifier = Modifier.fillMaxSize()) {
        PixivUserSettingScreen(
            uiState = UiState(PixivUserSettingState()),
            onUiEvent = {},
            onActionEvent = {}
        )
    }
}
