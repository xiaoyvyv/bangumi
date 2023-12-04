package com.xiaoyv.common.widget.web.page

import android.annotation.SuppressLint
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.widget.web.WebBase
import com.xiaoyv.widget.webview.UiWebView

/**
 * Class: [SignView]
 *
 * @author why
 * @since 12/2/23
 */
@SuppressLint("JavascriptInterface")
class SignView(override val webView: UiWebView) : WebBase(webView) {
    override val pageRoute: String
        get() = "sign"

    suspend fun setSign(sign: String) {
        val json = mapOf("sign" to sign).toJson(true)
        callJs("window.sign.setSign($json);")
    }


    init {
        webView.addJavascriptInterface(this, "android")
    }
}