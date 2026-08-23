package com.xiaoyv.bangumi.features.pixiv.tag.main

import com.xiaoyv.bangumi.features.pixiv.tag.main.business.PixivTagViewModel
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.navScope
import com.xiaoyv.bangumi.shared.ui.component.navigation.navigator
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

val pixivTagMainModule = module {
    viewModelOf(::PixivTagViewModel)

    navScope {
        navigation<Screen.PixivTag> { key ->
            PixivTagRoute(
                viewModel = koinViewModel { parametersOf(key.tag) },
                onNavScreen = { navigator.navigate(it) },
                onNavUp = { navigator.goBack() },
            )
        }
    }
}
