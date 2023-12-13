package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.BlogDetailEntity
import com.xiaoyv.common.api.parser.entity.BlogEntity
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.api.parser.entity.SampleRelatedEntity
import com.xiaoyv.common.api.parser.hrefId
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.parseCount
import com.xiaoyv.common.api.parser.replaceSmiles
import com.xiaoyv.common.api.parser.requireNoError
import com.xiaoyv.common.kts.decodeUrl
import org.jsoup.nodes.Document

/**
 * 解析日志
 *
 * @author why
 * @since 11/28/23
 */
fun Document.parserBlogList(userCenter: Boolean): List<BlogEntity> {
    requireNoError()
    val div = if (userCenter) "div" else "div.entry"

    return select("#news_list > .item, #entry_list > .item").map {
        val blogEntity = BlogEntity()
        blogEntity.image = it.select(".cover a img").attr("src").optImageUrl()
        blogEntity.title = it.select("$div .title a").text()
        blogEntity.id = it.select("$div .title a").hrefId()

        // 区别解析
        if (userCenter) {
            blogEntity.time = it.select("$div small.time").text()
            it.select("$div .content").apply {
                select("small").remove()
                blogEntity.content = text()
                blogEntity.commentCount = it.select("$div small.orange").text().parseCount()
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
        blogEntity.nestingProfile = userCenter
        blogEntity
    }
}

fun Document.parserBlogDetail(blogId: String): BlogDetailEntity {
    requireNoError()

    return select("#news_list > .item, .entry_list > .item").let {
        val blogEntity = BlogDetailEntity()
        blogEntity.id = blogId

        select(".re_info small").outerHtml().let {
            val groupValues = "eraseEntry\\(\\s*(.*?)\\s*,\\s*'(.*?)'\\s*\\)".toRegex()
                .find(it)?.groupValues.orEmpty()
            if (blogEntity.id.isBlank()) {
                blogEntity.id = groupValues.getOrNull(1).orEmpty()
            }
            blogEntity.deleteHash = groupValues.getOrNull(2).orEmpty()
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
        blogEntity.content = select("#entry_content").html().replaceSmiles()

        // 关联的条目
        select("#related_subject_list > li").forEach { item ->
            val relative = SampleRelatedEntity.Item()
            item.select("li > a.avatar").apply {
                relative.image = select("img").attr("src").optImageUrl()
                relative.title = attr("title")

                relative.imageLink = attr("href")
                relative.titleLink = attr("href")
            }
            blogEntity.related.title = "关联的内容"
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