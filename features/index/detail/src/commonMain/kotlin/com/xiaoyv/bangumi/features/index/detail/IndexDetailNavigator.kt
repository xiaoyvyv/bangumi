package com.xiaoyv.bangumi.features.index.detail

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.xiaoyv.bangumi.shared.core.utils.composableScreen
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.core.utils.getLong
import com.xiaoyv.bangumi.shared.core.utils.navigateScreen
import com.xiaoyv.bangumi.shared.ui.component.navigation.EXTRA_ID
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen.Companion.IndexDetailRouteDefinition

data class IndexDetailArguments(val id: Long) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        id = savedStateHandle.getLong(EXTRA_ID)
    )
}

fun NavHostController.navigateIndexDetail(screen: Screen.IndexDetail) = debounce(screen.route) {
    navigateScreen(screen.route) {
        param(EXTRA_ID, screen.id)
    }
}

fun NavGraphBuilder.addIndexDetailScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composableScreen(IndexDetailRouteDefinition) {
        IndexDetailRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}