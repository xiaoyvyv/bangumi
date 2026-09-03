package com.xiaoyv.bangumi.features.preivew.video

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.app_name
import com.xiaoyv.bangumi.features.preivew.video.business.PreviewVideoEvent
import com.xiaoyv.bangumi.features.preivew.video.business.PreviewVideoState
import com.xiaoyv.bangumi.features.preivew.video.business.PreviewVideoViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.platform.video.VideoScaffold
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import org.jetbrains.compose.resources.stringResource
import org.openani.mediamp.compose.rememberMediampPlayer
import org.openani.mediamp.playUri
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun PreviewVideoRoute(
    viewModel: PreviewVideoViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val uiState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {}

    PreviewVideoScreen(
        uiState = uiState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is PreviewVideoEvent.UI.OnNavUp -> onNavUp()
                is PreviewVideoEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun PreviewVideoScreen(
    uiState: UiState<PreviewVideoState>,
    onUiEvent: (PreviewVideoEvent.UI) -> Unit,
    onActionEvent: (PreviewVideoEvent.Action) -> Unit
) {
    StateLayout(
        modifier = Modifier.fillMaxSize(),
        onRefresh = { loading -> onActionEvent(PreviewVideoEvent.Action.OnRefresh(loading)) },
        uiState = uiState,
    ) { state ->
        PreviewVideoScreenContent(state, onUiEvent, onActionEvent)
    }
}

@Composable
private fun PreviewVideoScreenContent(
    state: PreviewVideoState,
    onUiEvent: (PreviewVideoEvent.UI) -> Unit,
    onActionEvent: (PreviewVideoEvent.Action) -> Unit
) {
    val player = rememberMediampPlayer()

    LaunchedEffect(state.url) {
        if (state.url.isNotBlank()) {
            player.playUri(state.url)
        }
    }

    VideoScaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        player = player,
        title = stringResource(Res.string.app_name),
        onNavUp = { onUiEvent(PreviewVideoEvent.UI.OnNavUp) },
        onRetry = { player.playUri(state.url) },
    )
}

@Composable
@Preview
private fun PreviewPreviewVideoScreen() {
    PreviewColumn(modifier = Modifier.fillMaxSize()) {
        PreviewVideoScreen(
            uiState = UiState(
                PreviewVideoState("")
            ),
            onUiEvent = {},
            onActionEvent = {}
        )
    }
}
