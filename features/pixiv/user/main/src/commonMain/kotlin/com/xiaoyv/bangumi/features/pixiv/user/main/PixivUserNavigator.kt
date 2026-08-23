package com.xiaoyv.bangumi.features.pixiv.user.main

import com.xiaoyv.bangumi.features.pixiv.user.main.business.PixivUserViewModel
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.navScope
import com.xiaoyv.bangumi.shared.ui.component.navigation.navigator
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

val pixivUserMainModule = module {
    viewModelOf(::PixivUserViewModel)

    navScope {
        navigation<Screen.PixivUserMain> { key ->
            PixivUserRoute(
                viewModel = koinViewModel { parametersOf(key.userId) },
                onNavScreen = { navigator.navigate(it) },
                onNavUp = { navigator.goBack() }
            )
        }
    }
}
