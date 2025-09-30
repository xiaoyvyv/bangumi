package com.xiaoyv.bangumi.features.preivew.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.xiaoyv.bangumi.shared.core.utils.composableScreen
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.core.utils.navigateScreen
import com.xiaoyv.bangumi.shared.ui.component.navigation.EXTRA_TEXT
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen.Companion.PreviewTextRouteDefinition


private var html = mutableMapOf<String, String>()

data class PreviewTextArguments(val text: String) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        text = html[EXTRA_TEXT].orEmpty()
    )
}

fun NavHostController.navigatePreviewText(screen: Screen.PreviewText) = debounce(screen.route) {
    html[EXTRA_TEXT] = screen.text
    navigateScreen(screen.route) {
        param(EXTRA_TEXT, EXTRA_TEXT)
    }
}

fun NavGraphBuilder.addPreviewTextScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composableScreen(PreviewTextRouteDefinition) {
        PreviewTextRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}