package com.xiaoyv.bangumi.features.settings.privacy

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

fun NavHostController.navigateSettingsPrivacy(screen: Screen.SettingsPrivacy) = debounce(screen.route) {
    navigate(screen.route)
}

fun NavGraphBuilder.addSettingsPrivacyScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composable(route = Screen.SettingsPrivacy.route) {
        SettingsPrivacyRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}