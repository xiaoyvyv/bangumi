package com.xiaoyv.bangumi.features.main.tab.profile

import com.xiaoyv.bangumi.features.main.tab.profile.business.ProfileViewModel
import org.koin.core.module.dsl.viewModelOf
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.navScope
import com.xiaoyv.bangumi.shared.ui.component.navigation.navigator
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

val profileModule = module {
    viewModelOf(::ProfileViewModel)

    navScope {
        navigation<Screen.Profile> { key ->
            ProfileRoute(
                viewModel = koinViewModel { parametersOf(key) },
                onNavScreen = { navigator.navigate(it) },
                onNavUp = { navigator.goBack() }
            )
        }
    }
}
