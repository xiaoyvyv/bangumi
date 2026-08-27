@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.features.web.business

import com.multiplatform.webview.cookie.WebViewCookieManager
import com.multiplatform.webview.request.WebRequest
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.web_unsupported_protocol
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.PageStatus
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import org.jetbrains.compose.resources.getString
import com.xiaoyv.bangumi.shared.core.mvi.postEffect
import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.reduceError
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.core.utils.toUrl
import com.xiaoyv.bangumi.shared.core.utils.trimStr
import com.xiaoyv.bangumi.shared.data.api.client.cookie.ApiCookiesStorage
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.usecase.PixivRepoUseCase
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import io.ktor.http.Url
import kotlinx.collections.immutable.toPersistentList
import org.orbitmvi.orbit.syntax.Syntax

/**
 * [WebViewModel]
 *
 *  {"access_token":"cAjL_oOtmfuV1YSCTZjMSvO9qoQ_sP3EkM2hiohiyXE","expires_in":3600,"token_type":"bearer","scope":"","refresh_token":"GHnk7yueFv8C7N1hqEk6ZiAm0L_Klm8bbtf2tfN2jE4","user":{"profile_image_urls":{"px_16x16":"https:\/\/s.pximg.net\/common\/images\/no_profile_ss.png","px_50x50":"https:\/\/s.pximg.net\/common\/images\/no_profile_s.png","px_170x170":"https:\/\/s.pximg.net\/common\/images\/no_profile.png"},"id":"101189656","name":"hy w","account":"user_fjgh3423","mail_address":"wanghuaiyv@gmail.com","is_premium":false,"x_restrict":2,"is_mail_authorized":true,"require_policy_agreement":false},"response":{"access_token":"cAjL_oOtmfuV1YSCTZjMSvO9qoQ_sP3EkM2hiohiyXE","expires_in":3600,"token_type":"bearer","scope":"","refresh_token":"GHnk7yueFv8C7N1hqEk6ZiAm0L_Klm8bbtf2tfN2jE4","user":{"profile_image_urls":{"px_16x16":"https:\/\/s.pximg.net\/common\/images\/no_profile_ss.png","px_50x50":"https:\/\/s.pximg.net\/common\/images\/no_profile_s.png","px_170x170":"https:\/\/s.pximg.net\/common\/images\/no_profile.png"},"id":"101189656","name":"hy w","account":"user_fjgh3423","mail_address":"wanghuaiyv@gmail.com","is_premium":false,"x_restrict":2,"is_mail_authorized":true,"require_policy_agreement":false}}}
 *
 * @author why
 * @since 2025/1/12
 */
class WebViewModel(
    private val args: Screen.Web,
    private val pixivRepoUseCase: PixivRepoUseCase,
    private val cookieStorage: ApiCookiesStorage,
    private val userManager: UserManager
) : BaseViewModel<WebState, WebSideEffect, WebEvent.Action>() {
    private val webViewCookieManager = WebViewCookieManager()

    override fun initBaseState(): UiState<WebState> = UiState(data = createInitialState(), status = PageStatus.Loading)

    override fun createInitialState() = WebState(
        url = args.url,
    )

    override fun onEvent(event: WebEvent.Action) {
        when (event) {
            is WebEvent.Action.OnHandleProtocol -> onHandleProtocol(event.request)
            is WebEvent.Action.OnTitleChange -> onTitleChange(event.title)
            else -> Unit
        }
    }

    override suspend fun Syntax<UiState<WebState>, UiSideEffect<WebSideEffect>>.refreshSync() {
        runCatching { cookieStorage.get(args.url.toUrl()) }
            .onFailure { reduceError { it } }
            .onSuccess {
                reduceData { state.copy(cookies = it.toPersistentList()) }
            }
    }

    private fun onTitleChange(title: String?) = intent {
        reduceData { state.copy(title = title) }
    }

    private fun onHandleProtocol(request: WebRequest) = intent {
        val url = request.url.toUrl()
        when (url.protocol.name.lowercase()) {
            "pixiv" -> onHandleProtocolForPixiv(url)
            else -> {
                postToast { getString(Res.string.web_unsupported_protocol, url.protocol.name) }
            }
        }
    }

    private suspend fun Syntax<UiState<WebState>, UiSideEffect<WebSideEffect>>.onHandleProtocolForPixiv(
        url: Url,
    ) {
        val host = url.host.lowercase()
        val path = url.encodedPath.lowercase()

        when {
            // 登录
            host == "account" && path.contains("login") -> {
                val code = url.parameters["code"].trimStr()
                if (code.isNotBlank()) {
                    pixivRepoUseCase.sendAuthToken(code)
                        .onFailure {
                            debugLog { it.message.orEmpty() }
                            postToast { it.errMsg }
                            postEffect { WebSideEffect.OnReload }
                        }
                        .onSuccess {
                            val cookies = webViewCookieManager.getCookies(WebConstant.URL_BASE_PIXIV)
                            userManager.setPixivToken(it, cookies)

                            postEffect { WebSideEffect.OnNavUp }
                        }
                }
            }
        }
    }
}