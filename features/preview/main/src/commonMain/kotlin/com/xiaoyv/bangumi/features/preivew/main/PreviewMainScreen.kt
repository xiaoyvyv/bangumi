package com.xiaoyv.bangumi.features.preivew.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import com.github.panpf.zoomimage.CoilZoomAsyncImage
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_image
import com.xiaoyv.bangumi.features.preivew.main.business.PreviewMainEvent
import com.xiaoyv.bangumi.features.preivew.main.business.PreviewMainSideEffect
import com.xiaoyv.bangumi.features.preivew.main.business.PreviewMainState
import com.xiaoyv.bangumi.features.preivew.main.business.PreviewMainViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.ui.component.action.LocalActionHandler
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.chip.DropMenuActionButton
import com.xiaoyv.bangumi.shared.ui.component.layout.state.BgmProgressIndicator
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun PreviewMainRoute(
    viewModel: PreviewMainViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()
    val actionHandler = LocalActionHandler.current

    viewModel.collectBaseSideEffect { effect ->
        when (effect) {
            is PreviewMainSideEffect.OnShareMedia -> actionHandler.shareImage(effect.file)
        }
    }

    PreviewMainScreen(
        uiState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is PreviewMainEvent.UI.OnNavUp -> onNavUp()
                is PreviewMainEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun PreviewMainScreen(
    uiState: UiState<PreviewMainState>,
    onUiEvent: (PreviewMainEvent.UI) -> Unit,
    onActionEvent: (PreviewMainEvent.Action) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = uiState.data.title,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.surface,
                ),
                actions = {
                    DropMenuActionButton(
                        options = uiState.data.contextMenus,
                        imageTint = MaterialTheme.colorScheme.surface,
                        onOptionClick = { tab ->
                            when (tab.type) {
                                0 -> onActionEvent(PreviewMainEvent.Action.OnSaveMedia)
                                1 -> onActionEvent(PreviewMainEvent.Action.OnShareMedia)
                                2 -> onActionEvent(PreviewMainEvent.Action.OnSetWallpaper)
                            }
                        }
                    )
                },
                onNavigationClick = { onUiEvent(PreviewMainEvent.UI.OnNavUp) }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            onRefresh = { onActionEvent(PreviewMainEvent.Action.OnRefresh(it)) },
            uiState = uiState,
        ) { state ->
            PreviewMainScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun PreviewMainScreenContent(
    state: PreviewMainState,
    onUiEvent: (PreviewMainEvent.UI) -> Unit,
    onActionEvent: (PreviewMainEvent.Action) -> Unit,
) {
    val pagerState = rememberPagerState(state.index) {
        state.items.size
    }

    LaunchedEffect(Unit) {
        snapshotFlow { pagerState.currentPage }
            .collect {
                onActionEvent(PreviewMainEvent.Action.OnPageSelected(it))
            }
    }

    if (state.items.isNotEmpty()) HorizontalPager(
        modifier = Modifier.fillMaxSize(),
        state = pagerState
    ) { page ->
        Box(Modifier.fillMaxSize()) {
            var isLoading by remember { mutableStateOf(true) }

            CoilZoomAsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = state.items[page],
                contentDescription = stringResource(Res.string.global_image),
                onState = { painterState ->
                    isLoading = painterState is AsyncImagePainter.State.Loading
                },
                onTap = { onUiEvent(PreviewMainEvent.UI.OnNavUp) }
            )

            if (isLoading) {
                BgmProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(3.dp),
                    strokeWidth = 2.dp
                )
            }
        }
    }
}


@Composable
@Preview
private fun PreviewMainScreen() {
    PreviewColumn(modifier = Modifier.fillMaxSize()) {
        PreviewMainScreen(
            uiState = UiState(PreviewMainState(0, title = "1/1")),
            onUiEvent = { },
            onActionEvent = {}
        )
    }
}


