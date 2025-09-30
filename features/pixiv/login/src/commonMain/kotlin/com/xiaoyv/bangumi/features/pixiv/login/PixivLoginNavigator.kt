package com.xiaoyv.bangumi.features.pixiv.login

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen


fun NavHostController.navigatePixivLogin(screen: Screen.PixivLogin) = debounce(screen.route) {
    navigate(screen.route)
}

fun NavGraphBuilder.addPixivLoginScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composable(route = Screen.PixivLogin.route) {
        PixivLoginRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}