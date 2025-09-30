package com.xiaoyv.bangumi.features.almanac

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen


fun NavHostController.navigateAlmanac(screen: Screen.Almanac) = debounce(screen.route) {
    navigate(screen.route)
}

fun NavGraphBuilder.addAlmanacScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composable(route = Screen.Almanac.route) {
        AlmanacRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}