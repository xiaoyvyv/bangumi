package com.xiaoyv.bangumi.features.main.tab.tracking

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

fun NavHostController.navigateTracking(screen: Screen.Tracking) = debounce(screen.route) {
    navigate(screen.route)
}

fun NavGraphBuilder.addTrackingScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composable(route = Screen.Tracking.route) {
        TrackingRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}