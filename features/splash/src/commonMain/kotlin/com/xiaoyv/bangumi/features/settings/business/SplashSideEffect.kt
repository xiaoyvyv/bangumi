package com.xiaoyv.bangumi.features.settings.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

sealed interface SplashSideEffect {
    data class Navigate(val screen: Screen) : SplashSideEffect
}
