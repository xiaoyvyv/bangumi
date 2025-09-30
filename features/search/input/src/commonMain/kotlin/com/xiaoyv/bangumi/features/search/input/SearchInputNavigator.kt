package com.xiaoyv.bangumi.features.search.input

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.xiaoyv.bangumi.shared.core.utils.composableByRoute
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.core.utils.navString
import com.xiaoyv.bangumi.shared.core.utils.navigateByRoute
import com.xiaoyv.bangumi.shared.ui.component.navigation.SCREEN_ROUTE_SEARCH_INPUT
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

data class SearchInputArguments(val query: String) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        query = savedStateHandle.get<String>("query").orEmpty()
    )
}

fun NavHostController.navigateSearchInput(screen: Screen.SearchInput) = debounce(screen.route) {
    navigateByRoute(screen.route, "query" to screen.query)
}

fun NavGraphBuilder.addSearchInputScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composableByRoute(SCREEN_ROUTE_SEARCH_INPUT, navString("query")) {
        SearchInputRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}