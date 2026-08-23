package com.xiaoyv.bangumi.features.pixiv.user.edit

import com.xiaoyv.bangumi.features.pixiv.user.edit.business.PixivUserEditViewModel
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.navScope
import com.xiaoyv.bangumi.shared.ui.component.navigation.navigator
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

val pixivUserEditModule = module {
    viewModelOf(::PixivUserEditViewModel)

    navScope {
        navigation<Screen.PixivUserEdit> {
            PixivUserEditRoute(
                viewModel = koinViewModel(),
                onNavScreen = { navigator.navigate(it) },
                onNavUp = { navigator.goBack() }
            )
        }
    }
}
