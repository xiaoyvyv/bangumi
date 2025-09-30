package com.xiaoyv.bangumi.features.settings.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

fun NavHostController.navigateSettingsMain(screen: Screen.SettingsMain) = debounce(screen.route) {
    navigate(screen.route)
}

fun NavGraphBuilder.addSettingsMainScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composable(route = Screen.SettingsMain.route) {
        SettingsMainRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}