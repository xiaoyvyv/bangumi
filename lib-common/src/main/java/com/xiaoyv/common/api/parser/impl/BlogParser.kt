package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.BlogDetailEntity
import com.xiaoyv.common.api.parser.entity.BlogEntity
import com.xiaoyv.common.api.parser.entity.CreatePostEntity
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.api.parser.entity.SampleRelatedEntity
import com.xiaoyv.common.api.parser.hrefId
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.parseCount
import com.xiaoyv.common.api.parser.preHandleHtml
import com.xiaoyv.common.api.parser.requireNoError
import com.xiaoyv.common.config.bean.PostAttach
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.decodeUrl
import com.xiaoyv.common.kts.i18n
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * 解析日志
 *
 * @author why
 * @since 11/28/23
 */
fun Document.parserBlogList(isQueryTargetUser: Boolean, isQueryMine: Boolean): List<BlogEntity> {
    requireNoError()
    val div = if (isQueryTargetUser) "div" else "div.entry"

    return select("#news_list > .item, #entry_list > .item").map {
        val blogEntity = BlogEntity()
        blogEntity.image = it.select(".cover a img").attr("src").optImageUrl()
        blogEntity.title = it.select("$div .title a").text()
        blogEntity.id = it.select("$div .title a").hrefId()

        // 区别解析
        if (isQueryTargetUser) {
            blogEntity.time = it.select("$div small.time").text()
            it.select("$div .content").apply {
                select("small").remove()
                blogEntity.commentCount = it.select("$div small.orange").text().parseCount()
                blogEntity.content = text()
            }
        } else {
            blogEntity.time = it.select("$div .time small").lastOrNull()?.text().orEmpty()
                .replace("/", "").trim()
            it.select("$div .content").apply {
                blogEntity.commentCount = select("small").remove().text().parseCount()
                blogEntity.content = text()
            }
        }

        it.select("$div .time a.blue").apply {
            blogEntity.recentUserName = getOrNull(0)?.text().orEmpty()
            blogEntity.recentUserId = getOrNull(0)?.hrefId().orEmpty()
            blogEntity.mediaName = getOrNull(1)?.text().orEmpty()
        }
        // 是否嵌套在用户中心的解析
        blogEntity.nestingProfile = isQueryMine
        blogEntity
    }
}

fun Document.parserBlogDetail(blogId: String): BlogDetailEntity {
    requireNoError()

    return select("#news_list > .item, .entry_list > .item").let {
        val blogEntity = BlogDetailEntity()
        blogEntity.id = blogId

        // 自己发布日志，解析日志 ID
        select(".re_info small").outerHtml().let {
            val groupValues = "eraseEntry\\(\\s*(.*?)\\s*,\\s*'(.*?)'\\s*\\)".toRegex()
                .find(it)?.groupValues.orEmpty()
            if (blogEntity.id.isBlank()) {
                blogEntity.id = groupValues.getOrNull(1).orEmpty()
            }
        }

        select("#pageHeader a.avatar").apply {
            blogEntity.userId = hrefId()
            blogEntity.userAvatar = select("img.avatar").attr("src").optImageUrl()
            blogEntity.userName = text()
        }

        blogEntity.title = select("#pageHeader h1")
            .firstOrNull()?.lastChild()?.toString()
            .orEmpty().trim()

        blogEntity.time = select("#columnA .re_info small")
            .firstOrNull()?.textNodes()?.firstOrNull()?.text()
            .orEmpty().substringBefore("/").trim()

        // src="/img/smiles/tv/19.gif" -> src="https://bgm.tv/img/smiles/tv/19.gif"
        blogEntity.content = select("#entry_content").html().preHandleHtml()

        // 关联的条目
        select("#related_subject_list > li").forEach { item ->
            val relative = SampleRelatedEntity.Item()
            item.select("li > a.avatar").apply {
                relative.image = select("img").attr("src").optImageUrl()
                relative.title = attr("title")

                relative.imageLink = attr("href")
                relative.titleLink = attr("href")
            }
            blogEntity.related.title = i18n(CommonString.parse_relation)
            blogEntity.related.items.add(relative)
        }

        blogEntity.tags = select(".tags > a").map { item ->
            val tag = MediaDetailEntity.MediaTag()
            tag.tagName = item.hrefId().decodeUrl()
            tag.title = item.text()
            tag.url = item.attr("href")
            tag.mediaType = item.attr("href").trim('/').substringBefore("/")
            tag
        }
        blogEntity.comments = parserBottomComment()
        blogEntity.replyForm = parserReplyForm()
        blogEntity
    }
}

/**
 * 解析编辑日志的信息
 */
fun Element.parserBlogEditInfo(): CreatePostEntity {
    val postEntity = CreatePostEntity()
    select("#editTopicForm").apply {
        postEntity.title = select("input[name=title]").attr("value").trim()
        postEntity.content = select("textarea[name=content]").text()
        postEntity.tags = select("input[name=tags]").attr("value").trim()
        val public = select("input[name=public][checked]").attr("value")
        postEntity.isPublic = public == "1"
    }
    return postEntity
}

/**
 * 解析编辑日志的关联条目
 */
fun Element.parserBlogEditAttach(): List<PostAttach> {
    return select("#related_subject_list > li").map { item ->
        val attach = PostAttach()
        attach.id = item.select("a.avatar").hrefId()
        attach.title = item.select("a.avatar").attr("title")
        attach.image = item.select("a.avatar img").attr("src").optImageUrl()
        attach
    }
}