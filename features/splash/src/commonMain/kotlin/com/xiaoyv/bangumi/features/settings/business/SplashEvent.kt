package com.xiaoyv.bangumi.features.settings.business

sealed interface SplashEvent {
    sealed interface Action : SplashEvent {
        data object OnLaunch : Action
    }
}
