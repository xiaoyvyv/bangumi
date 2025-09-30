package com.xiaoyv.bangumi.features.notification

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

fun NavHostController.navigateNotification(screen: Screen.Notification) = debounce(screen.route) {
    navigate(screen.route)
}

fun NavGraphBuilder.addNotificationScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composable(route = Screen.Notification.route) {
        NotificationRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}