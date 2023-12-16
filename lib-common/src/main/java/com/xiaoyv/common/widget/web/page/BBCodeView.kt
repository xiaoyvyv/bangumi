package com.xiaoyv.common.widget.web.page

import android.annotation.SuppressLint
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.widget.web.WebBase
import com.xiaoyv.widget.webview.UiWebView

/**
 * Class: [BBCodeView]
 *
 * @author why
 * @since 12/2/23
 */
@SuppressLint("JavascriptInterface")
class BBCodeView(override val webView: UiWebView) : WebBase(webView) {
    override val pageRoute: String
        get() = "bb-code"

    suspend fun setCode(code: String) {
        val json = mapOf("code" to code).toJson(true)

        callJs("window.bbcode.setCode($json);")
    }
}