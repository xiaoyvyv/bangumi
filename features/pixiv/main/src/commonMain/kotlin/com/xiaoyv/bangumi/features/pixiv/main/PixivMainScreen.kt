package com.xiaoyv.bangumi.features.pixiv.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.login_title
import com.xiaoyv.bangumi.features.pixiv.main.business.PixivMainEvent
import com.xiaoyv.bangumi.features.pixiv.main.business.PixivMainState
import com.xiaoyv.bangumi.features.pixiv.main.business.PixivMainViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
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
        baseState = baseState,
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
    baseState: BaseState<PixivMainState>,
    onUiEvent: (PixivMainEvent.UI) -> Unit,
    onActionEvent: (PixivMainEvent.Action) -> Unit,
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = stringResource(Res.string.login_title),
                onNavigationClick = { onUiEvent(PixivMainEvent.UI.OnNavUp) }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = { onActionEvent(PixivMainEvent.Action.OnRefresh(it)) },
            baseState = baseState,
        ) { state ->
            PixivMainScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun PixivMainScreenContent(
    state: PixivMainState,
    onUiEvent: (PixivMainEvent.UI) -> Unit,
    onActionEvent: (PixivMainEvent.Action) -> Unit,
) {

}

