package com.xiaoyv.common.widget.web

import com.blankj.utilcode.util.StringUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xiaoyv.common.R
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.openInBrowser
import com.xiaoyv.widget.webview.UiWebView
import com.xiaoyv.widget.webview.listener.OnWindowListener
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
        webView.multipleWindows = true
        webView.addUrlInterceptor(interceptor)
        webView.loadUrl(WebConfig.page(pageRoute))
        webView.onWindowListener = object : OnWindowListener {
            override fun openNewWindow(url: String) {
                MaterialAlertDialogBuilder(webView.context)
                    .setTitle(StringUtils.getString(CommonString.common_tip))
                    .setMessage("是否通过浏览器打开该链接？")
                    .setNegativeButton(StringUtils.getString(R.string.common_cancel), null)
                    .setPositiveButton(StringUtils.getString(R.string.common_done)) { _, _ ->
                        openInBrowser(url)
                    }
                    .create()
                    .show()
            }
        }
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