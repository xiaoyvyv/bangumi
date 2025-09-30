package com.xiaoyv.bangumi.features.tag.detail

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.utils.composableScreen
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.core.utils.getInt
import com.xiaoyv.bangumi.shared.core.utils.navigateScreen
import com.xiaoyv.bangumi.shared.ui.component.navigation.EXTRA_TYPE
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen.Companion.TagDetailRouteDefinition
import com.xiaoyv.bangumi.shared.ui.component.navigation.withScreenParams


data class TagDetailArguments(@field:SubjectType val type: Int) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        type = savedStateHandle.getInt(EXTRA_TYPE, SubjectType.ANIME)
    )
}

fun NavHostController.navigateTagDetail(screen: Screen.TagDetail) = debounce(screen.route) {
    navigateScreen(screen.route) {
        withScreenParams(screen)
    }
}

fun NavGraphBuilder.addTagDetailScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composableScreen(TagDetailRouteDefinition) {
        TagDetailRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}