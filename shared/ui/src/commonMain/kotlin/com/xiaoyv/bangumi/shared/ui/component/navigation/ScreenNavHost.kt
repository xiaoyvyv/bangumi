package com.xiaoyv.bangumi.shared.ui.component.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.xiaoyv.bangumi.shared.core.types.settings.SettingNavigationAnimation
import com.xiaoyv.bangumi.shared.data.manager.shared.currentSettings
import org.koin.compose.navigation3.koinEntryProvider

@Composable
fun ScreenNavHost(
    navigator: Navigator,
    modifier: Modifier = Modifier,
) {
    val settings = currentSettings()
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()

    NavDisplay(
        modifier = modifier,
        onBack = { navigator.goBack() },
        backStack = navigator.backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = koinEntryProvider(),
        sceneStrategies = listOf(listDetailStrategy),
        transitionSpec = {
            if (initialStateIsNavKey(Screen.Splash::class)) {
                FadeNavTransitions.transitionSpec(this)
            } else {
                when (settings.ui.navigationAnimation) {
                    SettingNavigationAnimation.FADE -> FadeNavTransitions.transitionSpec(this)
                    SettingNavigationAnimation.SLIDE -> DefaultNavTransitions.transitionSpec(this)
                    else -> EmptyNavTransitions.transitionSpec(this)
                }
            }
        },
        popTransitionSpec = {
            if (initialStateIsNavKey(Screen.Splash::class)) {
                FadeNavTransitions.popTransitionSpec(this)
            } else {
                when (settings.ui.navigationAnimation) {
                    SettingNavigationAnimation.FADE -> FadeNavTransitions.popTransitionSpec(this)
                    SettingNavigationAnimation.SLIDE -> DefaultNavTransitions.popTransitionSpec(this)
                    else -> EmptyNavTransitions.popTransitionSpec(this)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun PagerNavHost(
    backStack: NavBackStack<NavKey>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavDisplay(
        modifier = modifier,
        onBack = onBack,
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = koinEntryProvider(),
        transitionSpec = FadeNavTransitions.transitionSpec,
        popTransitionSpec = FadeNavTransitions.popTransitionSpec
    )
}