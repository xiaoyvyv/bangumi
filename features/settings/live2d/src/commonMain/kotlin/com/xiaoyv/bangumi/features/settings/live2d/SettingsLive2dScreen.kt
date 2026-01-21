package com.xiaoyv.bangumi.features.settings.live2d

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.settings_live2d
import com.xiaoyv.bangumi.core_resource.resources.settings_live2d_voice
import com.xiaoyv.bangumi.features.settings.live2d.business.SettingsLive2dEvent
import com.xiaoyv.bangumi.features.settings.live2d.business.SettingsLive2dState
import com.xiaoyv.bangumi.features.settings.live2d.business.SettingsLive2dViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.data.manager.shared.currentSettings
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmLargeTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingContainer
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingSwitchItem
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun SettingsLive2dRoute(
    viewModel: SettingsLive2dViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    SettingsLive2dScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is SettingsLive2dEvent.UI.OnNavUp -> onNavUp()
                is SettingsLive2dEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun SettingsLive2dScreen(
    baseState: BaseState<SettingsLive2dState>,
    onUiEvent: (SettingsLive2dEvent.UI) -> Unit,
    onActionEvent: (SettingsLive2dEvent.Action) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            BgmLargeTopAppBar(
                title = stringResource(Res.string.settings_live2d),
                scrollBehavior = scrollBehavior,
                onNavigationClick = { onUiEvent(SettingsLive2dEvent.UI.OnNavUp) }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(it),
            baseState = baseState,
        ) { state ->
            SettingsLive2dScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun SettingsLive2dScreenContent(
    state: SettingsLive2dState,
    onUiEvent: (SettingsLive2dEvent.UI) -> Unit,
    onActionEvent: (SettingsLive2dEvent.Action) -> Unit,
) {
    val settings = currentSettings()

    Column(modifier = Modifier.padding(vertical = 24.dp)) {
        SettingContainer(label = { Text(text = stringResource(Res.string.settings_live2d)) }) {
            SettingSwitchItem(
                title = stringResource(Res.string.settings_live2d),
                value = settings.live2d.enable,
                onValueChange = {
                    onActionEvent(SettingsLive2dEvent.Action.OnUpdate(settings.live2d.copy(enable = it)))
                }
            )
            SettingSwitchItem(
                title = stringResource(Res.string.settings_live2d_voice),
                value = settings.live2d.voiceEnable,
                onValueChange = {
                    onActionEvent(SettingsLive2dEvent.Action.OnUpdate(settings.live2d.copy(voiceEnable = it)))
                }
            )
        }
    }
}

