package com.xiaoyv.bangumi.features.blog.page

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.ui.component.navigation.SCREEN_ROUTE_BLOG_LIST
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

fun NavHostController.navigateBlogPage(screen: Screen.BlogList) = debounce(screen.route) {
    navigate(screen.route)
}

fun NavGraphBuilder.addBlogPageScreen(
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    composable(route = SCREEN_ROUTE_BLOG_LIST) {
//        BlogPageRoute(
//            onNavUp = onNavUp,
//            onNavScreen = onNavScreen
//        )
    }
}