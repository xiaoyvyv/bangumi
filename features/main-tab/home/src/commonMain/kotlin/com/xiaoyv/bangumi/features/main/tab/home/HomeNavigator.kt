package com.xiaoyv.bangumi.features.main.tab.home

import com.xiaoyv.bangumi.features.main.tab.home.business.HomeViewModel
import com.xiaoyv.bangumi.features.main.tab.home.page.group.HomeGroupViewModel
import com.xiaoyv.bangumi.features.main.tab.home.page.mono.HomeMonoViewModel
import org.koin.core.module.dsl.viewModelOf
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.navScope
import com.xiaoyv.bangumi.shared.ui.component.navigation.navigator
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

val homeModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::HomeGroupViewModel)
    viewModelOf(::HomeMonoViewModel)

    navScope {
        navigation<Screen.Home> { key ->
            HomeRoute(
                viewModel = koinViewModel { parametersOf(key) },
                onNavScreen = { navigator.navigate(it) },
                onNavUp = { navigator.goBack() }
            )
        }
    }
}
