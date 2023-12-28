package com.xiaoyv.common.widget.web.page

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import com.blankj.utilcode.util.ThreadUtils
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.api.parser.entity.BlogDetailEntity
import com.xiaoyv.common.api.parser.entity.CommentTreeEntity
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

    var onReplyUserListener: (String, CommentTreeEntity) -> Unit = { _, _ -> }
    var onReplyNewListener: () -> Unit = {}

    override val pageRoute: String
        get() = "blog"

    init {
        webView.addJavascriptInterface(this, "android")
    }

    suspend fun loadBlogDetail(detailEntity: BlogDetailEntity?) {
        val entity = detailEntity ?: return
        commentPagination.refreshComments(entity.comments)
        entity.comments = emptyList()

        callJs("window.blog.loadBlogDetail(${entity.toJson()})")
    }


    @JavascriptInterface
    fun onReplyUser(replyJs: String, json: String) {
        val formEntity = json.fromJson<CommentTreeEntity>()
        if (formEntity != null) {
            ThreadUtils.runOnUiThread { onReplyUserListener(replyJs, formEntity) }
        }
    }

    @JavascriptInterface
    fun onReplyNew() {
        ThreadUtils.runOnUiThread { onReplyNewListener() }
    }
}