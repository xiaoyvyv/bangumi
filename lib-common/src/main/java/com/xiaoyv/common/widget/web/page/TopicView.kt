package com.xiaoyv.common.widget.web.page

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import androidx.annotation.Keep
import com.blankj.utilcode.util.ThreadUtils
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.api.parser.entity.CommentTreeEntity
import com.xiaoyv.common.api.parser.entity.TopicDetailEntity
import com.xiaoyv.common.kts.fromJson
import com.xiaoyv.common.widget.web.WebBase
import com.xiaoyv.widget.webview.UiWebView


/**
 * Class: [TopicView]
 *
 * @author why
 * @since 11/30/23
 */
@SuppressLint("JavascriptInterface")
class TopicView(override val webView: UiWebView) : WebBase(webView) {
    var onReplyUserListener: (String, CommentTreeEntity) -> Unit = { _, _ -> }
    var onReplyNewListener: () -> Unit = {}

    override val pageRoute: String
        get() = "topic"

    init {
        webView.addJavascriptInterface(this, "android")
    }

    suspend fun loadTopicDetail(detailEntity: TopicDetailEntity?) {
        val entity = detailEntity ?: return
        commentPagination.refreshComments(entity.comments)
        entity.comments = emptyList()

        callJs("window.topic.loadTopicDetail(${entity.toJson()})")
    }

    @Keep
    @JavascriptInterface
    fun onReplyUser(replyJs: String, json: String) {
        val formEntity = json.fromJson<CommentTreeEntity>()
        if (formEntity != null) {
            ThreadUtils.runOnUiThread {
                onReplyUserListener(replyJs, formEntity)
            }
        }
    }

    @Keep
    @JavascriptInterface
    fun onReplyNew() {
        ThreadUtils.runOnUiThread {
            onReplyNewListener()
        }
    }
}