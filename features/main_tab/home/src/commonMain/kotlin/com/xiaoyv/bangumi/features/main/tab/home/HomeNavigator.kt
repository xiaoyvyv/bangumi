package com.xiaoyv.bangumi.features.main.tab.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

fun NavHostController.navigateHome(screen: Screen.Home) = debounce(screen.route) {
    navigate(screen.route)
}

fun NavGraphBuilder.addHomeScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composable(route = Screen.Home.route) {
        HomeRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}