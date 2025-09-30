package com.xiaoyv.bangumi.features.mikan.studio

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.xiaoyv.bangumi.shared.core.utils.composableByRoute
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.core.utils.navLong
import com.xiaoyv.bangumi.shared.core.utils.navString
import com.xiaoyv.bangumi.shared.core.utils.navigateByRoute
import com.xiaoyv.bangumi.shared.ui.component.navigation.SCREEN_ROUTE_MIKAN_STUDIO
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

data class MikanStudioArguments(
    val id: Long,
    val mikanId: String,
) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        id = savedStateHandle.get<Long>("id") ?: 0,
        mikanId = savedStateHandle.get<String>("mikanId").orEmpty(),
    )
}

fun NavHostController.navigateMikanStudio(screen: Screen.MikanStudio) = debounce(screen.route) {
    navigateByRoute(screen.route, "id" to screen.subjectId, "mikanId" to screen.mikanId)
}

fun NavGraphBuilder.addMikanStudioScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composableByRoute(route = SCREEN_ROUTE_MIKAN_STUDIO, navLong("id"), navString("mikanId")) {
        MikanStudioRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}