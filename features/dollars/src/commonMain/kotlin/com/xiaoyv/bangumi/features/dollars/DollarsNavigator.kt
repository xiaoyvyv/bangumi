package com.xiaoyv.bangumi.features.dollars

import com.xiaoyv.bangumi.features.dollars.business.DollarsViewModel
import org.koin.core.module.dsl.viewModelOf
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.navScope
import com.xiaoyv.bangumi.shared.ui.component.navigation.navigator
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

val dollarsModule = module {
    viewModelOf(::DollarsViewModel)

    navScope {
        navigation<Screen.Dollars> { key ->
            DollarsRoute(
                viewModel = koinViewModel { parametersOf(key) },
                onNavScreen = { navigator.navigate(it) },
                onNavUp = { navigator.goBack() }
            )
        }
    }
}
