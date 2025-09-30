package com.xiaoyv.bangumi.features.groups.detail

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.xiaoyv.bangumi.shared.core.utils.composableScreen
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.core.utils.getString
import com.xiaoyv.bangumi.shared.core.utils.navigateScreen
import com.xiaoyv.bangumi.shared.ui.component.navigation.EXTRA_NAME
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen.Companion.GroupDetailRouteDefinition


data class GroupsDetailArguments(val name: String) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        name = savedStateHandle.getString(EXTRA_NAME)
    )
}

fun NavHostController.navigateGroupsDetail(screen: Screen.GroupDetail) = debounce(screen.route) {
    navigateScreen(screen.route) {
        param(EXTRA_NAME, screen.name)
    }
}

fun NavGraphBuilder.addGroupsDetailScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composableScreen(GroupDetailRouteDefinition) {
        GroupsDetailRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}