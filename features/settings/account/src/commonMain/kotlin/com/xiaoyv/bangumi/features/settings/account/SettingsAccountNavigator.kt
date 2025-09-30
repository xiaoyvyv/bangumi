package com.xiaoyv.bangumi.features.settings.account

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

fun NavHostController.navigateSettingsAccount(screen: Screen.SettingsAccount) = debounce(screen.route) {
    navigate(screen.route)
}

fun NavGraphBuilder.addSettingsAccountScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composable(route = Screen.SettingsAccount.route) {
        SettingsAccountRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}