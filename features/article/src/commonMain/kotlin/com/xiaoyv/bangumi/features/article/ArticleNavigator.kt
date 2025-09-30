package com.xiaoyv.bangumi.features.article

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.xiaoyv.bangumi.shared.core.types.RakuenIdType
import com.xiaoyv.bangumi.shared.core.utils.composableScreen
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.core.utils.getLong
import com.xiaoyv.bangumi.shared.core.utils.getString
import com.xiaoyv.bangumi.shared.core.utils.navigateScreen
import com.xiaoyv.bangumi.shared.ui.component.navigation.EXTRA_ID
import com.xiaoyv.bangumi.shared.ui.component.navigation.EXTRA_TYPE
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen.Companion.ArticleRouteDefinition

data class ArticleArguments(
    val id: Long,
    @field:RakuenIdType val type: String,
) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        id = savedStateHandle.getLong(EXTRA_ID),
        type = savedStateHandle.getString(EXTRA_TYPE),
    )
}

fun NavHostController.navigateArticle(screen: Screen.Article) = debounce(screen.route) {
    navigateScreen(base = screen.route) {
        param(EXTRA_ID, screen.id)
        param(EXTRA_TYPE, screen.type)
    }
}

fun NavGraphBuilder.addArticleScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composableScreen(definition = ArticleRouteDefinition) {
        ArticleRoute(
            onNavUp = onNavUp,
            onNavScreen = onNavScreen
        )
    }
}