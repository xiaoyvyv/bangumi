package com.xiaoyv.bangumi.features.detect

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.xiaoyv.bangumi.shared.component.DetectType
import com.xiaoyv.bangumi.shared.core.utils.composableByRoute
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.core.utils.navInt
import com.xiaoyv.bangumi.shared.core.utils.navString
import com.xiaoyv.bangumi.shared.core.utils.navigateByRoute
import com.xiaoyv.bangumi.shared.ui.component.navigation.SCREEN_ROUTE_RECEIVE
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

data class ReceiveArguments(
    val path: String,
    @field:DetectType val type: Int,
) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        path = savedStateHandle.get<String>("path").orEmpty(),
        type = savedStateHandle.get<Int>("type") ?: DetectType.SOURCE
    )
}

fun NavHostController.navigateReceive(screen: Screen.DetectImage) = debounce(screen.route) {
    navigateByRoute(screen.route, "path" to screen.path.orEmpty(), "type" to screen.type)
}

fun NavGraphBuilder.addReceiveScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composableByRoute(SCREEN_ROUTE_RECEIVE, navString("path"), navInt("type")) {
        ReceiveRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}