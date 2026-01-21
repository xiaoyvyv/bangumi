package com.xiaoyv.bangumi.features.splash

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.navScope
import com.xiaoyv.bangumi.shared.ui.component.navigation.navigator
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

val splashModule = module {
    navScope {
        navigation<Screen.Splash> { key ->
            SplashRoute(onNavScreen = { navigator.navigate(it) })
        }
    }
}
