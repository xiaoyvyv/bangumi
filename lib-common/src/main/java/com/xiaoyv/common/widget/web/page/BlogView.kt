package com.xiaoyv.common.widget.web.page

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import androidx.annotation.Keep
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.api.parser.entity.BlogDetailEntity
import com.xiaoyv.common.api.parser.entity.CommentTreeEntity
import com.xiaoyv.common.helper.CommentPaginationHelper
import com.xiaoyv.common.kts.fromJson
import com.xiaoyv.common.widget.web.WebBase
import com.xiaoyv.widget.webview.UiWebView


/**
 * Class: [BlogView]
 *
 * @author why
 * @since 11/30/23
 */
@SuppressLint("JavascriptInterface")
class BlogView(override val webView: UiWebView) : WebBase(webView) {
    private var commentPagination: CommentPaginationHelper? = null

    var onReplyUserListener: (String, CommentTreeEntity) -> Unit = { _, _ -> }
    var onReplyNewListener: () -> Unit = {}

    override val pageRoute: String
        get() = "blog"

    init {
        webView.addJavascriptInterface(this, "android")
    }

    suspend fun loadBlogDetail(detailEntity: BlogDetailEntity?) {
        val entity = detailEntity ?: return
        commentPagination = CommentPaginationHelper(entity.comments)
        entity.comments = emptyList()

        callJs("window.blog.loadBlogDetail(${entity.toJson()})")
    }

    /**
     * 优化评论加载
     */
    @Keep
    @JvmOverloads
    @JavascriptInterface
    fun onLoadComments(page: Int, size: Int = 10, isDesc: Boolean = false): String {
        return commentPagination?.loadComments(page, size, isDesc).orEmpty().toJson()
    }

    @JavascriptInterface
    fun onReplyUser(replyJs: String, json: String) {
        val formEntity = json.fromJson<CommentTreeEntity>()
        if (formEntity != null) {
            onReplyUserListener(replyJs, formEntity)
        }
    }

    @JavascriptInterface
    fun onReplyNew() {
        onReplyNewListener()
    }
}