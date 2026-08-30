package com.xiaoyv.bangumi.features.pixiv.user.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import com.xiaoyv.bangumi.shared.ui.component.scroll.rememberScrollUpScrollState as rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.rounded.ManageAccounts
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_settings
import com.xiaoyv.bangumi.core_resource.resources.pixiv_user_settings_account
import com.xiaoyv.bangumi.core_resource.resources.pixiv_user_settings_account_desc
import com.xiaoyv.bangumi.core_resource.resources.pixiv_user_settings_autoplay
import com.xiaoyv.bangumi.core_resource.resources.pixiv_user_settings_autoplay_desc
import com.xiaoyv.bangumi.core_resource.resources.pixiv_user_settings_content
import com.xiaoyv.bangumi.core_resource.resources.pixiv_user_settings_display
import com.xiaoyv.bangumi.core_resource.resources.pixiv_user_settings_show_ai
import com.xiaoyv.bangumi.core_resource.resources.pixiv_user_settings_show_ai_desc
import com.xiaoyv.bangumi.core_resource.resources.pixiv_user_settings_show_r18
import com.xiaoyv.bangumi.core_resource.resources.pixiv_user_settings_show_r18_desc
import com.xiaoyv.bangumi.features.pixiv.user.setting.business.PixivUserSettingEvent
import com.xiaoyv.bangumi.features.pixiv.user.setting.business.PixivUserSettingState
import com.xiaoyv.bangumi.features.pixiv.user.setting.business.PixivUserSettingViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingContainer
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingItem
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingSwitchItem
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
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

/**
 * Hosts Pixiv-only display and content preferences.
 */
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

/**
 * Renders settings in the application's grouped preference layout.
 */
@Composable
private fun PixivUserSettingScreenContent(
    modifier: Modifier,
    state: PixivUserSettingState,
    onUiEvent: (PixivUserSettingEvent.UI) -> Unit,
    onActionEvent: (PixivUserSettingEvent.Action) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        SettingContainer(label = { Text(stringResource(Res.string.pixiv_user_settings_content)) }) {
            SettingSwitchItem(
                title = stringResource(Res.string.pixiv_user_settings_show_r18),
                description = stringResource(Res.string.pixiv_user_settings_show_r18_desc),
                value = state.showR18,
                shape = ListItemDefaults.segmentedShapes(0, 2),
                onValueChange = { onActionEvent(PixivUserSettingEvent.Action.OnShowR18Changed(it)) },
            )
            SettingSwitchItem(
                title = stringResource(Res.string.pixiv_user_settings_show_ai),
                description = stringResource(Res.string.pixiv_user_settings_show_ai_desc),
                value = state.showAiWorks,
                shape = ListItemDefaults.segmentedShapes(1, 2),
                onValueChange = { onActionEvent(PixivUserSettingEvent.Action.OnShowAiWorksChanged(it)) },
            )
        }
        SettingContainer(label = { Text(stringResource(Res.string.pixiv_user_settings_display)) }) {
            SettingSwitchItem(
                title = stringResource(Res.string.pixiv_user_settings_autoplay),
                description = stringResource(Res.string.pixiv_user_settings_autoplay_desc),
                value = state.autoplayUgoira,
                shape = ListItemDefaults.segmentedShapes(0, 1),
                onValueChange = { onActionEvent(PixivUserSettingEvent.Action.OnAutoplayUgoiraChanged(it)) },
            )
        }
        SettingContainer(label = { Text(stringResource(Res.string.pixiv_user_settings_account)) }) {
            SettingItem(
                title = stringResource(Res.string.pixiv_user_settings_account),
                supportingContent = { Text(stringResource(Res.string.pixiv_user_settings_account_desc)) },
                icon = BgmIcons.ManageAccounts,
                shape = ListItemDefaults.segmentedShapes(0, 1),
            )
        }
    }
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
