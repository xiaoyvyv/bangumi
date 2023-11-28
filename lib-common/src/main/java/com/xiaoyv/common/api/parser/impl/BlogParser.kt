package com.xiaoyv.common.api.parser.impl

import androidx.core.text.parseAsHtml
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