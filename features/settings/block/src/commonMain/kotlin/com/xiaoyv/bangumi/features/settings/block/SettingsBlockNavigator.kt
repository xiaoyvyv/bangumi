package com.xiaoyv.bangumi.features.settings.block

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

fun NavHostController.navigateSettingsBlock(screen: Screen.SettingsBlock) = debounce(screen.route) {
    navigate(screen.route)
}

fun NavGraphBuilder.addSettingsBlockScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composable(route = Screen.SettingsBlock.route) {
        SettingsBlockRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}