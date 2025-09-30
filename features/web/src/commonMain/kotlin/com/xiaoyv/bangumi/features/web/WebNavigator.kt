package com.xiaoyv.bangumi.features.web

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.xiaoyv.bangumi.shared.core.utils.composableByRoute
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.core.utils.navString
import com.xiaoyv.bangumi.shared.core.utils.navigateByRoute
import com.xiaoyv.bangumi.shared.ui.component.navigation.SCREEN_ROUTE_WEB
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import io.ktor.util.decodeBase64String
import io.ktor.util.encodeBase64

data class WebArguments(val url: String) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        url = savedStateHandle.get<String>("url").orEmpty().decodeBase64String()
    )
}

fun NavHostController.navigateWeb(screen: Screen.Web) = debounce(screen.route) {
    navigateByRoute(screen.route, "url" to screen.url.encodeBase64())
}

fun NavGraphBuilder.addWebScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composableByRoute(route = SCREEN_ROUTE_WEB, navString("url")) {
        WebRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}