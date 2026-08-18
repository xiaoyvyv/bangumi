package com.xiaoyv.bangumi.features.splash.business

sealed interface SplashEvent {
    sealed interface Action : SplashEvent {
        data object OnLaunch : Action
        data object OnRefresh : Action
    }
}
