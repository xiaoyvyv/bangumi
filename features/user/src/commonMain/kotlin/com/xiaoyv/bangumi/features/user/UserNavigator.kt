package com.xiaoyv.bangumi.features.user

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.xiaoyv.bangumi.shared.core.utils.composableScreen
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.core.utils.getString
import com.xiaoyv.bangumi.shared.core.utils.navigateScreen
import com.xiaoyv.bangumi.shared.ui.component.navigation.EXTRA_USERNAME
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen.Companion.UserDetailRouteDefinition


data class UserArguments(val username: String) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        username = savedStateHandle.getString(EXTRA_USERNAME)
    )
}

fun NavHostController.navigateUser(screen: Screen.UserDetail) = debounce(screen.route) {
    navigateScreen(screen.route) {
        param(EXTRA_USERNAME, screen.username)
    }
}

fun NavGraphBuilder.addUserScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composableScreen(UserDetailRouteDefinition) {
        UserRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}