package com.xiaoyv.bangumi.features.splash.business

sealed interface SplashSideEffect {
    data object NavigateMain : SplashSideEffect
}
