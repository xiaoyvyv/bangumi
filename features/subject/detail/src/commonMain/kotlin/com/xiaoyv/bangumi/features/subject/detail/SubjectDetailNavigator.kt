package com.xiaoyv.bangumi.features.subject.detail

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.xiaoyv.bangumi.shared.core.utils.composableByRoute
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.core.utils.navLong
import com.xiaoyv.bangumi.shared.core.utils.navigateByRoute
import com.xiaoyv.bangumi.shared.ui.component.navigation.SCREEN_ROUTE_SUBJECT
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

data class SubjectDetailArguments(val id: Long) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        id = savedStateHandle.get<Long>("id") ?: 0
    )
}

fun NavHostController.navigateSubjectDetail(screen: Screen.SubjectDetail) = debounce(screen.route) {
    navigateByRoute(screen.route, "id" to screen.subjectId)
}

fun NavGraphBuilder.addSubjectDetailScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composableByRoute(SCREEN_ROUTE_SUBJECT, navLong("id")) {
        SubjectDetailRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}