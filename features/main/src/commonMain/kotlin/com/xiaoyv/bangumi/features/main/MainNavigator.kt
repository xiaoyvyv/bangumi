package com.xiaoyv.bangumi.features.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

fun NavHostController.navigateMain(screen: Screen.Main) = debounce(screen.route) {
    navigate(screen.route) {
        popUpTo(graph.startDestinationId) { inclusive = true }
        launchSingleTop = true
    }
}

fun NavGraphBuilder.addMainScreen(onNavScreen: (Screen) -> Unit) {
    composable(route = Screen.Main.route) {
        MainRoute(onNavScreen = onNavScreen)
    }
}