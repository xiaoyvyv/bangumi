package com.xiaoyv.bangumi.features.settings.block

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.settings_block_user
import com.xiaoyv.bangumi.features.friend.FriendRoute
import com.xiaoyv.bangumi.features.settings.block.business.SettingsBlockEvent
import com.xiaoyv.bangumi.features.settings.block.business.SettingsBlockState
import com.xiaoyv.bangumi.features.settings.block.business.SettingsBlockViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.types.list.ListUserType
import com.xiaoyv.bangumi.shared.data.manager.shared.currentUser
import com.xiaoyv.bangumi.shared.data.model.request.list.user.ListUserParam
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun SettingsBlockRoute(
    viewModel: SettingsBlockViewModel = koinViewModel<SettingsBlockViewModel>(),
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    SettingsBlockScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is SettingsBlockEvent.UI.OnNavUp -> onNavUp()
                is SettingsBlockEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun SettingsBlockScreen(
    baseState: BaseState<SettingsBlockState>,
    onUiEvent: (SettingsBlockEvent.UI) -> Unit,
    onActionEvent: (SettingsBlockEvent.Action) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BgmTopAppBar(
                title = stringResource(Res.string.settings_block_user),
                onNavigationClick = { onUiEvent(SettingsBlockEvent.UI.OnNavUp) }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            baseState = baseState,
        ) { state ->
            SettingsBlockScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun SettingsBlockScreenContent(
    state: SettingsBlockState,
    onUiEvent: (SettingsBlockEvent.UI) -> Unit,
    onActionEvent: (SettingsBlockEvent.Action) -> Unit,
) {
    val user = currentUser()
    FriendRoute(
        param = remember {
            ListUserParam(
                type = ListUserType.USER_BLOCKLIST,
                username = user.username,
            )
        },
        onNavScreen = {
            onUiEvent(SettingsBlockEvent.UI.OnNavScreen(it))
        }
    )
}

