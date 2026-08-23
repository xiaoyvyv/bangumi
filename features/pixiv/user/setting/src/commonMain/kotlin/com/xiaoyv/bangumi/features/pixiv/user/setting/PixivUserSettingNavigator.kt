package com.xiaoyv.bangumi.features.pixiv.user.setting

import com.xiaoyv.bangumi.features.pixiv.user.setting.business.PixivUserSettingViewModel
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.navScope
import com.xiaoyv.bangumi.shared.ui.component.navigation.navigator
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

val pixivUserSettingModule = module {
    viewModelOf(::PixivUserSettingViewModel)

    navScope {
        navigation<Screen.PixivUserSetting> {
            PixivUserSettingRoute(
                viewModel = koinViewModel(),
                onNavScreen = { navigator.navigate(it) },
                onNavUp = { navigator.goBack() }
            )
        }
    }
}
