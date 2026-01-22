package com.xiaoyv.bangumi.features.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveComponentOverrideApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.rememberNavBackStack
import com.xiaoyv.bangumi.features.main.business.MainEvent
import com.xiaoyv.bangumi.features.main.business.MainState
import com.xiaoyv.bangumi.features.main.business.MainViewModel
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalHideNavIcon
import com.xiaoyv.bangumi.shared.ui.component.navigation.PagerNavHost
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.stateConfiguration
import com.xiaoyv.bangumi.shared.ui.kts.isWideScreen
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
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

@OptIn(ExperimentalMaterial3AdaptiveComponentOverrideApi::class)
@Composable
fun MainScreen(
    state: MainState,
    onUiEvent: (MainEvent.UI) -> Unit,
    onActionEvent: (MainEvent.Action) -> Unit,
) {
    val bottomTabs = state.rememberBottomTabs()
    val startDestinationRoute = remember(bottomTabs, state.defaultSelected) {
        val tab = bottomTabs.getOrNull(state.defaultSelected) ?: bottomTabs.first()
        tab.first.fillParams(tab.second.label)
    }
    val backStack = rememberNavBackStack(stateConfiguration, startDestinationRoute)
    backStack.lastOrNull() as? Screen
    val isWideScreen = isWideScreen
    var selected by remember { mutableIntStateOf(0) }

    NavigationSuiteScaffold1(
        modifier = Modifier.fillMaxSize(),
        navigationSuiteItems = bottomTabs,
        onTabSelected = { selected = it },
        selectedTabIndex = { selected },
        /*navigationSuiteItems = {
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
                    selected = current?.route?.startsWith(item.first.route) == true,
                    badge = {
                        val appState = LocalSharedState.current
                        val unreadCnt = appState.unreadNotification + appState.unreadMessage
                        if (unreadCnt > 0 && item.first == Screen.Profile) {
                            Badge { Text(text = unreadCnt.toString()) }
                        }
                    },
                    onClick = {
                        backStack.add(item.first.fillParams(item.second.label))
                    }
                )
            }
        },*/
        content = {
            CompositionLocalProvider(LocalHideNavIcon provides true) {
                PagerNavHost(
                    modifier = Modifier.fillMaxSize(),
                    backStack = backStack,
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


private fun Screen.fillParams(label: StringResource): Screen {
    return when (this) {
        is Screen.SubjectBrowser -> copy(title = runBlocking { getString(label) })
        else -> this
    }
}
