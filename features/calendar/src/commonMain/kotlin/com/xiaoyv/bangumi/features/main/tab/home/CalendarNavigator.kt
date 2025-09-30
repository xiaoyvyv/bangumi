package com.xiaoyv.bangumi.features.main.tab.home

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.xiaoyv.bangumi.shared.core.utils.composableScreen
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.core.utils.getBoolean
import com.xiaoyv.bangumi.shared.core.utils.navigateScreen
import com.xiaoyv.bangumi.shared.ui.component.navigation.EXTRA_BOOLEAN
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen.Companion.CalendarRouteDefinition
import com.xiaoyv.bangumi.shared.ui.component.navigation.withScreenParams


data class CalendarArguments(val isToday: Boolean) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        isToday = savedStateHandle.getBoolean(EXTRA_BOOLEAN)
    )
}

fun NavHostController.navigateCalendar(screen: Screen.Calendar) = debounce(screen.route) {
    navigateScreen(screen.route) {
        withScreenParams(screen)
    }
}

fun NavGraphBuilder.addCalendarScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composableScreen(CalendarRouteDefinition) {
        CalendarRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}