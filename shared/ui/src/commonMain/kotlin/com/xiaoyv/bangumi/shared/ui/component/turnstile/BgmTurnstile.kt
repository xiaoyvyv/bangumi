package com.xiaoyv.bangumi.shared.ui.component.turnstile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.multiplatform.webview.request.RequestInterceptor
import com.multiplatform.webview.request.WebRequest
import com.multiplatform.webview.request.WebRequestInterceptResult
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.core.utils.toUrl
import com.xiaoyv.bangumi.shared.data.manager.shared.currentSettings
import com.xiaoyv.bangumi.shared.sni.AntiSniWebProxy


@Composable
fun BgmTurnstile(
    url: String,
    callback: String,
    onToken: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        val settings = currentSettings()
        val webViewState = rememberWebViewState(url)

        val requestInterceptor = remember(onToken, webViewState) {
            object : RequestInterceptor {
                override fun onInterceptUrlRequest(request: WebRequest, navigator: WebViewNavigator): WebRequestInterceptResult {
                    if (request.url.startsWith(callback)) {
                        onToken(request.url.toUrl().parameters["token"].orEmpty())
                        return WebRequestInterceptResult.Reject
                    } else {
                        return WebRequestInterceptResult.Allow
                    }
                }
            }
        }

        val webViewNavigator = rememberWebViewNavigator(
            requestInterceptor = requestInterceptor
        )

        DisposableEffect(settings) {
            val proxy = AntiSniWebProxy(
                settings.network.configHosts,
                settings.network.tlsFragmentationDomains,
                connectTimeoutMillis = 10000,
                headerTimeoutMillis = 10000,
                errorHandler = {
                    debugLog { it.stackTraceToString() }
                }
            )
            proxy.start()
            onDispose { proxy.close() }
        }

        WebView(
            modifier = Modifier.fillMaxSize(),
            state = webViewState,
            navigator = webViewNavigator,
        )
    }
}