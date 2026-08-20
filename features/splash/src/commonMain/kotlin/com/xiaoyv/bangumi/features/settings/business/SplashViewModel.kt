package com.xiaoyv.bangumi.features.settings.business

import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.utils.formatDate
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

class SplashViewModel(
    private val userManager: UserManager,
) : BaseViewModel<SplashState, SplashSideEffect, SplashEvent.Action>() {

    override fun createInitialState(): SplashState = SplashState()

    override fun onEvent(event: SplashEvent.Action) {

    }

    /**
     * 检查并确定 App 启动后的目标跳转页面
     *
     * 规则：
     * 1. 每天第一次启动 App；
     * 2. 或者 settings.network.sniHosts 中的域名有任意一个 IP 为空（无 IP 或包含空白字符串 IP）；
     * 满足上述任意一条则打开 [Screen.DnsResolver]，否则打开主页 [Screen.Main]。
     */
    fun checkTargetScreen(): Screen {
        val todayDate = System.currentTimeMillis().formatDate("yyyy-MM-dd")
        val isFirstLaunchToday = userManager.lastLaunchDate != todayDate

        if (isFirstLaunchToday) {
            userManager.lastLaunchDate = todayDate
        }

        val sniHosts = userManager.settings.network.sniHosts
        val hasEmptyIp = sniHosts.values.any { ips -> ips.isEmpty() || ips.any { it.isBlank() } }

        return if (isFirstLaunchToday || hasEmptyIp) {
            Screen.DnsResolver
        } else {
            Screen.Main
        }
    }
}
