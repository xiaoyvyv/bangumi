package com.xiaoyv.bangumi.features.settings.business

import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.postEffect
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.repository.UserRepository
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

class SplashViewModel(
    private val userManager: UserManager,
    private val userRepository: UserRepository,
) : BaseViewModel<SplashState, SplashSideEffect, SplashEvent.Action>() {

    override fun createInitialState(): SplashState = SplashState

    override fun onEvent(event: SplashEvent.Action) {
        when (event) {
            SplashEvent.Action.OnLaunch -> intent {
                postEffect { SplashSideEffect.Navigate(checkTargetScreen()) }
            }
        }
    }

    /**
     * 检查并确定 App 启动后的目标跳转页面
     *
     * 每隔 12 小时的启动时通过未读通知接口检测服务连通性；
     * 请求失败时打开 [Screen.DnsResolver]，其余情况打开主页 [Screen.Main]。
     */
    suspend fun checkTargetScreen(): Screen {
        val currentTime = kotlin.time.Clock.System.now().toEpochMilliseconds()
        if (currentTime - userManager.lastBgmHostCheckTime < BGM_HOST_CHECK_INTERVAL_MILLIS || !userManager.settings.network.customResolve) {
            return Screen.Main
        }

        userManager.lastBgmHostCheckTime = currentTime
        val isBgmHostReachable = userRepository.fetchUserUnreadNotification().isSuccess

        return if (isBgmHostReachable) Screen.Main else Screen.DnsResolver
    }

    private companion object {
        const val BGM_HOST_CHECK_INTERVAL_MILLIS = 12 * 60 * 60 * 1000L
    }
}
