package com.xiaoyv.bangumi.features.settings.privacy

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.settings_privacy
import com.xiaoyv.bangumi.features.settings.privacy.business.SettingsPrivacyEvent
import com.xiaoyv.bangumi.features.settings.privacy.business.SettingsPrivacyState
import com.xiaoyv.bangumi.features.settings.privacy.business.SettingsPrivacyViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmLargeTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun SettingsPrivacyRoute(
    viewModel: SettingsPrivacyViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    SettingsPrivacyScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is SettingsPrivacyEvent.UI.OnNavUp -> onNavUp()
                is SettingsPrivacyEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun SettingsPrivacyScreen(
    baseState: BaseState<SettingsPrivacyState>,
    onUiEvent: (SettingsPrivacyEvent.UI) -> Unit,
    onActionEvent: (SettingsPrivacyEvent.Action) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            BgmLargeTopAppBar(
                title = stringResource(Res.string.settings_privacy),
                scrollBehavior = scrollBehavior,
                onNavigationClick = { onUiEvent(SettingsPrivacyEvent.UI.OnNavUp) }
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
            SettingsPrivacyScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun SettingsPrivacyScreenContent(
    state: SettingsPrivacyState,
    onUiEvent: (SettingsPrivacyEvent.UI) -> Unit,
    onActionEvent: (SettingsPrivacyEvent.Action) -> Unit,
) {

}

