package com.xiaoyv.bangumi.features.preivew.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.panpf.zoomimage.CoilZoomAsyncImage
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_image
import com.xiaoyv.bangumi.features.preivew.main.business.PreviewMainEvent
import com.xiaoyv.bangumi.features.preivew.main.business.PreviewMainState
import com.xiaoyv.bangumi.features.preivew.main.business.PreviewMainViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun PreviewMainRoute(
    viewModel: PreviewMainViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    PreviewMainScreen(
        baseState = baseState,
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
    baseState: BaseState<PreviewMainState>,
    onUiEvent: (PreviewMainEvent.UI) -> Unit,
    onActionEvent: (PreviewMainEvent.Action) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = baseState.content { "${index + 1}/${items.size}" },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.surface,
                ),
                onNavigationClick = { onUiEvent(PreviewMainEvent.UI.OnNavUp) }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            onRefresh = { onActionEvent(PreviewMainEvent.Action.OnRefresh(it)) },
            baseState = baseState,
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
    ) {
        CoilZoomAsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = state.items[it],
            contentDescription = stringResource(Res.string.global_image),
            onTap = { onUiEvent(PreviewMainEvent.UI.OnNavUp) }
        )
    }
}

