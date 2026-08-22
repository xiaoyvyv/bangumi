package com.xiaoyv.bangumi.features.pixiv.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_pixiv
import com.xiaoyv.bangumi.features.pixiv.main.business.PixivMainEvent
import com.xiaoyv.bangumi.features.pixiv.main.business.PixivMainState
import com.xiaoyv.bangumi.features.pixiv.main.business.PixivMainViewModel
import com.xiaoyv.bangumi.features.pixiv.main.page.PixivMainPageScreen
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmTabHorizontalPager
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun PixivMainRoute(
    viewModel: PixivMainViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    PixivMainScreen(
        uiState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is PixivMainEvent.UI.OnNavUp -> onNavUp()
                is PixivMainEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun PixivMainScreen(
    uiState: UiState<PixivMainState>,
    onUiEvent: (PixivMainEvent.UI) -> Unit,
    onActionEvent: (PixivMainEvent.Action) -> Unit,
) {
    StateLayout(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
    ) { state ->
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                BgmTopAppBar(
                    title = stringResource(Res.string.global_pixiv),
                    onNavigationClick = { onUiEvent(PixivMainEvent.UI.OnNavUp) }
                )
            }
        ) { paddingValues ->
            PixivMainScreenContent(
                modifier = Modifier.padding(paddingValues),
                state = state,
                onUiEvent = onUiEvent,
                onActionEvent = onActionEvent
            )
        }
    }
}

@Composable
private fun PixivMainScreenContent(
    modifier: Modifier,
    state: PixivMainState,
    onUiEvent: (PixivMainEvent.UI) -> Unit,
    onActionEvent: (PixivMainEvent.Action) -> Unit,
) {
    BgmTabHorizontalPager(
        modifier = modifier.fillMaxSize(),
        tabs = state.tabs
    ) { page ->
        PixivMainPageScreen(
            content = state.tabs[page].type,
            onUiEvent = onUiEvent,
            onActionEvent = onActionEvent
        )
    }
}
