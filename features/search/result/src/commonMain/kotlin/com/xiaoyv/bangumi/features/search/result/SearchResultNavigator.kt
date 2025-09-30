package com.xiaoyv.bangumi.features.search.result

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.xiaoyv.bangumi.shared.core.utils.composableByRoute
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.core.utils.navString
import com.xiaoyv.bangumi.shared.core.utils.navigateByRoute
import com.xiaoyv.bangumi.shared.ui.component.navigation.SCREEN_ROUTE_SEARCH_RESULT
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen


data class SearchResultArguments(val query: String) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        query = savedStateHandle.get<String>("query").orEmpty()
    )
}

fun NavHostController.navigateSearchResult(screen: Screen.SearchResult) = debounce(screen.route) {
    navigateByRoute(screen.route, "query" to screen.query) {
        popUpTo(screen.route) {
            inclusive = true
        }
    }
}

fun NavGraphBuilder.addSearchResultScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composableByRoute(SCREEN_ROUTE_SEARCH_RESULT, navString("query")) {
        SearchResultRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}