package com.xiaoyv.bangumi.features.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import com.xiaoyv.bangumi.shared.ui.view.BgmScaffold as Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_cancel
import com.xiaoyv.bangumi.core_resource.resources.global_save
import com.xiaoyv.bangumi.core_resource.resources.settings_appearance
import com.xiaoyv.bangumi.core_resource.resources.settings_cache_state
import com.xiaoyv.bangumi.core_resource.resources.settings_cache_state_desc
import com.xiaoyv.bangumi.core_resource.resources.settings_home_shortcuts
import com.xiaoyv.bangumi.core_resource.resources.settings_home_shortcuts_summary
import com.xiaoyv.bangumi.shared.ui.component.dialog.sheet.rememberSheetDialogState
import com.xiaoyv.bangumi.core_resource.resources.settings_content
import com.xiaoyv.bangumi.core_resource.resources.settings_deeplink
import com.xiaoyv.bangumi.core_resource.resources.settings_indication
import com.xiaoyv.bangumi.core_resource.resources.settings_monet_theme
import com.xiaoyv.bangumi.core_resource.resources.settings_monet_theme_desc
import com.xiaoyv.bangumi.core_resource.resources.settings_navigation_animation
import com.xiaoyv.bangumi.core_resource.resources.settings_performance
import com.xiaoyv.bangumi.core_resource.resources.settings_theme
import com.xiaoyv.bangumi.core_resource.resources.settings_theme_color
import com.xiaoyv.bangumi.core_resource.resources.settings_theme_color_reset
import com.xiaoyv.bangumi.core_resource.resources.settings_time_machine_grid_limit
import com.xiaoyv.bangumi.core_resource.resources.settings_top_app_bar_scroll_up
import com.xiaoyv.bangumi.core_resource.resources.settings_top_app_bar_scroll_up_desc
import com.xiaoyv.bangumi.core_resource.resources.settings_tracking_desc
import com.xiaoyv.bangumi.core_resource.resources.settings_tracking_desc_desc
import com.xiaoyv.bangumi.core_resource.resources.settings_ui
import com.xiaoyv.bangumi.core_resource.resources.settings_ui_style
import com.xiaoyv.bangumi.features.settings.ui.business.SettingsUiEvent
import com.xiaoyv.bangumi.features.settings.ui.business.SettingsUiState
import com.xiaoyv.bangumi.features.settings.ui.business.SettingsUiViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.types.settings.SettingIndication
import com.xiaoyv.bangumi.shared.core.types.settings.SettingNavigationAnimation
import com.xiaoyv.bangumi.shared.core.types.settings.SettingTheme
import com.xiaoyv.bangumi.shared.core.types.settings.SettingUiStyle
import com.xiaoyv.bangumi.shared.data.manager.shared.currentSettings
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting
import com.xiaoyv.bangumi.shared.libnative.System
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmLargeTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingContainer
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingItem
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingOptionItem
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingSwitchItem
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens.settingIndicationItems
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens.settingNavigationAnimationItems
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.ColorPicker
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.window.WindowDialog
import com.xiaoyv.bangumi.shared.ui.component.scroll.rememberScrollUpScrollState as rememberScrollState

@Composable
fun SettingsUiRoute(
    viewModel: SettingsUiViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    SettingsUiScreen(
        uiState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is SettingsUiEvent.UI.OnNavUp -> onNavUp()
                is SettingsUiEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun SettingsUiScreen(
    uiState: UiState<SettingsUiState>,
    onUiEvent: (SettingsUiEvent.UI) -> Unit,
    onActionEvent: (SettingsUiEvent.Action) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            BgmLargeTopAppBar(
                title = stringResource(Res.string.settings_ui),
                scrollBehavior = scrollBehavior,
                onNavigationClick = { onUiEvent(SettingsUiEvent.UI.OnNavUp) }
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
            SettingsUiScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun SettingsUiScreenContent(
    state: SettingsUiState,
    onUiEvent: (SettingsUiEvent.UI) -> Unit,
    onActionEvent: (SettingsUiEvent.Action) -> Unit,
) {
    val settings = currentSettings()

    Column(modifier = Modifier.padding(vertical = ContentMargin)) {
        SettingsAppearanceSection(settings = settings.ui) {
            onActionEvent(SettingsUiEvent.Action.OnUpdate(it))
        }

        SettingContainer(label = { Text(text = stringResource(Res.string.settings_performance)) }) {
            SettingOptionItem(
                title = stringResource(Res.string.settings_time_machine_grid_limit),
                shape = ListItemDefaults.segmentedShapes(0, 3),
                value = settings.ui.timeMachineGridLimit.toString(),
                items = TabTokens.settingTimeMachineGridLimitItems,
                onClick = {
                    onActionEvent(SettingsUiEvent.Action.OnUpdate(settings.ui.copy(timeMachineGridLimit = it)))
                }
            )

            SettingOptionItem(
                title = stringResource(Res.string.settings_navigation_animation),
                shape = ListItemDefaults.segmentedShapes(1, 3),
                value = stringResource(SettingNavigationAnimation.string(settings.ui.navigationAnimation)),
                items = settingNavigationAnimationItems,
                onClick = {
                    onActionEvent(SettingsUiEvent.Action.OnUpdate(settings.ui.copy(navigationAnimation = it)))
                }
            )

            SettingSwitchItem(
                title = stringResource(Res.string.settings_cache_state),
                shape = ListItemDefaults.segmentedShapes(2, 3),
                description = stringResource(Res.string.settings_cache_state_desc),
                value = settings.ui.cacheState,
                onValueChange = {
                    onActionEvent(SettingsUiEvent.Action.OnUpdate(settings.ui.copy(cacheState = it)))
                },
            )
        }


        SettingContainer(label = { Text(text = stringResource(Res.string.settings_home_shortcuts)) }) {
            val shortcutSheet = rememberSheetDialogState()
            val hidden = settings.ui.hiddenHomeShortcuts
            val ordered = TabTokens.orderedHomeActions(settings.ui.homeShortcutOrder)
            val editItems = ordered.map { tab ->
                ComposeHomeShortcutEditItem(
                    id = tab.type.toString(),
                    title = stringResource(tab.label),
                    enabled = tab.type.toString() !in hidden,
                )
            }
            val defaultItems = TabTokens.mainHomeActions.map { tab ->
                ComposeHomeShortcutEditItem(
                    id = tab.type.toString(),
                    title = stringResource(tab.label),
                    enabled = true,
                )
            }
            SettingItem(
                title = stringResource(Res.string.settings_home_shortcuts),
                shape = ListItemDefaults.segmentedShapes(0, 1),
                supportingContent = {
                    Text(
                        text = editItems.filter { it.enabled }.joinToString(" / ") { it.title }
                            .ifBlank { stringResource(Res.string.settings_home_shortcuts_summary) },
                    )
                },
                onClick = { shortcutSheet.show() },
            )
            HomeShortcutSheet(
                state = shortcutSheet,
                items = editItems,
                defaultItems = defaultItems,
                onDismiss = { shortcutSheet.dismiss() },
                onSave = { saved ->
                    onActionEvent(
                        SettingsUiEvent.Action.OnUpdate(
                            settings.ui.copy(
                                homeShortcutOrder = saved.map { it.id },
                                hiddenHomeShortcuts = saved.filterNot { it.enabled }.map { it.id },
                            )
                        )
                    )
                    shortcutSheet.dismiss()
                },
            )
        }


        SettingContainer(label = { Text(text = stringResource(Res.string.settings_content)) }) {
            SettingSwitchItem(
                title = stringResource(Res.string.settings_tracking_desc),
                description = stringResource(Res.string.settings_tracking_desc_desc),
                shape = ListItemDefaults.segmentedShapes(0, 2),
                value = settings.ui.trackingDesc,
                onValueChange = {
                    onActionEvent(SettingsUiEvent.Action.OnUpdate(settings.ui.copy(trackingDesc = it)))
                },
            )

            SettingItem(
                title = stringResource(Res.string.settings_deeplink),
                shape = ListItemDefaults.segmentedShapes(1, 2),
                onClick = {
                    System.launchDeeplinkSettings()
                }
            )
        }
    }
}

@Composable
private fun SettingsAppearanceSection(
    settings: ComposeSetting.UIConfig,
    onUpdate: (ComposeSetting.UIConfig) -> Unit,
) {
    val itemCount = if (settings.monetTheme) 6 else 5
    val themeIndex = if (settings.monetTheme) 3 else 2

    SettingContainer(label = { Text(text = stringResource(Res.string.settings_appearance)) }) {
        SettingOptionItem(
            title = stringResource(Res.string.settings_ui_style),
            shape = ListItemDefaults.segmentedShapes(0, itemCount),
            value = stringResource(SettingUiStyle.string(settings.style)),
            items = TabTokens.settingUiStyleItems,
            onClick = { onUpdate(settings.copy(style = it)) },
        )

        SettingSwitchItem(
            title = stringResource(Res.string.settings_monet_theme),
            shape = ListItemDefaults.segmentedShapes(1, itemCount),
            description = stringResource(Res.string.settings_monet_theme_desc),
            value = settings.monetTheme,
            onValueChange = { onUpdate(settings.copy(monetTheme = it)) },
        )

        if (settings.monetTheme) {
            SettingsThemeColorItem(
                shape = ListItemDefaults.segmentedShapes(2, itemCount),
                themeColor = settings.themeColor,
                onColorChange = { onUpdate(settings.copy(themeColor = it)) },
            )
        }

        SettingOptionItem(
            title = stringResource(Res.string.settings_theme),
            shape = ListItemDefaults.segmentedShapes(themeIndex, itemCount),
            value = stringResource(SettingTheme.string(settings.theme)),
            items = TabTokens.settingThemeItems,
            onClick = { onUpdate(settings.copy(theme = it)) },
        )

        SettingOptionItem(
            title = stringResource(Res.string.settings_indication),
            shape = ListItemDefaults.segmentedShapes(themeIndex + 1, itemCount),
            items = settingIndicationItems,
            value = stringResource(SettingIndication.string(settings.indication)),
            onClick = { onUpdate(settings.copy(indication = it)) },
        )

        SettingSwitchItem(
            title = stringResource(Res.string.settings_top_app_bar_scroll_up),
            shape = ListItemDefaults.segmentedShapes(itemCount - 1, itemCount),
            description = stringResource(Res.string.settings_top_app_bar_scroll_up_desc),
            value = settings.topAppBarScrollUp,
            onValueChange = { onUpdate(settings.copy(topAppBarScrollUp = it)) },
        )
    }
}

@Composable
private fun SettingsThemeColorItem(
    shape: ListItemShapes,
    themeColor: Long,
    onColorChange: (Long) -> Unit,
) {
    var showColorPicker by remember { mutableStateOf(false) }
    var draftColor by remember(themeColor) { mutableStateOf(Color(themeColor.toInt())) }

    SettingItem(
        title = stringResource(Res.string.settings_theme_color),
        shape = shape,
        trailingContent = {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(themeColor.toInt())),
            )
        },
        onClick = {
            draftColor = Color(themeColor.toInt())
            showColorPicker = true
        },
    )

    WindowDialog(
        show = showColorPicker,
        title = stringResource(Res.string.settings_theme_color),
        onDismissRequest = { showColorPicker = false },
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
        ) {
            ColorPicker(
                color = draftColor,
                onColorChanged = { draftColor = it },
                modifier = Modifier.fillMaxWidth(),
            )
            TextButton(
                text = stringResource(Res.string.settings_theme_color_reset),
                onClick = { draftColor = Color(ComposeSetting.UIConfig.DefaultThemeColor.toInt()) },
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(ContentMarginHalf),
            ) {
                TextButton(
                    text = stringResource(Res.string.global_cancel),
                    onClick = { showColorPicker = false },
                    modifier = Modifier.weight(1f),
                )
                TextButton(
                    text = stringResource(Res.string.global_save),
                    onClick = {
                        onColorChange(draftColor.toArgb().toLong() and 0xFFFFFFFFL)
                        showColorPicker = false
                    },
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}


@Preview
@Composable
private fun SettingsUiScreenPreview() {
    PreviewColumn {
        SettingsUiScreen(
            uiState = UiState(data = SettingsUiState()),
            onUiEvent = {},
            onActionEvent = {}
        )
    }
}
