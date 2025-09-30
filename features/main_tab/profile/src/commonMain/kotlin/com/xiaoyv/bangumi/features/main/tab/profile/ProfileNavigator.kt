package com.xiaoyv.bangumi.features.main.tab.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

fun NavHostController.navigateProfile(screen: Screen.Profile) = debounce(screen.route) {
    navigate(screen.route)
}

fun NavGraphBuilder.addProfileScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composable(route = Screen.Profile.route) {
        ProfileRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}