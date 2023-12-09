package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.BlogDetailEntity
import com.xiaoyv.common.api.parser.entity.BlogEntity
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
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
fun Document.parserBlogList(mediaType: String): List<BlogEntity> {
    requireNoError()

    return select("#news_list > .item, .entry_list > .item").map {
        val blogEntity = BlogEntity()
        blogEntity.mediaType = mediaType
        blogEntity.image = it.select(".cover a img").attr("src").optImageUrl()
        blogEntity.title = it.select(".entry .title a").text()
        blogEntity.id = it.select(".entry .title a").attr("href").substringAfterLast("/")
        blogEntity.time = it.select(".entry .time small").lastOrNull()?.text().orEmpty()
            .replace("/", "").trim()
        it.select(".entry .content").apply {
            blogEntity.commentCount = select("small").remove().text().parseCount()
            blogEntity.content = text()
        }
        it.select(".entry .time a.blue").apply {
            blogEntity.recentUserName = getOrNull(0)?.text().orEmpty()
            blogEntity.recentUserId = getOrNull(0)?.attr("href").orEmpty()
                .substringAfterLast("/")
            blogEntity.mediaName = getOrNull(1)?.text().orEmpty()
        }
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
            blogEntity.userId = attr("href").substringAfterLast("/")
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

        blogEntity.related = select("#related_subject_list > li").map { item ->
            val relative = MediaDetailEntity.MediaRelative()

            item.select("li > a.avatar").apply {
                relative.id = attr("href").substringAfterLast("/")
                relative.cover = select("img").attr("src").optImageUrl()
                relative.titleCn = attr("title")
                relative.titleNative = attr("title")
            }
            relative
        }

        blogEntity.tags = select(".tags > a").map { item ->
            val tag = MediaDetailEntity.MediaTag()
            tag.tagName = item.attr("href").substringAfterLast("/").decodeUrl()
            tag.title = item.text()
            tag.url = item.attr("href")
            tag
        }
        blogEntity.comments = parserBottomComment()
        blogEntity.replyForm = parserReplyForm()
        blogEntity
    }
}