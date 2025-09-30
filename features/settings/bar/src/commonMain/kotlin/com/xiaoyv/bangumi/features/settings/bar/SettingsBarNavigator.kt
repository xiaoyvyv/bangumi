package com.xiaoyv.bangumi.features.settings.bar

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

fun NavHostController.navigateSettingsBar(screen: Screen.SettingsBar) = debounce(screen.route) {
    navigate(screen.route)
}

fun NavGraphBuilder.addSettingsBarScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composable(route = Screen.SettingsBar.route) {
        SettingsBarRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}