package com.xiaoyv.bangumi.features.timeline.detail

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.xiaoyv.bangumi.shared.core.utils.composableScreen
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.core.utils.getLong
import com.xiaoyv.bangumi.shared.core.utils.navigateScreen
import com.xiaoyv.bangumi.shared.ui.component.navigation.EXTRA_ID
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen.Companion.TimelineDetailRouteDefinition


data class TimelineDetailArguments(val id: Long) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        id = savedStateHandle.getLong(EXTRA_ID)
    )
}

fun NavHostController.navigateTimelineDetail(screen: Screen.TimelineDetail) = debounce(screen.route) {
    navigateScreen(screen.route) {
        param(EXTRA_ID, screen.id)
    }
}

fun NavGraphBuilder.addTimelineDetailScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composableScreen(TimelineDetailRouteDefinition) {
        TimelineDetailRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}