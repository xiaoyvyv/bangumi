package com.xiaoyv.common.widget.web

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.R
import com.xiaoyv.common.api.parser.entity.BlogDetailEntity
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.widget.webview.UiWebInterceptor
import com.xiaoyv.widget.webview.UiWebView
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine


/**
 * Class: [BlogView]
 *
 * @author why
 * @since 11/30/23
 */
@SuppressLint("JavascriptInterface")
class BlogView(private val webView: UiWebView) : UiWebInterceptor {
    private var mounted = false
    private var cache = 7 * 24 * 60 * 60 * 1000L

    var onPreviewImageListener: (String, List<String>) -> Unit = { _, _ -> }

    init {
        webView.addJavascriptInterface(this, "android")
        webView.addUrlInterceptor(this)
    }

    /**
     * 设置缓存头部
     */
    private fun setCacheHeaders(currentTime: Long, maxAge: Long): Map<String, String> {
        val headers: MutableMap<String, String> = HashMap()
        headers["Cache-Control"] = "public, max-age=" + maxAge / 1000
        headers["Expires"] = (currentTime + maxAge).toString()
        headers["Access-Control-Allow-Origin"] = "*"
        return headers
    }

    fun startLoad() {
        webView.loadUrl("http://192.168.6.70:5173/#/")
//        webView.loadUrl("file:///android_asset/h5/index.html")
    }

    @JavascriptInterface
    fun onPreviewImage(imageUrl: String, imageUrls: Array<String>) {
        onPreviewImageListener(imageUrl, imageUrls.toList())
    }

    suspend fun loadBlogDetail(detailEntity: BlogDetailEntity?) {
        val entity = detailEntity ?: return
        callJs("window.blog.loadBlogDetail(${entity.toJson()})")
    }

    private suspend fun callJs(js: String) {
        while (!mounted) {
            delay(100)
            mounted = isMounted()
            debugLog { "isMounted: $mounted" }
        }
        webView.evaluateJavascript(js, null)
    }

    private suspend fun isMounted(): Boolean {
        return suspendCancellableCoroutine { emit ->
            webView.evaluateJavascript("window.mounted") {
                emit.resumeWith(Result.success(it.toBoolean()))
            }
        }
    }

    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        val url = request.url.toString()
        when {
            // 检查请求是否是字体文件
            url.contains("font.ttf") -> {
                runCatching {
                    val inputStream = webView.context.resources.openRawResource(R.raw.font)
                    val response = WebResourceResponse("font/ttf", "UTF-8", inputStream)
                    val now = System.currentTimeMillis()
                    response.setResponseHeaders(setCacheHeaders(now, cache))
                    return response
                }
            }
            // 检查请求是否是表情文件
            url.contains("/img/smiles") -> {
                runCatching {
                    debugLog { "表情：$url" }
                    return null
                }
            }
        }
        return super.shouldInterceptRequest(view, request)
    }
}