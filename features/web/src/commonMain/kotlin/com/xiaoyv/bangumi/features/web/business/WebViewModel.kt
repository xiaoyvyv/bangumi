package com.xiaoyv.bangumi.features.web.business

import androidx.lifecycle.SavedStateHandle
import com.multiplatform.webview.request.WebRequest
import com.xiaoyv.bangumi.features.web.WebArguments
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.mvi.BaseSyntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.core.utils.toUrl
import com.xiaoyv.bangumi.shared.core.utils.trimStr
import com.xiaoyv.bangumi.shared.data.api.client.cookie.BgmCookieStorage
import com.xiaoyv.bangumi.shared.data.usecase.PixivRepoUseCase
import io.ktor.http.Url
import kotlinx.collections.immutable.toPersistentList

/**
 * [WebViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class WebViewModel(
    savedStateHandle: SavedStateHandle,
    private val pixivRepoUseCase: PixivRepoUseCase,
    private val cookieStorage: BgmCookieStorage,
) : BaseViewModel<WebState, WebSideEffect, WebEvent.Action>(savedStateHandle) {
    private val args = WebArguments(savedStateHandle)

    override fun initBaseState(): BaseState<WebState> = BaseState.Loading()

    override fun initSate(onCreate: Boolean) = WebState(
        url = args.url,
    )

    override fun onEvent(event: WebEvent.Action) {
        when (event) {
            is WebEvent.Action.OnHandleProtocol -> onHandleProtocol(event.request)
            is WebEvent.Action.OnTitleChange -> onTitleChange(event.title)
            else -> Unit
        }
    }

    override suspend fun BaseSyntax<WebState, WebSideEffect>.refreshSync() {
        runCatching { cookieStorage.get(args.url.toUrl()) }
            .onFailure { reduceError { it } }
            .onSuccess {
                reduceContent { state.copy(cookies = it.toPersistentList()) }
            }
    }

    private fun onTitleChange(title: String?) = action {
        reduceContent { state.copy(title = title) }
    }

    private fun onHandleProtocol(request: WebRequest) = action {
        val url = request.url.toUrl()
        when (url.protocol.name.lowercase()) {
            "pixiv" -> onHandleProtocolForPixiv(url)
            else -> {
                postToast { "暂不支持处理该协议的链接（${url.protocol.name}）" }
            }
        }
    }

    private suspend fun BaseSyntax<WebState, WebSideEffect>.onHandleProtocolForPixiv(
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