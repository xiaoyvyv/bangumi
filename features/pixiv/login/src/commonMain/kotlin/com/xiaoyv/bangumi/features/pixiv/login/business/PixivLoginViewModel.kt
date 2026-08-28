package com.xiaoyv.bangumi.features.pixiv.login.business

import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.postEffect
import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.repository.PixivRepository
import com.xiaoyv.bangumi.shared.sni.AntiSniWebProxy

/**
 * [PixivLoginViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class PixivLoginViewModel(
    private val userManager: UserManager,
    private val pixivRepository: PixivRepository,
) :
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
            is PixivLoginEvent.Action.OnRefresh -> refresh(contentLoading = event.loading)
            PixivLoginEvent.Action.OnOpenLogin -> onOpenLogin()
            PixivLoginEvent.Action.OnCheckLogin -> onCheckLogin()
        }
    }

    /**
     * 检查内置网页登录返回后是否已保存 Pixiv Token。
     */
    private fun onCheckLogin() = intent {
        val token = userManager.pixivToken
        if (token.accessToken.isNotBlank() || token.refreshToken.isNotBlank()) {
            postEffect { PixivLoginSideEffect.OnLoginSuccess }
        }
    }

    /**
     * 获取 PKCE Challenge，并通知页面打开应用内 Pixiv 授权网页。
     */
    private fun onOpenLogin() = intent {
        reduceData { state.copy(isOpeningLogin = true) }

        pixivRepository.fetchLoginChallenge()
            .onFailure {
                reduceData { state.copy(isOpeningLogin = false) }
                postToast { it.errMsg }
            }
            .onSuccess { challenge ->
                reduceData { state.copy(isOpeningLogin = false) }
                postEffect { PixivLoginSideEffect.OnOpenWebLogin(challenge.codeChallenge) }
            }
    }

    override fun onCleared() {
        super.onCleared()
        proxy.close()
    }
}
