package com.xiaoyv.bangumi.features.mikan.detail

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.xiaoyv.bangumi.shared.core.utils.composableByRoute
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.core.utils.navString
import com.xiaoyv.bangumi.shared.core.utils.navigateByRoute
import com.xiaoyv.bangumi.shared.ui.component.navigation.SCREEN_ROUTE_MIKAN_RESOURCES
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

data class MikanStudioArguments(
    val mikanId: String,
    val groupId: String,
    val groupName: String,
) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        mikanId = savedStateHandle.get<String>("mikanId").orEmpty(),
        groupId = savedStateHandle.get<String>("groupId").orEmpty(),
        groupName = savedStateHandle.get<String>("groupName").orEmpty(),
    )
}

fun NavHostController.navigateMikanDetail(screen: Screen.MikanResources) = debounce(screen.route) {
    navigateByRoute(
        screen.route,
        "mikanId" to screen.mikanId,
        "groupId" to screen.groupId,
        "groupName" to screen.groupName,
    )
}

fun NavGraphBuilder.addMikanDetailScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composableByRoute(
        route = SCREEN_ROUTE_MIKAN_RESOURCES,
        navString("mikanId"),
        navString("groupId"),
        navString("groupName")
    ) {
        MikanDetailRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}