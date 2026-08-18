package com.xiaoyv.bangumi.features.settings

import com.xiaoyv.bangumi.features.settings.business.SplashViewModel
import org.koin.core.module.dsl.viewModelOf
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.navScope
import com.xiaoyv.bangumi.shared.ui.component.navigation.navigator
import org.koin.compose.viewmodel.koinViewModel
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

val splashModule = module {
    viewModelOf(::SplashViewModel)

    navScope {
        navigation<Screen.Splash> { key ->
            SplashRoute(
                viewModel = koinViewModel(),
                onNavScreen = { navigator.navigate(it) },
            )
        }
    }
}
