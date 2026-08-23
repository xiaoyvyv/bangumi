package com.xiaoyv.bangumi.features.pixiv.illust.main

import com.xiaoyv.bangumi.features.pixiv.illust.main.business.PixivIllustViewModel
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.navScope
import com.xiaoyv.bangumi.shared.ui.component.navigation.navigator
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

val pixivIllustMainModule = module {
    viewModel { (illustId: Long) ->
        PixivIllustViewModel(illustId = illustId, pixivRepository = get())
    }

    navScope {
        navigation<Screen.PixivIllust> { key ->
            PixivIllustRoute(
                viewModel = koinViewModel { parametersOf(key.id) },
                onNavScreen = { navigator.navigate(it) },
                onNavUp = { navigator.goBack() }
            )
        }
    }
}
