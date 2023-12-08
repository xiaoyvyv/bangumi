package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.SuperTopicEntity
import com.xiaoyv.common.api.parser.fetchStyleBackgroundUrl
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.parseCount
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.TopicType
import org.jsoup.nodes.Document

/**
 * @author why
 * @since 11/26/23
 */
fun Document.parserSuperTopic(): List<SuperTopicEntity> {
    return select("#eden_tpc_list > ul > li").map {
        val entity = SuperTopicEntity()
        val titleLink = it.select("a.title").attr("href")
        entity.avatarId = it.select("a.avatar > span").attr("data-user")
        entity.userName = it.select("a.avatar").attr("title")
        entity.avatarUrl = it.select("a.avatar > span").attr("style")
            .fetchStyleBackgroundUrl().optImageUrl()

        entity.id = it.select("a.title").attr("href").substringAfterLast("/")
        entity.title = it.select("a.title").text()
        entity.commentCount = it.select(".inner small.grey").text().parseCount()

        entity.attachId = it.select(".inner .row a").attr("href").substringAfterLast("/")
        entity.attachTitle = it.select(".inner .row a").text()
        entity.time = it.select(".inner small.time").text().trim().removePrefix("...")

        when {
            titleLink.contains("/blog/") -> {
                entity.pathType = BgmPathType.TYPE_BLOG
            }

            titleLink.contains("/topic/") -> {
                entity.pathType = BgmPathType.TYPE_TOPIC
                entity.topicType = "/topic/(.*?)/".toRegex()
                    .find(titleLink)?.groupValues?.getOrNull(1)
                    .orEmpty()

                if (entity.topicType == TopicType.TYPE_CRT) {
                    entity.attachTitle = "虚拟人物"
                }
                if (entity.topicType == TopicType.TYPE_PERSON) {
                    entity.attachTitle = "现实人物"
                }
            }
        }

        entity
    }
}