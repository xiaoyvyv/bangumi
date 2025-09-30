package com.xiaoyv.bangumi.features.main.tab.topic

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

fun NavHostController.navigateTopic(screen: Screen.Topic) = debounce(screen.route) {
    navigate(screen.route)
}

fun NavGraphBuilder.addTopicScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composable(route = Screen.Topic.route) {
        TopicRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}