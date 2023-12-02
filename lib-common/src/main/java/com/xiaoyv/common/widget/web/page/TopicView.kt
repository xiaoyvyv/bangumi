package com.xiaoyv.common.widget.web.page

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.api.parser.entity.CommentTreeEntity
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.api.parser.entity.TopicDetailEntity
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.fromJson
import com.xiaoyv.common.widget.web.WebBase
import com.xiaoyv.widget.kts.useNotNull
import com.xiaoyv.widget.webview.UiWebView


/**
 * Class: [TopicView]
 *
 * @author why
 * @since 11/30/23
 */
@SuppressLint("JavascriptInterface")
class TopicView(override val webView: UiWebView) : WebBase(webView) {
    var onPreviewImageListener: (String, List<String>) -> Unit = { _, _ -> }
    var onReplyUserListener: (String, CommentTreeEntity) -> Unit = { _, _ -> }
    var onReplyNewListener: () -> Unit = {}
    var onNeedLoginListener: () -> Unit = {}
    var onClickRelatedListener: (MediaDetailEntity.MediaRelative) -> Unit = { }

    override val pageRoute: String
        get() = "topic"

    init {
        webView.addJavascriptInterface(this, "android")
    }

    @JavascriptInterface
    fun onPreviewImage(imageUrl: String, imageUrls: Array<String>) {
        onPreviewImageListener(imageUrl, imageUrls.toList())
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

    @JavascriptInterface
    fun onNeedLogin() {
        debugLog { "请登录后再操作" }
        onNeedLoginListener()
    }

    @JavascriptInterface
    fun onClickRelated(json: String) {
        useNotNull(json.fromJson<MediaDetailEntity.MediaRelative>()) {
            onClickRelatedListener(this)
        }
    }

    suspend fun loadTopicDetail(detailEntity: TopicDetailEntity?) {
        val entity = detailEntity ?: return
        callJs("window.topic.loadTopicDetail(${entity.toJson()})")
    }
}