package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.hrefId
import com.xiaoyv.common.api.parser.entity.NotifyEntity
import com.xiaoyv.common.api.parser.fetchStyleBackgroundUrl
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.parseHtml
import com.xiaoyv.common.api.parser.requireNoError
import org.jsoup.nodes.Element

/**
 * @author why
 * @since 12/8/23
 */
fun Element.parserNotify(): List<NotifyEntity> {
    requireNoError()

    return select("#comment_list > div").map { item ->
        val entity = NotifyEntity()
        entity.userId = item.select("a.avatar").hrefId()
        entity.userAvatar = item.select("a.avatar > span").attr("style")
            .fetchStyleBackgroundUrl().optImageUrl()
        entity.userName = item.select(".inner strong a").text()
        item.select(".inner .reply_content").apply {
            entity.replyContent = html().parseHtml()
            entity.title = text()
            entity.titleLink = select("a").attr("href")
        }
        entity
    }
}