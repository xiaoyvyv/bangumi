package com.xiaoyv.bangumi.features.splash

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

fun NavHostController.navigateSplash(screen: Screen.Splash) = debounce(screen.route) {
    navigate(screen.route)
}

fun NavGraphBuilder.addSplashScreen(onNavScreen: (Screen) -> Unit) {
    composable(route = Screen.Splash.route) {
        SplashRoute(onNavScreen = onNavScreen)
    }
}