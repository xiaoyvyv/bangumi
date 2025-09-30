package com.xiaoyv.bangumi.features.settings.live2d

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

fun NavHostController.navigateSettingsLive2d(screen: Screen.SettingsLive2d) = debounce(screen.route) {
    navigate(screen.route)
}

fun NavGraphBuilder.addSettingsLive2dScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composable(route = Screen.SettingsLive2d.route) {
        SettingsLive2dRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}