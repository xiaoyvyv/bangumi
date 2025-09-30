package com.xiaoyv.bangumi.features.settings.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_avatar
import com.xiaoyv.bangumi.core_resource.resources.global_network_service
import com.xiaoyv.bangumi.core_resource.resources.global_save
import com.xiaoyv.bangumi.core_resource.resources.settings_account_info
import com.xiaoyv.bangumi.core_resource.resources.settings_change_avatar
import com.xiaoyv.bangumi.features.settings.account.business.SettingsAccountEvent
import com.xiaoyv.bangumi.features.settings.account.business.SettingsAccountSideEffect
import com.xiaoyv.bangumi.features.settings.account.business.SettingsAccountState
import com.xiaoyv.bangumi.features.settings.account.business.SettingsAccountViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.types.EditInfoType
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmLargeTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.button.LoadingIconButton
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.BgmAlertInputDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertInputDialogState
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingContainer
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingItem
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.kts.orNotSet
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun SettingsAccountRoute(
    viewModel: SettingsAccountViewModel = koinViewModel<SettingsAccountViewModel>(),
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()
    val avatarPickLauncher = rememberFilePickerLauncher(type = FileKitType.Image) { file ->
        if (file != null) viewModel.onEvent(SettingsAccountEvent.Action.OnPickAvatar(file))
    }

    viewModel.collectBaseSideEffect {
        when (it) {
            SettingsAccountSideEffect.OnNavUp -> onNavUp()
        }
    }

    SettingsAccountScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is SettingsAccountEvent.UI.OnNavUp -> onNavUp()
                is SettingsAccountEvent.UI.OnNavScreen -> onNavScreen(it.screen)
                is SettingsAccountEvent.UI.OnPickAvatar -> avatarPickLauncher.launch()
            }
        },
    )
}

@Composable
private fun SettingsAccountScreen(
    baseState: BaseState<SettingsAccountState>,
    onUiEvent: (SettingsAccountEvent.UI) -> Unit,
    onActionEvent: (SettingsAccountEvent.Action) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            BgmLargeTopAppBar(
                title = stringResource(Res.string.settings_account_info),
                scrollBehavior = scrollBehavior,
                actions = {
                    baseState.content {
                        LoadingIconButton(
                            loading = loading,
                            enabled = loading.not(),
                            onClick = { onActionEvent(SettingsAccountEvent.Action.OnSave) }
                        ) {
                            Icon(
                                imageVector = BgmIcons.Save,
                                contentDescription = stringResource(Res.string.global_save)
                            )
                        }
                    }
                },
                onNavigationClick = { onUiEvent(SettingsAccountEvent.UI.OnNavUp) }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(it),
            onRefresh = { onActionEvent(SettingsAccountEvent.Action.OnRefresh(it)) },
            baseState = baseState,
        ) { state ->
            SettingsAccountScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun SettingsAccountScreenContent(
    state: SettingsAccountState,
    onUiEvent: (SettingsAccountEvent.UI) -> Unit,
    onActionEvent: (SettingsAccountEvent.Action) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val inputDialogState = rememberAlertInputDialogState()

    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        SettingContainer {
            SettingItem(
                modifier = Modifier.padding(vertical = 16.dp),
                title = stringResource(Res.string.settings_change_avatar),
                trailingContent = null,
                leadingContent = {
                    AsyncImage(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        model = if (state.avatarBytes.isNotEmpty()) state.avatarBytes else state.avatar,
                        contentDescription = stringResource(Res.string.global_avatar)
                    )
                },
                onClick = { onUiEvent(SettingsAccountEvent.UI.OnPickAvatar) }
            )

            state.items.entries.forEach { data ->
                SettingItem(
                    title = stringResource(EditInfoType.string(data.key)),
                    leadingContent = null,
                    trailingContent = null,
                    supportingContent = { Text(text = data.value.orNotSet()) },
                    onClick = {
                        scope.launch {
                            inputDialogState.show {
                                it.copy(
                                    title = getString(EditInfoType.string(data.key)),
                                    value = data.value,
                                    singleLine = data.key != EditInfoType.TYPE_INTRO,
                                    extraString = data.key
                                )
                            }
                        }
                    }
                )
            }
        }

        SettingContainer(label = { Text(text = stringResource(Res.string.global_network_service)) }) {
            state.networkItems.entries.forEach { data ->
                SettingItem(
                    title = stringResource(EditInfoType.string(data.key)),
                    leadingContent = null,
                    trailingContent = null,
                    supportingContent = { Text(text = data.value.orNotSet()) },
                    onClick = {
                        scope.launch {
                            inputDialogState.show {
                                it.copy(
                                    title = getString(EditInfoType.string(data.key)),
                                    value = data.value,
                                    singleLine = true,
                                    extraString = data.key
                                )
                            }
                        }
                    }
                )
            }
        }
    }

    BgmAlertInputDialog(
        state = inputDialogState,
        onConfirm = {
            onActionEvent(SettingsAccountEvent.Action.OnEditInfo(it.extraString, it.value))
        }
    )
}

