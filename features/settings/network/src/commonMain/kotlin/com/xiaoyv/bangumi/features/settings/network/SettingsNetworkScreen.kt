package com.xiaoyv.bangumi.features.settings.network

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
import com.xiaoyv.bangumi.core_resource.resources.global_domain
import com.xiaoyv.bangumi.core_resource.resources.global_pixiv
import com.xiaoyv.bangumi.core_resource.resources.global_req_timeout
import com.xiaoyv.bangumi.core_resource.resources.global_update
import com.xiaoyv.bangumi.core_resource.resources.settings_domain_bgm
import com.xiaoyv.bangumi.core_resource.resources.settings_domain_pixiv
import com.xiaoyv.bangumi.core_resource.resources.settings_dou_ban
import com.xiaoyv.bangumi.core_resource.resources.settings_network
import com.xiaoyv.bangumi.core_resource.resources.settings_timeout_request
import com.xiaoyv.bangumi.core_resource.resources.settings_timeout_socket
import com.xiaoyv.bangumi.core_resource.resources.settings_update_channel
import com.xiaoyv.bangumi.features.settings.network.business.SettingsNetworkEvent
import com.xiaoyv.bangumi.features.settings.network.business.SettingsNetworkState
import com.xiaoyv.bangumi.features.settings.network.business.SettingsNetworkViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.types.settings.SettingUpdateChannel
import com.xiaoyv.bangumi.shared.data.manager.shared.currentSettings
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmLargeTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingContainer
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingInputItem
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingOptionItem
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun SettingsNetworkRoute(
    viewModel: SettingsNetworkViewModel = koinViewModel<SettingsNetworkViewModel>(),
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    SettingsNetworkScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is SettingsNetworkEvent.UI.OnNavUp -> onNavUp()
                is SettingsNetworkEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun SettingsNetworkScreen(
    baseState: BaseState<SettingsNetworkState>,
    onUiEvent: (SettingsNetworkEvent.UI) -> Unit,
    onActionEvent: (SettingsNetworkEvent.Action) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            BgmLargeTopAppBar(
                title = stringResource(Res.string.settings_network),
                scrollBehavior = scrollBehavior,
                onNavigationClick = { onUiEvent(SettingsNetworkEvent.UI.OnNavUp) }
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
            SettingsNetworkScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun SettingsNetworkScreenContent(
    state: SettingsNetworkState,
    onUiEvent: (SettingsNetworkEvent.UI) -> Unit,
    onActionEvent: (SettingsNetworkEvent.Action) -> Unit,
) {
    val settings = currentSettings()

    Column(modifier = Modifier.padding(vertical = 24.dp)) {
        SettingContainer(label = { Text(text = stringResource(Res.string.global_domain)) }) {
            SettingOptionItem(
                title = stringResource(Res.string.settings_domain_bgm),
                value = settings.network.bgmHost,
                items = TabTokens.settingBangumiHosts,
                onClick = {
                    onActionEvent(SettingsNetworkEvent.Action.OnUpdate(settings.network.copy(bgmHost = it)))
                }
            )
        }

        SettingContainer(label = { Text(text = stringResource(Res.string.global_req_timeout)) }) {
            SettingOptionItem(
                title = stringResource(Res.string.settings_timeout_request),
                value = (settings.network.connectTimeoutMillis / 1000).toString() + "s",
                items = TabTokens.settingTimeoutItems,
                onClick = {
                    onActionEvent(SettingsNetworkEvent.Action.OnUpdate(settings.network.copy(connectTimeoutMillis = it)))
                }
            )
            SettingOptionItem(
                title = stringResource(Res.string.settings_timeout_socket),
                value = (settings.network.socketTimeoutMillis / 1000).toString() + "s",
                items = TabTokens.settingTimeoutItems,
                onClick = {
                    onActionEvent(SettingsNetworkEvent.Action.OnUpdate(settings.network.copy(socketTimeoutMillis = it)))
                }
            )
        }

        SettingContainer(label = { Text(text = stringResource(Res.string.global_pixiv)) }) {
            SettingOptionItem(
                title = stringResource(Res.string.settings_domain_pixiv),
                value = TabTokens.settingPixivImgHosts
                    .find { it.type == settings.network.pixivImageHost }?.displayText()
                    ?: settings.network.pixivImageHost,
                items = TabTokens.settingPixivImgHosts,
                onClick = {
                    onActionEvent(SettingsNetworkEvent.Action.OnUpdate(settings.network.copy(pixivImageHost = it)))
                }
            )

            SettingInputItem(
                title = "Client Id",
                value = settings.network.pixivClientId,
                onClick = {
                    onActionEvent(SettingsNetworkEvent.Action.OnUpdate(settings.network.copy(pixivClientId = it)))
                }
            )

            SettingInputItem(
                title = "Client Secret",
                value = settings.network.pixivClientSecret,
                onClick = {
                    onActionEvent(SettingsNetworkEvent.Action.OnUpdate(settings.network.copy(pixivClientSecret = it)))
                }
            )

            SettingInputItem(
                title = "Version",
                value = settings.network.pixivVersion,
                onClick = {
                    onActionEvent(SettingsNetworkEvent.Action.OnUpdate(settings.network.copy(pixivVersion = it)))
                }
            )

            SettingInputItem(
                title = "Time Hash Secret",
                value = settings.network.pixivTimeHashSecret,
                onClick = {
                    onActionEvent(SettingsNetworkEvent.Action.OnUpdate(settings.network.copy(pixivTimeHashSecret = it)))
                }
            )
        }

        SettingContainer(label = { Text(text = stringResource(Res.string.settings_dou_ban)) }) {
            SettingInputItem(
                title = "Secret",
                value = settings.network.douBanKey,
                onClick = {
                    onActionEvent(SettingsNetworkEvent.Action.OnUpdate(settings.network.copy(douBanKey = it)))
                }
            )

            SettingInputItem(
                title = "UA",
                value = settings.network.douBanUA,
                onClick = {
                    onActionEvent(SettingsNetworkEvent.Action.OnUpdate(settings.network.copy(douBanUA = it)))
                }
            )
        }

        SettingContainer(label = { Text(text = stringResource(Res.string.global_update)) }) {
            SettingOptionItem(
                title = stringResource(Res.string.settings_update_channel),
                value = SettingUpdateChannel.string(settings.network.updateChannel),
                items = TabTokens.settingUpdateChannels,
                onClick = {
                    onActionEvent(SettingsNetworkEvent.Action.OnUpdate(settings.network.copy(updateChannel = it)))
                }
            )
        }
    }
}


