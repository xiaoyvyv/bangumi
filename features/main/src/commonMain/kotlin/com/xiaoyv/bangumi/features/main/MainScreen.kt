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
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.xiaoyv.bangumi.features.main.business.MainEvent
import com.xiaoyv.bangumi.features.main.business.MainState
import com.xiaoyv.bangumi.features.main.business.MainViewModel
import com.xiaoyv.bangumi.features.main.tab.home.addCalendarScreen
import com.xiaoyv.bangumi.features.main.tab.home.addHomeScreen
import com.xiaoyv.bangumi.features.main.tab.profile.addProfileScreen
import com.xiaoyv.bangumi.features.main.tab.timeline.addTimelineScreen
import com.xiaoyv.bangumi.features.main.tab.topic.addTopicScreen
import com.xiaoyv.bangumi.features.main.tab.tracking.addTrackingScreen
import com.xiaoyv.bangumi.features.message.addMessageMainScreen
import com.xiaoyv.bangumi.features.subject.browser.addSubjectBrowserScreen
import com.xiaoyv.bangumi.features.tag.detail.addTagDetailScreen
import com.xiaoyv.bangumi.shared.core.utils.RouteBuilder
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.core.utils.navigateScreen
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalHideNavIcon
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalSharedState
import com.xiaoyv.bangumi.shared.ui.component.navigation.PagerNavHost
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.withScreenParams
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeVectorTab
import com.xiaoyv.bangumi.shared.ui.kts.isWideScreen
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun MainRoute(
    viewModel: MainViewModel = koinViewModel<MainViewModel>(),
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
    val tabNavController = rememberNavController()
    val current by tabNavController.currentBackStackEntryAsState()
    val currentRoute = current?.destination?.route.orEmpty()
    val isWideScreen = isWideScreen
    val bottomTabs = state.rememberBottomTabs()

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            bottomTabs.forEachIndexed { index, item ->
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
                    selected = currentRoute.startsWith(item.first.route),
                    badge = {
                        val appState = LocalSharedState.current
                        val unreadCnt = appState.unreadNotification + appState.unreadMessage
                        if (unreadCnt > 0 && item.first == Screen.Profile) {
                            Badge { Text(text = unreadCnt.toString()) }
                        }
                    },
                    onClick = {
                        tabNavController.navigateTab(item.first, item.second.label)
                    }
                )
            }
        },
        content = {
            MainScreenContent(
                state = state,
                bottomTabs = bottomTabs,
                navController = tabNavController,
                onUiEvent = onUiEvent,
            )
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

@Composable
fun MainScreenContent(
    state: MainState,
    bottomTabs: PersistentList<Pair<Screen, ComposeVectorTab<String>>>,
    navController: NavHostController,
    onUiEvent: (MainEvent.UI) -> Unit,
) {
    CompositionLocalProvider(LocalHideNavIcon provides true) {
        val startDestinationRoute = remember(bottomTabs, state.defaultSelected) {
            val tab = bottomTabs.getOrNull(state.defaultSelected) ?: bottomTabs.first()
            val screen = tab.first
            RouteBuilder(screen.route)
                .fillParams(screen, tab.second.label)
                .build()
        }
        val onNavigateUp = remember(navController) { { debounce("navigateUp") { navController.navigateUp() } } }

        PagerNavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            startDestinationRoute = startDestinationRoute,
        ) {
            addHomeScreen(onNavUp = onNavigateUp, onNavScreen = { onUiEvent(MainEvent.UI.OnNavScreen(it)) })
            addSubjectBrowserScreen(onNavUp = onNavigateUp, onNavScreen = { onUiEvent(MainEvent.UI.OnNavScreen(it)) })
            addTimelineScreen(onNavUp = onNavigateUp, onNavScreen = { onUiEvent(MainEvent.UI.OnNavScreen(it)) })
            addTopicScreen(onNavUp = onNavigateUp, onNavScreen = { onUiEvent(MainEvent.UI.OnNavScreen(it)) })
            addProfileScreen(onNavUp = onNavigateUp, onNavScreen = { onUiEvent(MainEvent.UI.OnNavScreen(it)) })
            addTrackingScreen(onNavUp = onNavigateUp, onNavScreen = { onUiEvent(MainEvent.UI.OnNavScreen(it)) })
            addTagDetailScreen(onNavUp = onNavigateUp, onNavScreen = { onUiEvent(MainEvent.UI.OnNavScreen(it)) })
            addMessageMainScreen(onNavigateUp, onNavScreen = { onUiEvent(MainEvent.UI.OnNavScreen(it)) })
            addCalendarScreen(onNavigateUp, onNavScreen = { onUiEvent(MainEvent.UI.OnNavScreen(it)) })
        }
    }
}


private fun NavHostController.navigateTab(screen: Screen, label: StringResource) = navigateScreen(
    base = screen.route,
    navOptionsBuilder = {
        popUpTo(graph.startDestinationRoute.orEmpty()) {
            saveState = true
        }
        restoreState = true
        launchSingleTop = true
    },
    routeBuilder = {
        fillParams(screen, label)
    }
)

private fun RouteBuilder.fillParams(screen: Screen, label: StringResource): RouteBuilder {
    when (screen) {
        is Screen.SubjectBrowser -> withScreenParams(screen.copy(title = runBlocking { getString(label) }))
        is Screen.TagDetail -> withScreenParams(screen)
        is Screen.Calendar -> withScreenParams(screen)
        else -> Unit
    }
    return this
}