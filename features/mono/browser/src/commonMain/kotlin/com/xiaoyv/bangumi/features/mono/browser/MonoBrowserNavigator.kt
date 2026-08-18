package com.xiaoyv.bangumi.features.mono.browser

import com.xiaoyv.bangumi.features.mono.browser.business.MonoBrowserViewModel
import org.koin.core.module.dsl.viewModelOf
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.navScope
import com.xiaoyv.bangumi.shared.ui.component.navigation.navigator
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

val monoBrowserModule = module {
    viewModelOf(::MonoBrowserViewModel)

    navScope {
        navigation<Screen.MonoBrowser> { key ->
            MonoBrowserRoute(
                viewModel = koinViewModel { parametersOf(key) },
                onNavScreen = { navigator.navigate(it) },
                onNavUp = { navigator.goBack() }
            )
        }
    }
}
