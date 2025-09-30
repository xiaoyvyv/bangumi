package com.xiaoyv.bangumi.features.subject.browser

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.xiaoyv.bangumi.shared.core.utils.composableScreen
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.core.utils.fromJson
import com.xiaoyv.bangumi.shared.core.utils.getString
import com.xiaoyv.bangumi.shared.core.utils.navigateScreen
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.SubjectBrowserBody
import com.xiaoyv.bangumi.shared.ui.component.navigation.EXTRA_OBJ
import com.xiaoyv.bangumi.shared.ui.component.navigation.EXTRA_TITLE
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen.Companion.SubjectDetailRouteDefinition
import com.xiaoyv.bangumi.shared.ui.component.navigation.withScreenParams
import io.ktor.util.decodeBase64String


data class SubjectBrowserArguments(
    val body: SubjectBrowserBody,
    val title: String,
) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        body = savedStateHandle.getString(EXTRA_OBJ)
            .decodeBase64String()
            .fromJson<SubjectBrowserBody>() ?: SubjectBrowserBody.Empty,
        title = savedStateHandle.getString(EXTRA_TITLE),
    )

    val uniqueKey = body.uniqueKey + title
}

fun NavHostController.navigateSubjectBrowser(screen: Screen.SubjectBrowser) = debounce(screen.route) {
    navigateScreen(screen.route) { withScreenParams(screen) }
}

fun NavGraphBuilder.addSubjectBrowserScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composableScreen(SubjectDetailRouteDefinition) {
        SubjectBrowserRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}