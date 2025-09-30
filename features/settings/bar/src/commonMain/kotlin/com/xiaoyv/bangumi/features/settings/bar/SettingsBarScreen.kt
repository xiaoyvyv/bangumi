package com.xiaoyv.bangumi.features.settings.bar

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.settings_bar
import com.xiaoyv.bangumi.core_resource.resources.settings_bar_1
import com.xiaoyv.bangumi.core_resource.resources.settings_bar_2
import com.xiaoyv.bangumi.core_resource.resources.settings_bar_3
import com.xiaoyv.bangumi.core_resource.resources.settings_bar_4
import com.xiaoyv.bangumi.core_resource.resources.settings_bar_5
import com.xiaoyv.bangumi.core_resource.resources.settings_bar_boot
import com.xiaoyv.bangumi.core_resource.resources.settings_bar_boot_default
import com.xiaoyv.bangumi.core_resource.resources.settings_bar_tab
import com.xiaoyv.bangumi.features.settings.bar.business.SettingsBarEvent
import com.xiaoyv.bangumi.features.settings.bar.business.SettingsBarState
import com.xiaoyv.bangumi.features.settings.bar.business.SettingsBarViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.data.manager.shared.currentSettings
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmLargeTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.AlertOptionDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertDialogState
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingContainer
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingItem
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingItemTrailing
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun SettingsBarRoute(
    viewModel: SettingsBarViewModel = koinViewModel<SettingsBarViewModel>(),
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    SettingsBarScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is SettingsBarEvent.UI.OnNavUp -> onNavUp()
                is SettingsBarEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun SettingsBarScreen(
    baseState: BaseState<SettingsBarState>,
    onUiEvent: (SettingsBarEvent.UI) -> Unit,
    onActionEvent: (SettingsBarEvent.Action) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            BgmLargeTopAppBar(
                title = stringResource(Res.string.settings_bar),
                scrollBehavior = scrollBehavior,
                onNavigationClick = { onUiEvent(SettingsBarEvent.UI.OnNavUp) }
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
            SettingsBarScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun SettingsBarScreenContent(
    state: SettingsBarState,
    onUiEvent: (SettingsBarEvent.UI) -> Unit,
    onActionEvent: (SettingsBarEvent.Action) -> Unit,
) {
    val settings = currentSettings()

    Column(modifier = Modifier.padding(vertical = 24.dp)) {
        SettingContainer(label = { Text(text = stringResource(Res.string.settings_bar_boot)) }) {
            val chooseDefaultTabDialogState = rememberAlertDialogState()
            AlertOptionDialog(
                title = stringResource(Res.string.settings_bar_boot_default),
                state = chooseDefaultTabDialogState,
                items = TabTokens.mainTabIndex,
                onClick = { tab, index ->
                    onActionEvent(SettingsBarEvent.Action.OnUpdate(settings.homeTab.copy(defaultSelected = tab.type)))
                }
            )
            SettingItem(
                title = stringResource(Res.string.settings_bar_boot_default),
                trailingContent = {
                    SettingItemTrailing(
                        text = TabTokens.mainTabIndex.getOrNull(settings.homeTab.defaultSelected)?.displayText().orEmpty()
                    )
                },
                onClick = { chooseDefaultTabDialogState.show() }
            )
        }
        SettingContainer(label = { Text(text = stringResource(Res.string.settings_bar_tab)) }) {
            val chooseTabFeatureDialogState = rememberAlertDialogState()
            var changeTabIndex by remember { mutableStateOf(0) }

            val tabs = remember(settings.homeTab) {
                persistentListOf(
                    settings.homeTab.tab1 to Res.string.settings_bar_1,
                    settings.homeTab.tab2 to Res.string.settings_bar_2,
                    settings.homeTab.tab3 to Res.string.settings_bar_3,
                    settings.homeTab.tab4 to Res.string.settings_bar_4,
                    settings.homeTab.tab5 to Res.string.settings_bar_5,
                )
            }


            AlertOptionDialog(
                title = stringResource(Res.string.settings_bar_tab),
                state = chooseTabFeatureDialogState,
                items = TabTokens.mainTabFeatures,
                onClick = { tab, index ->
                    val updated = when (changeTabIndex) {
                        0 -> settings.homeTab.copy(tab1 = tab.type)
                        1 -> settings.homeTab.copy(tab2 = tab.type)
                        2 -> settings.homeTab.copy(tab3 = tab.type)
                        3 -> settings.homeTab.copy(tab4 = tab.type)
                        4 -> settings.homeTab.copy(tab5 = tab.type)
                        else -> settings.homeTab
                    }
                    onActionEvent(SettingsBarEvent.Action.OnUpdate(updated))
                }
            )

            tabs.forEachIndexed { index, tab ->
                SettingItem(
                    title = stringResource(tab.second),
                    trailingContent = {
                        SettingItemTrailing(
                            text = TabTokens.mainTabFeatures.find { it.type == tab.first }?.displayText().orEmpty()
                        )
                    },
                    onClick = {
                        changeTabIndex = index
                        chooseTabFeatureDialogState.show()
                    }
                )
            }
        }
    }
}

