package com.xiaoyv.common.widget.web.page

import android.annotation.SuppressLint
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.api.parser.entity.BlogCreateEntity
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.kts.fromJson
import com.xiaoyv.common.widget.web.WebBase
import com.xiaoyv.widget.webview.UiWebView

/**
 * Class: [BlogPostView]
 *
 * @author why
 * @since 12/2/23
 */
@SuppressLint("JavascriptInterface")
class BlogPostView(override val webView: UiWebView) : WebBase(webView) {

    suspend fun setPostInfo(info: BlogCreateEntity) {
        val json = info.toJson()
        callJs("window.blogPost.setPostInfo($json);")
    }

    override val pageRoute: String
        get() = "blog-post"

    suspend fun getPostInfo(): BlogCreateEntity? {
        return callJs("window.blogPost.getPostInfo();")
            .replace("\\\"", "\"")
            .trimStart('"')
            .trimEnd('"')
            .fromJson<BlogCreateEntity>()
    }

    suspend fun addRelated(related: MediaDetailEntity.MediaRelative) {
        callJs("window.blogPost.addRelated(${related.toJson()});")
    }

    init {
        webView.addJavascriptInterface(this, "android")
    }
}