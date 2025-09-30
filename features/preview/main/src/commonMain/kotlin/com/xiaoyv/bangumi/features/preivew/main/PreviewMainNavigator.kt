package com.xiaoyv.bangumi.features.preivew.main

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.xiaoyv.bangumi.shared.core.utils.composableScreen
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.core.utils.fromJson
import com.xiaoyv.bangumi.shared.core.utils.getInt
import com.xiaoyv.bangumi.shared.core.utils.getString
import com.xiaoyv.bangumi.shared.core.utils.navigateScreen
import com.xiaoyv.bangumi.shared.core.utils.toJson
import com.xiaoyv.bangumi.shared.ui.component.navigation.EXTRA_INDEX
import com.xiaoyv.bangumi.shared.ui.component.navigation.EXTRA_OBJ
import com.xiaoyv.bangumi.shared.ui.component.navigation.SPEC_FLOAT
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen.Companion.PreviewMainRouteDefinition
import io.ktor.util.decodeBase64String
import io.ktor.util.encodeBase64


data class PreviewMainArguments(
    val index: Int,
    val items: List<String>,
) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        index = savedStateHandle.getInt(EXTRA_INDEX),
        items = savedStateHandle.getString(EXTRA_OBJ)
            .decodeBase64String()
            .fromJson<List<String>>().orEmpty()
    )
}

fun NavHostController.navigatePreviewMain(screen: Screen.PreviewMain) = debounce(screen.route) {
    navigateScreen(base = screen.route) {
        param(EXTRA_INDEX, screen.index)
        param(EXTRA_OBJ, screen.items.toJson().encodeBase64())
    }
}

fun NavGraphBuilder.addPreviewMainScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composableScreen(
        PreviewMainRouteDefinition,
        enterTransition = { fadeIn(SPEC_FLOAT) },
        exitTransition = { fadeOut(SPEC_FLOAT) },
    ) {
        PreviewMainRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}