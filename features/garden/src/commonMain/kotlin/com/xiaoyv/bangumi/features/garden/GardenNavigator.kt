package com.xiaoyv.bangumi.features.garden

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.xiaoyv.bangumi.shared.core.utils.composableScreen
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.core.utils.getString
import com.xiaoyv.bangumi.shared.core.utils.navigateScreen
import com.xiaoyv.bangumi.shared.ui.component.navigation.EXTRA_TEXT
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen.Companion.GardenRouteDefinition
import io.ktor.http.decodeURLQueryComponent
import io.ktor.http.encodeURLQueryComponent


data class GardenArguments(val text: String) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        text = savedStateHandle.getString(EXTRA_TEXT).decodeURLQueryComponent()
    )
}

fun NavHostController.navigateGarden(screen: Screen.Garden) = debounce(screen.route) {
    navigateScreen(screen.route) {
        param(EXTRA_TEXT, screen.query.encodeURLQueryComponent())
    }
}

fun NavGraphBuilder.addGardenScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composableScreen(GardenRouteDefinition) {
        GardenRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}