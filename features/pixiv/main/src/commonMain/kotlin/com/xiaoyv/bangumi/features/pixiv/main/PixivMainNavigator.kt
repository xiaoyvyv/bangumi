package com.xiaoyv.bangumi.features.pixiv.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen


fun NavHostController.navigatePixivMain(screen: Screen.PixivMain) = debounce(screen.route) {
    navigate(screen.route)
}

fun NavGraphBuilder.addPixivMainScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composable(route = Screen.PixivMain.route) {
        PixivMainRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}