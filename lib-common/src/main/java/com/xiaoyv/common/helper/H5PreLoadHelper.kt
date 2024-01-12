package com.xiaoyv.common.helper

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log

/**
 * ChatPreLoadHelper
 *
 * @author why
 * @since 2023/3/30
 */
object H5PreLoadHelper {
    private const val WEB_VIEW_FACTORY_CLASS_NAME = "android.webkit.WebViewFactory"
    private const val FACTORY_METHOD_NAME_GET_PROVIDER = "getProvider"

    fun preloadWebView(application: Application) {
        // 将在系统闲置的时候执行
        application.mainLooper.queue.addIdleHandler {
            startChromiumEngine()
            // 返回 false 将会移除这个 IdleHandler
            false
        }
    }

    @SuppressLint("PrivateApi")
    private fun startChromiumEngine() {
        try {
            val start = System.currentTimeMillis()
            val webViewFactoryClass = Class.forName(WEB_VIEW_FACTORY_CLASS_NAME)
            ReflectHelper.invokeStaticMethod<Any>(
                webViewFactoryClass,
                FACTORY_METHOD_NAME_GET_PROVIDER
            )?.let {
                ReflectHelper.invokeMethod<Any>(
                    it, "startYourEngines",
                    arrayOf(Boolean::class.java),
                    arrayOf(true)
                )
            }
            val time = System.currentTimeMillis() - start
            Log.e("ChatPreLoadHelper", "Start chromium engine complete: $time ms")
        } catch (t: Throwable) {
            Log.e("ChatPreLoadHelper", "Start chromium engine error", t)
        }
    }
}