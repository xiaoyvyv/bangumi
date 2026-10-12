package com.xiaoyv.bangumi.features.settings.bar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import com.xiaoyv.bangumi.shared.ui.component.scroll.rememberScrollUpScrollState as rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.xiaoyv.bangumi.shared.ui.view.BgmScaffold
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.xiaoyv.bangumi.core_resource.resources.settings_bar_appearance
import com.xiaoyv.bangumi.core_resource.resources.settings_bar_boot
import com.xiaoyv.bangumi.core_resource.resources.settings_bar_boot_default
import com.xiaoyv.bangumi.core_resource.resources.settings_bar_tab
import com.xiaoyv.bangumi.core_resource.resources.settings_appearance
import com.xiaoyv.bangumi.features.settings.bar.business.SettingsBarEvent
import com.xiaoyv.bangumi.features.settings.bar.business.SettingsBarState
import com.xiaoyv.bangumi.features.settings.bar.business.SettingsBarViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.types.settings.SettingBottomBarAppearance
import com.xiaoyv.bangumi.shared.data.manager.shared.currentSettings
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmLargeTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingContainer
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingOptionItem
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun SettingsBarRoute(
    viewModel: SettingsBarViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    SettingsBarScreen(
        uiState = baseState,
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
    uiState: UiState<SettingsBarState>,
    onUiEvent: (SettingsBarEvent.UI) -> Unit,
    onActionEvent: (SettingsBarEvent.Action) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    BgmScaffold(
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
            uiState = uiState,
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

    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        SettingContainer(label = { Text(text = stringResource(Res.string.settings_appearance)) }) {
            SettingOptionItem(
                title = stringResource(Res.string.settings_bar_appearance),
                shape = ListItemDefaults.segmentedShapes(0, 1),
                value = stringResource(SettingBottomBarAppearance.string(settings.homeTab.appearance)),
                items = TabTokens.settingBottomBarAppearanceItems,
                onClick = {
                    onActionEvent(SettingsBarEvent.Action.OnUpdate(settings.homeTab.copy(appearance = it)))
                }
            )
        }
        SettingContainer(label = { Text(text = stringResource(Res.string.settings_bar_boot)) }) {
            SettingOptionItem(
                title = stringResource(Res.string.settings_bar_boot_default),
                shape = ListItemDefaults.segmentedShapes(0, 1),
                value = TabTokens.mainTabIndex
                    .getOrNull(settings.homeTab.defaultSelected)
                    ?.displayText()
                    .orEmpty(),
                items = TabTokens.mainTabIndex,
                onClick = { selected ->
                    onActionEvent(
                        SettingsBarEvent.Action.OnUpdate(
                            settings.homeTab.copy(defaultSelected = selected)
                        )
                    )
                },
            )
        }
        SettingContainer(label = { Text(text = stringResource(Res.string.settings_bar_tab)) }) {
            val tabs = remember(settings.homeTab) {
                persistentListOf(
                    settings.homeTab.tab1 to Res.string.settings_bar_1,
                    settings.homeTab.tab2 to Res.string.settings_bar_2,
                    settings.homeTab.tab3 to Res.string.settings_bar_3,
                    settings.homeTab.tab4 to Res.string.settings_bar_4,
                    settings.homeTab.tab5 to Res.string.settings_bar_5,
                )
            }

            tabs.forEachIndexed { index, tab ->
                SettingOptionItem(
                    title = stringResource(tab.second),
                    shape = ListItemDefaults.segmentedShapes(index, tabs.size),
                    value = TabTokens.mainTabFeatures
                        .find { it.type == tab.first }
                        ?.displayText()
                        .orEmpty(),
                    items = TabTokens.mainTabFeatures,
                    onClick = { selected ->
                        val updated = when (index) {
                            0 -> settings.homeTab.copy(tab1 = selected)
                            1 -> settings.homeTab.copy(tab2 = selected)
                            2 -> settings.homeTab.copy(tab3 = selected)
                            3 -> settings.homeTab.copy(tab4 = selected)
                            4 -> settings.homeTab.copy(tab5 = selected)
                            else -> settings.homeTab
                        }
                        onActionEvent(SettingsBarEvent.Action.OnUpdate(updated))
                    },
                )
            }
        }
    }
}
