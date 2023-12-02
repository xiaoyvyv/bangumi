package com.xiaoyv.common.widget.web

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import com.xiaoyv.common.R
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.widget.webview.UiWebInterceptor

/**
 * Class: [WebResourceInterceptor]
 *
 * @author why
 * @since 12/2/23
 */
class WebResourceInterceptor : UiWebInterceptor {
    private var cache = 7 * 24 * 60 * 60 * 1000L

    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        val url = request.url.toString()
        when {
            // 检查请求是否是字体文件
            url.contains("font.ttf") -> {
                runCatching {
                    val inputStream = view.context.resources.openRawResource(R.raw.font)
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
}