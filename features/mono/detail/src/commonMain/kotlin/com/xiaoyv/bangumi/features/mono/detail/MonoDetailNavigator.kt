package com.xiaoyv.bangumi.features.mono.detail

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.utils.composableByRoute
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.core.utils.navInt
import com.xiaoyv.bangumi.shared.core.utils.navLong
import com.xiaoyv.bangumi.shared.core.utils.navigateByRoute
import com.xiaoyv.bangumi.shared.ui.component.navigation.SCREEN_ROUTE_MONO
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

data class MonoDetailArguments(
    val id: Long,
    @field:MonoType val type: Int,
) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        id = savedStateHandle.get<Long>("id") ?: 0,
        type = savedStateHandle.get<Int>("type") ?: MonoType.UNKNOWN
    )
}

fun NavHostController.navigateMonoDetail(screen: Screen.MonoDetail) = debounce(screen.route) {
    navigateByRoute(screen.route, "id" to screen.id, "type" to screen.type)
}

fun NavGraphBuilder.addMonoDetailScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composableByRoute(route = SCREEN_ROUTE_MONO, navLong("id"), navInt("type")) {
        MonoDetailRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}