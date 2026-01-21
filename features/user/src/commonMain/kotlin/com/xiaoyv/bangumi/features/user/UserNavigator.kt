package com.xiaoyv.bangumi.features.user

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.navScope
import com.xiaoyv.bangumi.shared.ui.component.navigation.navigator
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

val userModule = module {
    navScope {
        navigation<Screen.UserDetail> { key ->
            UserRoute(
                viewModel = koinViewModel { parametersOf(key) },
                onNavScreen = { navigator.navigate(it) },
                onNavUp = { navigator.goBack() }
            )
        }
    }
}