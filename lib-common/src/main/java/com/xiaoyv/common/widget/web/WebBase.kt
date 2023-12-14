package com.xiaoyv.common.widget.web

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.webkit.JavascriptInterface
import androidx.annotation.Keep
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.EncodeUtils
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.api.parser.entity.SampleRelatedEntity
import com.xiaoyv.common.currentApplication
import com.xiaoyv.common.helper.CommentPaginationHelper
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.fromJson
import com.xiaoyv.common.kts.showOptionsDialog
import com.xiaoyv.widget.kts.getAttrColor
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
    internal val commentPagination by lazy { CommentPaginationHelper() }

    abstract val pageRoute: String

    /**
     * JS 回调相关
     */
    var onPreviewImageListener: (String, List<String>) -> Unit = { _, _ -> }
    var onNeedLoginListener: () -> Unit = {}
    var onClickUserListener: (String) -> Unit = {}
    var onClickRelatedListener: (SampleRelatedEntity.Item) -> Unit = { }


    fun startLoad() {
        // 设置背景
        webView.setBackgroundColor(webView.context.getAttrColor(GoogleAttr.colorSurface))
        webView.background = ColorDrawable(webView.context.getAttrColor(GoogleAttr.colorSurface))

        // 配置
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

    /**
     * 优化评论加载
     */
    @Keep
    @JvmOverloads
    @JavascriptInterface
    fun onLoadComments(page: Int, size: Int = 10, sort: String): String {
        return commentPagination.loadComments(page, size, sort).toJson()
    }

    @Keep
    @JavascriptInterface
    fun onPreviewImage(imageUrl: String, imageUrls: Array<String>) {
        onPreviewImageListener(imageUrl, imageUrls.toList())
    }

    @Keep
    @JavascriptInterface
    fun onNeedLogin() {
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

    /**
     * 更改评论排序
     */
    @Keep
    @JavascriptInterface
    fun onClickCommentSort() {
        val activity = ActivityUtils.getTopActivity() as? FragmentActivity ?: return
        val sorts = listOf("asc", "desc", "hot")
        activity.runOnUiThread {
            activity.showOptionsDialog(
                title = "更改评论排序",
                items = arrayListOf("按时间正向排序", "按最新发布排序", "按热门程度排序"),
                onItemClick = { _, which ->
                    activity.launchUI {
                        callJs("window.changeCommentSort('${sorts[which]}')")
                    }
                }
            )
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