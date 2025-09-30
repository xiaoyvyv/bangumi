package com.xiaoyv.bangumi.features.settings.main

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.Cached
import androidx.compose.material.icons.rounded.DisplaySettings
import androidx.compose.material.icons.rounded.Feedback
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.ManageAccounts
import androidx.compose.material.icons.rounded.Money
import androidx.compose.material.icons.rounded.NetworkCheck
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Source
import androidx.compose.material.icons.rounded.TableBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.settings_about
import com.xiaoyv.bangumi.core_resource.resources.settings_about_app
import com.xiaoyv.bangumi.core_resource.resources.settings_about_author
import com.xiaoyv.bangumi.core_resource.resources.settings_account
import com.xiaoyv.bangumi.core_resource.resources.settings_account_info
import com.xiaoyv.bangumi.core_resource.resources.settings_bar
import com.xiaoyv.bangumi.core_resource.resources.settings_block_user
import com.xiaoyv.bangumi.core_resource.resources.settings_clean_cache
import com.xiaoyv.bangumi.core_resource.resources.settings_common
import com.xiaoyv.bangumi.core_resource.resources.settings_donate
import com.xiaoyv.bangumi.core_resource.resources.settings_feedback
import com.xiaoyv.bangumi.core_resource.resources.settings_live2d
import com.xiaoyv.bangumi.core_resource.resources.settings_logout
import com.xiaoyv.bangumi.core_resource.resources.settings_logout_desc
import com.xiaoyv.bangumi.core_resource.resources.settings_network
import com.xiaoyv.bangumi.core_resource.resources.settings_privacy
import com.xiaoyv.bangumi.core_resource.resources.settings_qq_group
import com.xiaoyv.bangumi.core_resource.resources.settings_relate
import com.xiaoyv.bangumi.core_resource.resources.settings_source
import com.xiaoyv.bangumi.core_resource.resources.settings_title
import com.xiaoyv.bangumi.core_resource.resources.settings_ui
import com.xiaoyv.bangumi.core_resource.resources.settings_user_argument
import com.xiaoyv.bangumi.core_resource.resources.settings_user_privacy
import com.xiaoyv.bangumi.features.settings.main.business.SettingsMainEvent
import com.xiaoyv.bangumi.features.settings.main.business.SettingsMainState
import com.xiaoyv.bangumi.features.settings.main.business.SettingsMainViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalSharedState
import com.xiaoyv.bangumi.shared.ui.component.action.LocalActionHandler
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmLargeTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.BgmAlertDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertDialogState
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingContainer
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingItem
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import kotlin.random.Random

@Composable
fun SettingsMainRoute(
    viewModel: SettingsMainViewModel = koinViewModel<SettingsMainViewModel>(),
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    SettingsMainScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is SettingsMainEvent.UI.OnNavUp -> onNavUp()
                is SettingsMainEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun SettingsMainScreen(
    baseState: BaseState<SettingsMainState>,
    onUiEvent: (SettingsMainEvent.UI) -> Unit,
    onActionEvent: (SettingsMainEvent.Action) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        snapAnimationSpec = spring(stiffness = Spring.StiffnessHigh)
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            BgmLargeTopAppBar(
                title = stringResource(Res.string.settings_title),
                scrollBehavior = scrollBehavior,
                onNavigationClick = { onUiEvent(SettingsMainEvent.UI.OnNavUp) }
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
            SettingsMainScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun SettingsMainScreenContent(
    state: SettingsMainState,
    onUiEvent: (SettingsMainEvent.UI) -> Unit,
    onActionEvent: (SettingsMainEvent.Action) -> Unit,
) {
    val actionHandler = LocalActionHandler.current

    Column(modifier = Modifier.padding(vertical = 24.dp)) {
        SettingContainer(label = { Text(text = stringResource(Res.string.settings_account)) }) {
            SettingItem(
                title = stringResource(Res.string.settings_account_info),
                icon = BgmIcons.ManageAccounts,
                onClick = { onUiEvent(SettingsMainEvent.UI.OnNavScreen(Screen.SettingsAccount)) }
            )
            SettingItem(
                title = stringResource(Res.string.settings_privacy),
                icon = BgmIcons.PrivacyTip,
                onClick = { onUiEvent(SettingsMainEvent.UI.OnNavScreen(Screen.SettingsPrivacy)) }
            )
            SettingItem(
                title = stringResource(Res.string.settings_block_user),
                icon = BgmIcons.Block,
                onClick = { onUiEvent(SettingsMainEvent.UI.OnNavScreen(Screen.SettingsBlock)) }
            )
        }

        SettingContainer(label = { Text(text = stringResource(Res.string.settings_common)) }) {
            SettingItem(
                title = stringResource(Res.string.settings_live2d),
                icon = BgmIcons.Person,
                onClick = { onUiEvent(SettingsMainEvent.UI.OnNavScreen(Screen.SettingsLive2d)) }
            )
            SettingItem(
                title = stringResource(Res.string.settings_ui),
                icon = BgmIcons.DisplaySettings,
                onClick = { onUiEvent(SettingsMainEvent.UI.OnNavScreen(Screen.SettingsUi)) }
            )
            SettingItem(
                title = stringResource(Res.string.settings_bar),
                icon = BgmIcons.TableBar,
                onClick = { onUiEvent(SettingsMainEvent.UI.OnNavScreen(Screen.SettingsBar)) }
            )
            SettingItem(
                title = stringResource(Res.string.settings_network),
                icon = BgmIcons.NetworkCheck,
                onClick = { onUiEvent(SettingsMainEvent.UI.OnNavScreen(Screen.SettingsNetwork)) }
            )
            SettingItem(
                title = stringResource(Res.string.settings_clean_cache),
                icon = BgmIcons.Cached,
                trailingContent = null,
                onClick = {
                    onActionEvent(SettingsMainEvent.Action.OnCleanCache)
                }
            )
        }

        SettingContainer(label = { Text(text = stringResource(Res.string.settings_relate)) }) {
            SettingItem(
                title = stringResource(Res.string.settings_feedback),
                icon = BgmIcons.Feedback,
                trailingContent = null,
                onClick = { actionHandler.openInBrowser("https://github.com/xiaoyvyv/bangumi/issues") }
            )
            SettingItem(
                title = stringResource(Res.string.settings_donate),
                icon = BgmIcons.Money,
                trailingContent = null,
                onClick = { actionHandler.openInBrowser("https://lain.bgm.tv/pic/photo/l/47/7e/837364_do644.jpg") }
            )
            SettingItem(
                title = stringResource(Res.string.settings_qq_group),
                icon = BgmIcons.Groups,
                trailingContent = null,
                onClick = { actionHandler.openInBrowser("https://qm.qq.com/q/YomiSMeyUs") }
            )
            SettingItem(
                title = stringResource(Res.string.settings_source),
                icon = BgmIcons.Source,
                trailingContent = null,
                onClick = { actionHandler.openInBrowser("https://github.com/xiaoyvyv/bangumi") }
            )
        }

        SettingContainer(label = { Text(text = stringResource(Res.string.settings_about)) }) {
            SettingItem(
                title = stringResource(Res.string.settings_user_argument),
                icon = BgmIcons.Security,
                trailingContent = null,
                onClick = { actionHandler.openInBrowser("https://xiaoyvyv.github.io/bangumi/lib-doc/build/argument.html?_=${Random.nextLong()}") }
            )
            SettingItem(
                title = stringResource(Res.string.settings_user_privacy),
                icon = BgmIcons.PrivacyTip,
                trailingContent = null,
                onClick = { actionHandler.openInBrowser("https://xiaoyvyv.github.io/bangumi/lib-doc/build/starter.html?_=${Random.nextLong()}") }
            )
            SettingItem(
                title = stringResource(Res.string.settings_about_author),
                icon = BgmIcons.Info,
                trailingContent = null,
                onClick = { actionHandler.openInBrowser("https://github.com/xiaoyvyv") }
            )
            SettingItem(
                title = stringResource(Res.string.settings_about_app),
                icon = BgmIcons.Apps,
                trailingContent = null,
                onClick = { actionHandler.openInBrowser("https://github.com/xiaoyvyv/bangumi") }
            )
        }


        if (LocalSharedState.current.isLogin) {
            val confirmLogoutDialog = rememberAlertDialogState()
            BgmAlertDialog(
                state = confirmLogoutDialog,
                title = stringResource(Res.string.settings_logout),
                text = stringResource(Res.string.settings_logout_desc),
                onConfirm = {
                    onActionEvent(SettingsMainEvent.Action.OnLogout)
                }
            )

            Spacer(Modifier.height(24.dp))
            SettingItem(
                title = stringResource(Res.string.settings_logout),
                divider = false,
                trailingContent = null,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                ),
                onClick = { confirmLogoutDialog.show() }
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

