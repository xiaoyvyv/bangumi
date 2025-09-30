package com.xiaoyv.bangumi.features.dollars

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

fun NavHostController.navigateDollars(screen: Screen.Dollars) = debounce(screen.route) {
    navigate(screen.route)
}

fun NavGraphBuilder.addDollarsScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composable(route = Screen.Dollars.route) {
        DollarsRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}