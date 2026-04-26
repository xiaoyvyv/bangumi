package com.xiaoyv.bangumi.features.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.rememberNavBackStack
import com.xiaoyv.bangumi.features.main.business.MainEvent
import com.xiaoyv.bangumi.features.main.business.MainState
import com.xiaoyv.bangumi.features.main.business.MainViewModel
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalHideNavIcon
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalSharedState
import com.xiaoyv.bangumi.shared.ui.component.navigation.PagerNavHost
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.current
import com.xiaoyv.bangumi.shared.ui.component.navigation.goBack
import com.xiaoyv.bangumi.shared.ui.component.navigation.moveTop
import com.xiaoyv.bangumi.shared.ui.component.navigation.navigate
import com.xiaoyv.bangumi.shared.ui.component.navigation.stateConfiguration
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.kts.isWideScreen
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun MainRoute(
    viewModel: MainViewModel,
    onNavScreen: (Screen) -> Unit,
) {
    val state by viewModel.collectAsState()

    state.content {
        MainScreen(
            state = this,
            onUiEvent = {
                when (it) {
                    is MainEvent.UI.OnNavScreen -> onNavScreen(it.screen)
                }
            },
            onActionEvent = {

            }
        )
    }
}

@Composable
fun MainScreen(
    state: MainState,
    onUiEvent: (MainEvent.UI) -> Unit,
    onActionEvent: (MainEvent.Action) -> Unit,
) {
    val bottomTabs = state.rememberBottomTabs()
    val startDestination = remember(bottomTabs, state.defaultSelected) {
        bottomTabs.getOrNull(state.defaultSelected) ?: bottomTabs.first()
    }
    val backStack = rememberNavBackStack(stateConfiguration, startDestination.first)
    val isWideScreen = isWideScreen

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            bottomTabs.forEach { item ->
                item(
                    modifier = Modifier.padding(bottom = if (isWideScreen) LayoutPadding else 0.dp),
                    label = {
                        Text(
                            text = stringResource(item.second.label),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    icon = { Icon(item.second.icon, stringResource(item.second.label)) },
                    selected = backStack.current == item.first,
                    badge = {
                        val appState = LocalSharedState.current
                        val unreadCnt = appState.unreadNotification + appState.unreadMessage
                        if (unreadCnt > 0 && item.first == Screen.Profile) {
                            Badge { Text(text = unreadCnt.toString()) }
                        }
                    },
                    onClick = {
                        backStack.moveTop(item.first)
                    }
                )
            }
        },
        content = {
            CompositionLocalProvider(LocalHideNavIcon provides true) {
                PagerNavHost(
                    modifier = Modifier.fillMaxSize(),
                    backStack = backStack,
                    onBack = {
                        if (backStack.current != startDestination.first) {
                            backStack.moveTop(startDestination.first)
                        } else {
                            backStack.clear()
                        }
                    }
                )
            }

            if (isWideScreen) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                )
            }
        }
    )
}
