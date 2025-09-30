package com.xiaoyv.bangumi.features.settings.network

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

fun NavHostController.navigateSettingsNetwork(screen: Screen.SettingsNetwork) = debounce(screen.route) {
    navigate(screen.route)
}

fun NavGraphBuilder.addSettingsNetworkScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composable(route = Screen.SettingsNetwork.route) {
        SettingsNetworkRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}