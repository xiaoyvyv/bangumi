package com.xiaoyv.common.widget.web

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import com.xiaoyv.common.R
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.widget.webview.UiWebInterceptor
import java.io.FileInputStream

/**
 * Class: [WebResourceInterceptor]
 *
 * @author why
 * @since 12/2/23
 */
class WebResourceInterceptor(private val themeCssFile: String) : UiWebInterceptor {
    private var cache = 7 * 24 * 60 * 60 * 1000L

    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest,
    ): WebResourceResponse? {
        val url = request.url.toString()
        when {
            // 检查请求是否是字体文件
            url.contains("font.ttf") -> {
                if (ConfigHelper.isSmoothFont.not()) return null

                runCatching {
                    val response = view.context.resources.openRawResource(R.raw.font).let {
                        WebResourceResponse("font/ttf", "UTF-8", it)
                    }
                    response.setResponseHeaders(setCacheHeaders(System.currentTimeMillis(), cache))
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
            // 主题文件
            url.contains("/css/theme.css") -> {
                return WebResourceResponse("text/css", "UTF-8", FileInputStream(themeCssFile))
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