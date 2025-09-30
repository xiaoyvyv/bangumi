package com.xiaoyv.bangumi.features.main.tab.timeline

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

fun NavHostController.navigateTimeline(screen: Screen.Timeline) = debounce(screen.route) {
    navigate(screen.route)
}

fun NavGraphBuilder.addTimelineScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composable(route = Screen.Timeline.route) {
        TimelineRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}