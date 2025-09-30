package com.xiaoyv.bangumi.features.sign.sign_in

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

fun NavHostController.navigateSignIn(screen: Screen.SignIn) = debounce(screen.route) {
    navigate(screen.route)
}

fun NavGraphBuilder.addSignInScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composable(route = Screen.SignIn.route) {
        SignInRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}