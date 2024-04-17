package com.xiaoyv.common.widget.web

import android.content.Context
import android.graphics.BitmapFactory
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.annotation.DrawableRes
import com.blankj.utilcode.util.ImageUtils
import com.xiaoyv.common.R
import com.xiaoyv.common.api.parser.parseCount
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.emoji.BgmEmoji
import com.xiaoyv.widget.webview.UiWebInterceptor
import java.io.ByteArrayInputStream
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
            url.contains("font.ttf") -> runCatching {
                return null
            }
            // 检查请求是否是表情文件
            url.contains("/img/smiles") -> runCatching {
                return handleSmiles(view.context, url)
            }
            // 主题文件
            url.contains("/css/theme.css") -> runCatching {
                return handleThemeCss()
            }
        }
        return super.shouldInterceptRequest(view, request)
    }

    /**
     * 主题文件
     */
    private fun handleThemeCss(): WebResourceResponse {
        return WebResourceResponse("text/css", "UTF-8", FileInputStream(themeCssFile))
    }

    /**
     * 表情文件
     *
     * - https://bangumi.tv/img/smiles/tv/01.gif
     * - https://bangumi.tv/img/smiles/tv/02.gif
     * - https://bangumi.tv/img/smiles/tv/03.gif
     * - https://bangumi.tv/img/smiles/1.gif
     * - https://bangumi.tv/img/smiles/3.gif
     * - https://bangumi.tv/img/smiles/2.gif
     * - https://bangumi.tv/img/smiles/bgm/01.png
     * - https://bangumi.tv/img/smiles/bgm/02.png
     * - https://bangumi.tv/img/smiles/bgm/03.png
     */
    private fun handleSmiles(context: Context, url: String): WebResourceResponse? {
        val index = url.parseCount() - 1
        if (index == -1) return null

        when {
            url.contains("tv", true) -> {
                val resId = BgmEmoji.tvEmoji.values.toList().getOrNull(index) ?: return null
                return WebResourceResponse("image/*", "UTF-8", decodeResource(context, resId))
            }

            url.contains("bgm", true) -> {
                val resId = BgmEmoji.bgmEmoji.values.toList().getOrNull(index) ?: return null
                return WebResourceResponse("image/*", "UTF-8", decodeResource(context, resId))
            }

            else -> {
                val resId = BgmEmoji.normalEmoji.values.toList().getOrNull(index) ?: return null
                return WebResourceResponse("image/*", "UTF-8", decodeResource(context, resId))
            }
        }
    }

    /**
     * 读取表情
     */
    private fun decodeResource(context: Context, @DrawableRes resId: Int): ByteArrayInputStream {
        val bitmap = BitmapFactory.decodeResource(context.resources, resId)
        return ByteArrayInputStream(ImageUtils.bitmap2Bytes(bitmap))
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