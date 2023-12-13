package com.xiaoyv.common.widget.web

import android.content.Intent
import android.webkit.JavascriptInterface
import androidx.annotation.Keep
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.EncodeUtils
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.api.parser.entity.SampleRelatedEntity
import com.xiaoyv.common.currentApplication
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.fromJson
import com.xiaoyv.widget.kts.useNotNull
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

    /**
     * JS 回调相关
     */
    var onPreviewImageListener: (String, List<String>) -> Unit = { _, _ -> }
    var onNeedLoginListener: () -> Unit = {}
    var onClickUserListener: (String) -> Unit = {}
    var onClickRelatedListener: (SampleRelatedEntity.Item) -> Unit = { }

    fun startLoad() {
        webView.multipleWindows = true
        webView.addUrlInterceptor(interceptor)
        webView.loadUrl(WebConfig.page(pageRoute))
        webView.onWindowListener = object : OnWindowListener {
            override fun openNewWindow(url: String) {
                openUrl(url)
            }
        }

        // 机器人说话
        useNotNull(webView.findViewTreeLifecycleOwner()) {
            currentApplication.globalRobotSpeech.observe(this) {
                launchUI { callJs("window.robotSay('$it')") }
            }
        }
    }

    @Keep
    @JavascriptInterface
    fun onPreviewImage(imageUrl: String, imageUrls: Array<String>) {
        onPreviewImageListener(imageUrl, imageUrls.toList())
    }

    @Keep
    @JavascriptInterface
    fun onNeedLogin() {
        debugLog { "请登录后再操作" }
        onNeedLoginListener()
    }

    @Keep
    @JavascriptInterface
    fun onClickUser(userId: String) {
        onClickUserListener(userId)
    }

    @Keep
    @JavascriptInterface
    fun onClickRelated(json: String) {
        useNotNull(json.fromJson<SampleRelatedEntity.Item>()) {
            onClickRelatedListener(this)
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

    private fun openUrl(url: String) {
        runCatching {
            val byteArray = url.encodeToByteArray()
            val encode = EncodeUtils.base64Encode2String(byteArray)
            val uri = "bgm://bangumi.android/route?data=$encode"
            require(ActivityUtils.startActivity(Intent.parseUri(uri, Intent.URI_ALLOW_UNSAFE))) {
                "Uri: 启动失败 -> $uri"
            }
        }.onFailure {
            it.printStackTrace()
        }
    }
}