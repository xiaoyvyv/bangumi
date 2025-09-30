package com.xiaoyv.bangumi.features.main.tab.newest

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

fun NavHostController.navigateNewest(screen: Screen.Newest) = debounce(screen.route) {
    navigate(screen.route)
}

fun NavGraphBuilder.addNewestScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composable(route = Screen.Newest.route) {
        NewestRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}