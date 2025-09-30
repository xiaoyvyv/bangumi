package com.xiaoyv.bangumi.features.message

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

fun NavHostController.navigateMessageMain(screen: Screen.MessageMain) = debounce(screen.route) {
    navigate(screen.route)
}

fun NavGraphBuilder.addMessageMainScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composable(route = Screen.MessageMain.route) {
        MessageMainRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}