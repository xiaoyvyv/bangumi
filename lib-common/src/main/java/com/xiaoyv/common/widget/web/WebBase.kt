package com.xiaoyv.common.widget.web

import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.widget.webview.UiWebView
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Class: [WebBase]
 *
 * @author why
 * @since 12/2/23
 */
abstract class WebBase(open val webView: UiWebView) {
    private var mounted = false
    private val interceptor by lazy { WebResourceInterceptor() }

    abstract val pageRoute: String


    fun startLoad() {
        webView.addUrlInterceptor(interceptor)
        webView.loadUrl(WebConfig.page(pageRoute))
    }

    suspend fun callJs(js: String): String {
        waitMounted()
        return suspendCancellableCoroutine { emit ->
            webView.evaluateJavascript(js) {
                emit.resumeWith(Result.success(it))
            }
        }
    }

    suspend fun waitMounted() {
        while (!mounted) {
            delay(100)
            mounted = isMounted()
            debugLog { "isMounted: $mounted" }
        }
    }

    private suspend fun isMounted(): Boolean {
        return suspendCancellableCoroutine { emit ->
            webView.evaluateJavascript("window.mounted") {
                emit.resumeWith(Result.success(it.toBoolean()))
            }
        }
    }
}