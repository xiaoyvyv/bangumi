package com.xiaoyv.bangumi.features.settings.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

fun NavHostController.navigateSettingsUi(screen: Screen.SettingsUi) = debounce(screen.route) {
    navigate(screen.route)
}

fun NavGraphBuilder.addSettingsUiScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composable(route = Screen.SettingsUi.route) {
        SettingsUiRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}