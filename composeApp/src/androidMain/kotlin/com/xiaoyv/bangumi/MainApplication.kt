package com.xiaoyv.bangumi

import android.annotation.SuppressLint
import android.app.Application
import android.content.MutableContextWrapper
import android.os.Looper
import android.webkit.WebView
import com.xiaoyv.bangumi.shared.application

/**
 * [MainApplication]
 *
 * @author why
 * @since 2025/1/13
 */
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        application = this
        preloadWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun preloadWebView() {
        Looper.myQueue().addIdleHandler {
            runCatching {
                val webView = WebView(MutableContextWrapper(applicationContext))
                webView.settings.javaScriptEnabled = true
                webView.loadUrl("about:blank")
                webView.destroy()
            }
            false
        }
    }
}