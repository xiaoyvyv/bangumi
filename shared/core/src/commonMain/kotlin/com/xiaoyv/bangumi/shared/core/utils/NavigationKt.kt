package com.xiaoyv.bangumi.shared.core.utils

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.xiaoyv.bangumi.shared.core.types.AppDsl
import io.ktor.http.encodeURLParameter
import kotlin.jvm.JvmSuppressWildcards

inline fun <reified T> SavedStateHandle.getOrDefault(key: String, default: T): T {
    return this[key] ?: default
}

fun SavedStateHandle.getString(key: String, default: String = ""): String = this[key] ?: default
fun SavedStateHandle.getInt(key: String, default: Int = 0): Int = getOrDefault(key, default)
fun SavedStateHandle.getLong(key: String, default: Long = 0L): Long = getOrDefault(key, default)
fun SavedStateHandle.getFloat(key: String, default: Float = 0f): Float = getOrDefault(key, default)
fun SavedStateHandle.getDouble(key: String, default: Double = 0.0): Double = getOrDefault(key, default)
fun SavedStateHandle.getBoolean(key: String, default: Boolean = false): Boolean = getOrDefault(key, default)
fun SavedStateHandle.getChar(key: String, default: Char = Char.MIN_VALUE): Char = getOrDefault(key, default)
fun SavedStateHandle.getShort(key: String, default: Short = 0): Short = getOrDefault(key, default)
fun SavedStateHandle.getStringArray(key: String): Array<String> = this[key] ?: emptyArray()
fun SavedStateHandle.getStringArrayList(key: String): ArrayList<String> = this[key] ?: arrayListOf()

fun NavHostController.navigateScreen(
    base: String,
    navOptionsBuilder: NavOptionsBuilder.() -> Unit = {},
    routeBuilder: RouteBuilder.() -> Unit,
) {
    val routeBuilder = RouteBuilder(base).apply(routeBuilder)
    val route = routeBuilder.build()
    navigate(route, builder = navOptionsBuilder)
}

@AppDsl
class RouteBuilder(private val base: String) {
    private val params = mutableMapOf<String, String>()

    fun param(key: String, value: String?) {
        if (value != null) params[key] = value
    }

    fun param(key: String, value: Int?) = param(key, value?.toString())
    fun param(key: String, value: Long?) = param(key, value?.toString())
    fun param(key: String, value: Boolean?) = param(key, value?.toString())
    fun param(key: String, value: Float?) = param(key, value?.toString())
    fun param(key: String, value: Double?) = param(key, value?.toString())
    fun param(key: String, value: Enum<*>?) = param(key, value?.name)

    fun build(): String {
        if (params.isEmpty()) return base
        val encoded = params.entries.joinToString("&") { (k, v) ->
            "$k=${v.encodeURLParameter()}"
        }
        return "$base?$encoded"
    }
}

fun NavGraphBuilder.composableScreen(
    definition: RouteDefinition,
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
    exitTransition: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
    popEnterTransition: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = enterTransition,
    popExitTransition: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = exitTransition,
    sizeTransform: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> SizeTransform?)? = null,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) {
    composable(
        route = definition.buildRoute(),
        deepLinks = deepLinks,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition,
        sizeTransform = sizeTransform,
        arguments = definition.arguments,
        content = content
    )
}

@Stable
@AppDsl
class RouteDefinition(private val base: String) {
    private val _arguments = mutableListOf<NamedNavArgument>()
    val arguments: List<NamedNavArgument> get() = _arguments

    private val pathSegments = mutableListOf<String>()

    fun stringArg(name: String, defaultValue: String? = null) {
        addArgument(name, NavType.StringType, defaultValue, true)
    }

    fun longArg(name: String, defaultValue: Long? = null) {
        addArgument(name, NavType.LongType, defaultValue, false)
    }

    fun intArg(name: String, defaultValue: Int? = null) {
        addArgument(name, NavType.IntType, defaultValue, false)
    }

    fun booleanArg(name: String, defaultValue: Boolean? = null) {
        addArgument(name, NavType.BoolType, defaultValue, false)
    }

    private fun <T> addArgument(
        name: String,
        type: NavType<T>,
        defaultValue: T?,
        nullable: Boolean = false,
    ) {
        val argument = navArgument(name) {
            this.type = type
            this.nullable = nullable
            if (defaultValue != null) this.defaultValue = defaultValue
        }
        _arguments += argument
        pathSegments += "$name={$name}"
    }

    fun buildRoute(): String {
        return if (pathSegments.isEmpty()) base else "$base?${pathSegments.joinToString("&")}"
    }
}
