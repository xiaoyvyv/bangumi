package com.xiaoyv.bangumi.features.web.internal

import com.multiplatform.webview.request.RequestInterceptor
import com.multiplatform.webview.request.WebRequest
import com.multiplatform.webview.request.WebRequestInterceptResult
import com.multiplatform.webview.web.WebViewNavigator

internal fun createWebRequestInterceptor(
    onHandleHttpUrl: (WebRequest) -> WebRequestInterceptResult,
    onHandleProtocol: (WebRequest) -> Unit,
) = WebRequestInterceptor(onHandleHttpUrl, onHandleProtocol)

/**
 * [WebRequestInterceptor]
 *
 * @since 2025/5/27
 */
internal class WebRequestInterceptor(
    private val onHandleHttpUrl: (WebRequest) -> WebRequestInterceptResult,
    private val onHandleProtocol: (WebRequest) -> Unit,
) : RequestInterceptor {

    override fun onInterceptUrlRequest(
        request: WebRequest,
        navigator: WebViewNavigator,
    ): WebRequestInterceptResult {
        val url = request.url
        if (url.startsWith("http", true)) return onHandleHttpUrl(request)
        navigator.stopLoading()
        onHandleProtocol(request)
        return WebRequestInterceptResult.Reject
    }
}