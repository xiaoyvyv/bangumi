package com.xiaoyv.bangumi.shared.core.utils

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

fun navString(key: String) = navArgument(key) {
    defaultValue = ""
    type = NavType.StringType
    nullable = true
}

fun navInt(key: String) = navArgument(key) {
    defaultValue = 0
    type = NavType.IntType
    nullable = false
}

fun navLong(key: String) = navArgument(key) {
    defaultValue = 0
    type = NavType.LongType
    nullable = false
}

fun navBoolean(key: String) = navArgument(key) {
    defaultValue = false
    type = NavType.BoolType
    nullable = false
}

fun NavHostController.navigateByRoute(
    route: String,
    vararg params: Pair<String, Any>,
    builder: NavOptionsBuilder.() -> Unit = {},
) {
    if (params.isEmpty()) navigate(route = route, builder = builder) else {
        navigate(
            route = route + "?" + params.joinToString("&") { it.first + "=" + it.second },
            builder = builder
        )
    }
}

fun NavGraphBuilder.composableByRoute(
    route: String,
    vararg navParams: NamedNavArgument,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) {
    val currentRoute = if (navParams.isEmpty()) route else {
        "$route?" + navParams.joinToString("&") {
            it.name + "={${it.name}}"
        }
    }

    composable(
        route = currentRoute,
        arguments = navParams.toList(),
        content = content
    )
}