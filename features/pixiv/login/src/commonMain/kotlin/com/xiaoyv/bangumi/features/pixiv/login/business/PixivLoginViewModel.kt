package com.xiaoyv.bangumi.features.pixiv.login.business

import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.sni.AntiSniWebProxy

/**
 * [PixivLoginViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class PixivLoginViewModel(val userManager: UserManager) :
    BaseViewModel<PixivLoginState, PixivLoginSideEffect, PixivLoginEvent.Action>() {

    private val proxy = AntiSniWebProxy(
        userManager.settings.network.configHosts,
        userManager.settings.network.tlsFragmentationDomains,
        connectTimeoutMillis = 10000,
        headerTimeoutMillis = 10000,
        errorHandler = {
            debugLog { it.stackTraceToString() }
        }
    )

    init {
        proxy.start()
    }

    override fun createInitialState() = PixivLoginState()

    override fun onEvent(event: PixivLoginEvent.Action) {
        when (event) {
            is PixivLoginEvent.Action.OnRefresh -> refresh(loading = event.loading)
        }
    }

    override fun onCleared() {
        super.onCleared()
        proxy.close()
    }
}