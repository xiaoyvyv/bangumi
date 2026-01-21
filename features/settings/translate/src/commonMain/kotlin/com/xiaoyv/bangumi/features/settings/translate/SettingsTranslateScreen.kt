package com.xiaoyv.bangumi.features.settings.translate

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
import com.xiaoyv.bangumi.core_resource.resources.settings_translate
import com.xiaoyv.bangumi.features.settings.translate.business.SettingsTranslateEvent
import com.xiaoyv.bangumi.features.settings.translate.business.SettingsTranslateState
import com.xiaoyv.bangumi.features.settings.translate.business.SettingsTranslateViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmLargeTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun SettingsTranslateRoute(
    viewModel: SettingsTranslateViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    SettingsTranslateScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is SettingsTranslateEvent.UI.OnNavUp -> onNavUp()
                is SettingsTranslateEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun SettingsTranslateScreen(
    baseState: BaseState<SettingsTranslateState>,
    onUiEvent: (SettingsTranslateEvent.UI) -> Unit,
    onActionEvent: (SettingsTranslateEvent.Action) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            BgmLargeTopAppBar(
                title = stringResource(Res.string.settings_translate),
                scrollBehavior = scrollBehavior,
                onNavigationClick = { onUiEvent(SettingsTranslateEvent.UI.OnNavUp) }
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
            SettingsTranslateScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun SettingsTranslateScreenContent(
    state: SettingsTranslateState,
    onUiEvent: (SettingsTranslateEvent.UI) -> Unit,
    onActionEvent: (SettingsTranslateEvent.Action) -> Unit,
) {

}

