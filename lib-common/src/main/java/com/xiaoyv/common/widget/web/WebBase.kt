package com.xiaoyv.common.widget.web

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.webkit.JavascriptInterface
import android.widget.PopupWindow
import androidx.annotation.Keep
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.EncodeUtils
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.ScreenUtils
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.CommentTreeEntity
import com.xiaoyv.common.api.parser.entity.LikeEntity
import com.xiaoyv.common.api.parser.entity.LikeEntity.Companion.normal
import com.xiaoyv.common.api.parser.entity.SampleRelatedEntity
import com.xiaoyv.common.api.response.ReplyResultEntity
import com.xiaoyv.common.currentApplication
import com.xiaoyv.common.databinding.ViewEmojiBinding
import com.xiaoyv.common.helper.CommentPaginationHelper
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.GoogleStyle
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.fromJson
import com.xiaoyv.common.kts.showOptionsDialog
import com.xiaoyv.widget.kts.dpi
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.getAttrColor
import com.xiaoyv.widget.kts.showToastCompat
import com.xiaoyv.widget.kts.useNotNull
import com.xiaoyv.widget.webview.UiWebView
import com.xiaoyv.widget.webview.listener.OnWindowListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

/**
 * Class: [WebBase]
 *
 * @author why
 * @since 12/2/23
 */
abstract class WebBase(open val webView: UiWebView) {
    private var mounted = false

    /**
     * 主题样式文件
     */
    private val themeCssFile by lazy {
        PathUtils.getFilesPathExternalFirst() + "/css/theme.css"
    }

    /**
     * 资源拦截器
     */
    private val interceptor by lazy { WebResourceInterceptor(themeCssFile) }

    /**
     * 评论分页器
     */
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
        val lifecycleOwner = webView.findViewTreeLifecycleOwner() ?: return
        lifecycleOwner.launchUI {
            // 构建主题配置样式
            createThemeCss(webView.context)

            // 设置背景
            webView.setBackgroundColor(webView.context.getAttrColor(GoogleAttr.colorSurface))
            webView.background =
                ColorDrawable(webView.context.getAttrColor(GoogleAttr.colorSurface))

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
                    launchUI { callJs("window.robotSay && window.robotSay('$it')") }
                }
            }
        }
    }

    /**
     * 构建 Css 配置
     */
    private suspend fun createThemeCss(context: Context) {
        withContext(Dispatchers.IO) {
            val colorPrimary = context.getAttrColor(GoogleAttr.colorPrimary)
            val colorOnPrimary = context.getAttrColor(GoogleAttr.colorOnPrimary)
            val colorSurface = context.getAttrColor(GoogleAttr.colorSurface)
            val colorSurfaceContainer = context.getAttrColor(GoogleAttr.colorSurfaceContainer)
            val colorOnSurface = context.getAttrColor(GoogleAttr.colorOnSurface)
            val colorOnSurfaceVariant = context.getAttrColor(GoogleAttr.colorOnSurfaceVariant)

            val themeCss = ":root {\n" +
                    "  --primary-color: ${ColorUtils.int2RgbString(colorPrimary)} !important;\n" +
                    "  --surface-color: ${ColorUtils.int2RgbString(colorSurface)} !important;\n" +
                    "  --on-primary-color: ${ColorUtils.int2RgbString(colorOnPrimary)} !important;\n" +
                    "  --on-surface-color: ${ColorUtils.int2RgbString(colorOnSurface)} !important;\n" +
                    "  --on-surface-variant-color: ${ColorUtils.int2RgbString(colorOnSurfaceVariant)} !important;\n" +
                    "  --surface-container-color: ${ColorUtils.int2RgbString(colorSurfaceContainer)} !important;\n" +
                    "}"

            FileIOUtils.writeFileFromString(themeCssFile, themeCss)
        }
    }

    /**
     * 添加评论
     */
    suspend fun addComment(comment: ReplyResultEntity) {
        callJs("window.comment.addComment(${comment.toJson()})")
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

    /**
     * 判断是否为默认排序，加载配置的默认排序
     */
    @Keep
    @JavascriptInterface
    fun onCommentSort(sort: String): String {
        if (sort.isBlank() || sort == "default") {
            return ConfigHelper.commentDefaultSort
        }
        return sort
    }

    @Keep
    @JavascriptInterface
    fun onPreviewImage(imageUrl: String, imageUrls: Array<String>) {
        onPreviewImageListener(imageUrl, imageUrls.toList())
    }

    @Keep
    @JavascriptInterface
    fun onNeedLogin() {
        if (!UserHelper.isLogin) {
            onNeedLoginListener()
        }
    }

    @Keep
    @JavascriptInterface
    fun onClickUser(userId: String, attachId: String? = null) {
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
                        callJs("window.comment.changeSort('${sorts[which]}')")
                    }
                }
            )
        }
    }


    /**
     * 快捷开关贴贴
     *
     * 注意：Bgm 站有一个BUG，短时间内重复发送和取消贴贴会失效。
     *
     * @param commentId 当前评论ID
     * @param emojiInfo 当前贴贴数据
     */
    @Keep
    @JavascriptInterface
    fun onToggleSmile(commentId: String, emojiInfo: String) {
        val activity = ActivityUtils.getTopActivity() as? BaseViewModelActivity<*, *> ?: return
        val likeAction = emojiInfo.fromJson<LikeEntity.LikeAction>() ?: return

        activity.launchUI(
            state = activity.viewModel.loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()
                showToastCompat(it.errorMsg)
            },
            block = {
                val map = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.toggleLike(
                        type = likeAction.type.toString(),
                        mainId = likeAction.mainId.toString(),
                        likeValue = likeAction.value.toString(),
                        commendId = commentId,
                        gh = UserHelper.formHash
                    ).normal(commentId)
                }

                // 刷新
                refreshEmoji(map)
            }
        )
    }

    /**
     * 点击评论 Action 按钮
     *
     * @param comment 当前评论数据
     */
    @Keep
    @JavascriptInterface
    @Suppress("UNUSED_PARAMETER")
    fun onClickCommentAction(comment: String, touchX: Int, touchY: Int) {
        val entity = comment.fromJson<CommentTreeEntity>() ?: return
        val activity = ActivityUtils.getTopActivity() as? BaseViewModelActivity<*, *> ?: return
        activity.runOnUiThread {
            val decorView = activity.window.decorView
            val binding = ViewEmojiBinding.inflate(activity.layoutInflater)
            val offsetX = ScreenUtils.getScreenWidth() / 3
            val window = PopupWindow(activity).apply {
                contentView = binding.root
                isFocusable = true
                animationStyle = GoogleStyle.Animation_AppCompat_DropDownUp
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }

            // Show
            window.showAtLocation(decorView, Gravity.NO_GRAVITY, offsetX, touchY.dpi)

            // 映射
            val emojiMap = mapOf(
                binding.emoji0 to "0",
                binding.emoji79 to "79",
                binding.emoji54 to "54",
                binding.emoji140 to "140",
                binding.emoji62 to "62",
                binding.emoji122 to "122",
                binding.emoji104 to "104",
                binding.emoji80 to "80",
                binding.emoji141 to "141",
                binding.emoji88 to "88",
                binding.emoji85 to "85",
                binding.emoji90 to "90"
            )

            // 发送贴贴
            val result = { action: Map<String, List<LikeEntity.LikeAction>> ->
                activity.launchUI { refreshEmoji(action) }
                Unit
            }

            // 设置点击事件
            emojiMap.forEach { (t, u) ->
                t.setOnClickListener(WebEmojiListener(activity, window, entity, u, result))
            }
        }
    }

    /**
     * 刷新贴贴数据
     */
    private suspend fun refreshEmoji(response: Map<String, List<LikeEntity.LikeAction>>) {
        callJs("window.comment.refreshEmoji(${response.toJson()})")
    }

    suspend fun callJs(js: String): String {
        waitMounted()
        return suspendCancellableCoroutine { emit ->
            webView.evaluateJavascript(js) {
                require(emit.isActive)

                emit.resumeWith(Result.success(it))
            }
        }
    }

    private suspend fun waitMounted() {
        while (!mounted) {
            delay(100)
            mounted = isMounted()
            debugLog { "isMounted: $mounted" }
        }
    }

    private suspend fun isMounted(): Boolean {
        return runCatching {
            suspendCancellableCoroutine { emit ->
                webView.evaluateJavascript("window.mounted") {
                    require(emit.isActive)

                    emit.resumeWith(Result.success(it.toBoolean()))
                }
            }
        }.getOrDefault(true)
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