package com.xiaoyv.bangumi.features.settings.live2d

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.settings_live2d
import com.xiaoyv.bangumi.core_resource.resources.settings_live2d_shell
import com.xiaoyv.bangumi.core_resource.resources.settings_live2d_shell_auto
import com.xiaoyv.bangumi.core_resource.resources.settings_live2d_shell_black_musume
import com.xiaoyv.bangumi.core_resource.resources.settings_live2d_shell_musume
import com.xiaoyv.bangumi.core_resource.resources.settings_live2d_size
import com.xiaoyv.bangumi.core_resource.resources.settings_live2d_size_100
import com.xiaoyv.bangumi.core_resource.resources.settings_live2d_size_125
import com.xiaoyv.bangumi.core_resource.resources.settings_live2d_size_150
import com.xiaoyv.bangumi.core_resource.resources.settings_live2d_size_175
import com.xiaoyv.bangumi.core_resource.resources.settings_live2d_size_200
import com.xiaoyv.bangumi.core_resource.resources.settings_live2d_size_50
import com.xiaoyv.bangumi.core_resource.resources.settings_live2d_size_75
import com.xiaoyv.bangumi.features.settings.live2d.business.SettingsLive2dEvent
import com.xiaoyv.bangumi.features.settings.live2d.business.SettingsLive2dState
import com.xiaoyv.bangumi.features.settings.live2d.business.SettingsLive2dViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.data.manager.shared.currentSettings
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmLargeTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingContainer
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingOptionItem
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingSwitchItem
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
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
        uiState = baseState,
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
    uiState: UiState<SettingsLive2dState>,
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
            uiState = uiState,
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

    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        SettingContainer(label = { Text(text = stringResource(Res.string.settings_live2d)) }) {
            SettingSwitchItem(
                title = stringResource(Res.string.settings_live2d),
                shape = ListItemDefaults.segmentedShapes(0, 3),
                value = settings.live2d.enable,
                onValueChange = {
                    onActionEvent(SettingsLive2dEvent.Action.OnUpdate(settings.live2d.copy(enable = it)))
                }
            )
            SettingOptionItem(
                title = stringResource(Res.string.settings_live2d_shell),
                shape = ListItemDefaults.segmentedShapes(1, 3),
                value = stringResource(getLive2dShellStringRes(settings.live2d.shell)),
                items = state.shellItems,
                onClick = {
                    onActionEvent(SettingsLive2dEvent.Action.OnUpdate(settings.live2d.copy(shell = it)))
                }
            )
            SettingOptionItem(
                title = stringResource(Res.string.settings_live2d_size),
                shape = ListItemDefaults.segmentedShapes(2, 3),
                value = stringResource(getLive2dSizeStringRes(settings.live2d.size)),
                items = state.sizeItems,
                onClick = {
                    onActionEvent(SettingsLive2dEvent.Action.OnUpdate(settings.live2d.copy(size = it)))
                }
            )
        }
    }
}

private fun getLive2dShellStringRes(shell: Int): StringResource {
    return when (shell) {
        ComposeSetting.Live2dConfig.Shell.MUSUME -> Res.string.settings_live2d_shell_musume
        ComposeSetting.Live2dConfig.Shell.BLACK_MUSUME -> Res.string.settings_live2d_shell_black_musume
        else -> Res.string.settings_live2d_shell_auto
    }
}

private fun getLive2dSizeStringRes(size: Int): StringResource {
    return when (size) {
        ComposeSetting.Live2dConfig.Size.SIZE_50 -> Res.string.settings_live2d_size_50
        ComposeSetting.Live2dConfig.Size.SIZE_75 -> Res.string.settings_live2d_size_75
        ComposeSetting.Live2dConfig.Size.SIZE_125 -> Res.string.settings_live2d_size_125
        ComposeSetting.Live2dConfig.Size.SIZE_150 -> Res.string.settings_live2d_size_150
        ComposeSetting.Live2dConfig.Size.SIZE_175 -> Res.string.settings_live2d_size_175
        ComposeSetting.Live2dConfig.Size.SIZE_200 -> Res.string.settings_live2d_size_200
        else -> Res.string.settings_live2d_size_100
    }
}
