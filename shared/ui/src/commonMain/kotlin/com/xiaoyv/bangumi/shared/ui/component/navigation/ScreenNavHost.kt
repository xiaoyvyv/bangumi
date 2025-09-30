package com.xiaoyv.bangumi.shared.ui.component.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.xiaoyv.bangumi.shared.core.types.settings.SettingNavigationAnimation
import com.xiaoyv.bangumi.shared.data.manager.shared.currentSettings

val FastInSlowOutEasing: Easing = CubicBezierEasing(0.8f, 1.0f, 0.6f, 0.0f)
val UniformAccelerationEasing = Easing { fraction -> fraction * fraction }
val UniformDecelerationEasing = Easing { fraction -> 1 - (1 - fraction) * (1 - fraction) }

private const val DURATION = 300
val SPEC_OFFSET = tween<IntOffset>(DURATION, easing = UniformDecelerationEasing)
val SPEC_FLOAT = tween<Float>(DURATION, easing = UniformDecelerationEasing)

object EmptyNavTransitions {
    val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        EnterTransition.None
    }
    val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        ExitTransition.None
    }
}

object DefaultNavTransitions {
    val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Start,
            animationSpec = SPEC_OFFSET
        )
    }
    val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        // 预览图片打开时，需要单独修改一下当前页面的出栈动画
        if (targetState.destination.route.orEmpty().startsWith(SCREEN_ROUTE_PREVIEW_MAIN)) fadeOut(SPEC_FLOAT) else {
            fadeOut(SPEC_FLOAT) + slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = SPEC_OFFSET,
                targetOffset = { fullOffset -> (fullOffset * 0.3f).toInt() }
            )
        }
    }
    val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        // 预览图片退出时，需要单独修改一下上一个页面的入栈动画
        if (initialState.destination.route.orEmpty().startsWith(SCREEN_ROUTE_PREVIEW_MAIN)) fadeIn(SPEC_FLOAT) else {
            fadeIn(SPEC_FLOAT) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = SPEC_OFFSET,
                initialOffset = { fullOffset -> (fullOffset * 0.3f).toInt() }
            )
        }
    }
    val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.End,
            animationSpec = SPEC_OFFSET,
        )
    }
    val sizeTransform: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> SizeTransform?)? = null
}

object FadeNavTransitions {
    val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        fadeIn(SPEC_FLOAT)
    }
    val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        // 预览图片打开时，需要单独修改一下当前页面的出栈动画
        if (targetState.destination.route.orEmpty().startsWith(SCREEN_ROUTE_PREVIEW_MAIN)) fadeOut(SPEC_FLOAT) else {
            fadeOut(SPEC_FLOAT)
        }
    }
    val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        // 预览图片退出时，需要单独修改一下上一个页面的入栈动画
        if (initialState.destination.route.orEmpty().startsWith(SCREEN_ROUTE_PREVIEW_MAIN)) fadeIn(SPEC_FLOAT) else {
            fadeIn(SPEC_FLOAT)
        }
    }
    val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        fadeOut(SPEC_FLOAT)
    }
    val sizeTransform: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> SizeTransform?)? = null
}

@Composable
fun ScreenNavHost(
    navController: NavHostController,
    startDestination: Screen = Screen.Splash,
    modifier: Modifier = Modifier,
    builder: NavGraphBuilder.() -> Unit,
) {
    val settings = currentSettings()

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination.route,
        enterTransition = when (settings.ui.navigationAnimation) {
            SettingNavigationAnimation.FADE -> FadeNavTransitions.enterTransition
            SettingNavigationAnimation.SLIDE -> DefaultNavTransitions.enterTransition
            else -> EmptyNavTransitions.enterTransition
        },
        exitTransition = when (settings.ui.navigationAnimation) {
            SettingNavigationAnimation.FADE -> FadeNavTransitions.exitTransition
            SettingNavigationAnimation.SLIDE -> DefaultNavTransitions.exitTransition
            else -> EmptyNavTransitions.exitTransition
        },
        popEnterTransition = when (settings.ui.navigationAnimation) {
            SettingNavigationAnimation.FADE -> FadeNavTransitions.popEnterTransition
            SettingNavigationAnimation.SLIDE -> DefaultNavTransitions.popEnterTransition
            else -> EmptyNavTransitions.enterTransition
        },
        popExitTransition = when (settings.ui.navigationAnimation) {
            SettingNavigationAnimation.FADE -> FadeNavTransitions.popExitTransition
            SettingNavigationAnimation.SLIDE -> DefaultNavTransitions.popExitTransition
            else -> EmptyNavTransitions.exitTransition
        },
        builder = builder
    )
}

@Composable
fun PagerNavHost(
    navController: NavHostController,
    startDestinationRoute: String,
    modifier: Modifier = Modifier,
    builder: NavGraphBuilder.() -> Unit,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestinationRoute,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
        builder = builder
    )
}