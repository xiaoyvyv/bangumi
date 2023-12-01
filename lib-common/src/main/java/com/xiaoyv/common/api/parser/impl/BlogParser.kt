package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.BlogDetailEntity
import com.xiaoyv.common.api.parser.entity.BlogEntity
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.parseHtml
import org.jsoup.nodes.Document

/**
 * 解析日志
 *
 * @author why
 * @since 11/28/23
 */
fun Document.parserBlogList(mediaType: String): List<BlogEntity> {
    return select("#news_list > .item, .entry_list > .item").map {
        val blogEntity = BlogEntity()

        blogEntity.image = it.select(".cover a img").attr("src").optImageUrl()
        blogEntity.title = it.select(".entry .title a").text()
        blogEntity.id = it.select(".entry .title a").attr("href").substringAfterLast("/")
        blogEntity.timeline = it.select(".entry .time").outerHtml().parseHtml()
        blogEntity.time = it.select(".entry .time small").lastOrNull()?.text().orEmpty()
            .replace("/", "").trim()
        blogEntity.content = it.select(".entry .content").text()
        blogEntity.comment = it.select(".entry small").text()
        blogEntity.mediaType = mediaType
        blogEntity
    }
}

fun Document.parserBlogDetail(blogId: String): BlogDetailEntity {
    return select("#news_list > .item, .entry_list > .item").let {
        val blogEntity = BlogDetailEntity()
        blogEntity.id = blogId
        select("#pageHeader").apply {
            select("#pageHeader a.avatar").apply {
                blogEntity.userId = attr("href").substringAfterLast("/")
                blogEntity.userAvatar = select("img.avatar").attr("src").optImageUrl()
                blogEntity.userName = text()
            }
        }

        blogEntity.title = select("#pageHeader h1")
            .firstOrNull()?.lastChild()?.toString()
            .orEmpty().trim()

        blogEntity.time = select("#columnA .re_info small")
            .firstOrNull()?.text()?.replace("/", "")
            .orEmpty().trim()

        // src="/img/smiles/tv/19.gif" -> src="https://bgm.tv/img/smiles/tv/19.gif"
        blogEntity.content = select("#entry_content").html()
            .replace(
                "src=\"/img/smiles/(.*?)\"".toRegex(),
                "src=\"${BgmApiManager.URL_BASE_WEB}/img/smiles/\$1\""
            )
        blogEntity
    }
}