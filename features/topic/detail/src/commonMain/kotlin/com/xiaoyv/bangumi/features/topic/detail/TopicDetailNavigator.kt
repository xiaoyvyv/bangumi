package com.xiaoyv.bangumi.features.topic.detail

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.xiaoyv.bangumi.shared.core.utils.composableScreen
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen.Companion.TopicDetailRouteDefinition

fun NavHostController.navigateTopicDetail(screen: Screen.TopicDetail) = debounce(screen.route) {
    navigate(screen.route)
}

fun NavGraphBuilder.addTopicDetailScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composableScreen(TopicDetailRouteDefinition) {
        TopicDetailRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}