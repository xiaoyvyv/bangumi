package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.SuperTopicEntity
import com.xiaoyv.common.api.parser.fetchStyleBackgroundUrl
import com.xiaoyv.common.api.parser.optImageUrl
import org.jsoup.nodes.Document

/**
 * @author why
 * @since 11/26/23
 */
fun Document.parserSuperTopic(): List<SuperTopicEntity> {
    return select("#eden_tpc_list > ul > li").map {
        val superEntity = SuperTopicEntity()
        superEntity.userId = it.select("a.avatar > span").attr("data-user")
        superEntity.userName = it.select("a.avatar").attr("title")
        superEntity.avatarUrl = it.select("a.avatar > span").attr("style")
            .fetchStyleBackgroundUrl().optImageUrl()

        superEntity.title = it.select("a.title").text()
        superEntity.titleLink = it.select("a.title").attr("href")
        superEntity.comment = it.select(".inner small.grey").text()

        superEntity.attachLink = it.select(".inner .row a").attr("href")
        superEntity.attachTitle = it.select(".inner .row a").text()
        superEntity.time = it.select(".inner small.time").text()

        superEntity
    }
}