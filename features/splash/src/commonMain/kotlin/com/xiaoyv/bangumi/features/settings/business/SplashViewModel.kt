package com.xiaoyv.bangumi.features.settings.business

import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel

class SplashViewModel : BaseViewModel<SplashState, SplashSideEffect, SplashEvent.Action>() {
    override fun createInitialState(): SplashState = SplashState()

    override fun onEvent(event: SplashEvent.Action) {

    }
}
