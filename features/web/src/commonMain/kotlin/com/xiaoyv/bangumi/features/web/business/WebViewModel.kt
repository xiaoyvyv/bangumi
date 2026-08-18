package com.xiaoyv.bangumi.features.web.business

import com.multiplatform.webview.request.WebRequest
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.PageStatus
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.postEffect
import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.reduceError
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.core.utils.toUrl
import com.xiaoyv.bangumi.shared.core.utils.trimStr
import com.xiaoyv.bangumi.shared.data.api.client.cookie.BgmCookieStorage
import com.xiaoyv.bangumi.shared.data.usecase.PixivRepoUseCase
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import io.ktor.http.Url
import kotlinx.collections.immutable.toPersistentList
import org.orbitmvi.orbit.syntax.Syntax

/**
 * [WebViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class WebViewModel(
    private val args: Screen.Web,
    private val pixivRepoUseCase: PixivRepoUseCase,
    private val cookieStorage: BgmCookieStorage,
) : BaseViewModel<WebState, WebSideEffect, WebEvent.Action>() {

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
                postToast { "暂不支持处理该协议的链接（${url.protocol.name}）" }
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
                            debugLog {
                                "PixivUser:$it"
                            }

                            postEffect { WebSideEffect.OnNavUp }
                        }
                }
            }
        }
    }
}