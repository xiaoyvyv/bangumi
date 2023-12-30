package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.SuperTopicEntity
import com.xiaoyv.common.api.parser.fetchStyleBackgroundUrl
import com.xiaoyv.common.api.parser.hrefId
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.parseCount
import com.xiaoyv.common.api.parser.requireNoError
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.TopicTimeType
import com.xiaoyv.common.config.annotation.TopicType
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.kts.groupValueOne
import com.xiaoyv.widget.kts.orEmpty
import com.xiaoyv.widget.kts.subListLimit
import org.jsoup.nodes.Document

/**
 * @author why
 * @since 11/26/23
 */
fun Document.parserSuperTopic(): List<SuperTopicEntity> {
    requireNoError()

    val topics = select("#eden_tpc_list > ul > li").map {
        val entity = SuperTopicEntity()
        val titleLink = it.select("a.title").attr("href")
        entity.avatarId = it.select("a.avatar > span").attr("data-user")
        entity.userName = it.select("a.avatar").attr("title")
        entity.avatarUrl = it.select("a.avatar > span").attr("style")
            .fetchStyleBackgroundUrl().optImageUrl()

        entity.id = it.select("a.title").hrefId()
        entity.title = it.select("a.title").text()
        entity.commentCount = it.select(".inner small.grey").text().parseCount()

        entity.attachId = it.select(".inner .row a").hrefId()
        entity.attachTitle = it.select(".inner .row a").text()
        entity.time = it.select(".inner small.time").text().trim().removePrefix("...")

        when {
            titleLink.contains("/blog/") -> {
                entity.pathType = BgmPathType.TYPE_BLOG
            }

            titleLink.contains("/topic/") -> {
                entity.pathType = BgmPathType.TYPE_TOPIC
                entity.topicType = "/topic/(.*?)/".toRegex().groupValueOne(titleLink)

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

    // 新、旧、坟
    if (ConfigHelper.isTopicTimeFlag) {
        var maxGroupTopicId = 0L
        var maxBlogId = 0L
        val groupTopicIds = mutableListOf<Long>()
        val blogIds = mutableListOf<Long>()

        // 读取最新的日志ID和小组话题ID
        topics.forEach {
            if (it.topicType == TopicType.TYPE_GROUP) {
                val topicId = it.id.toLongOrNull().orEmpty()
                if (topicId > maxGroupTopicId) {
                    maxGroupTopicId = topicId
                }

                groupTopicIds.add(topicId)
            }

            if (it.pathType == BgmPathType.TYPE_BLOG) {
                val blogId = it.id.toLongOrNull().orEmpty()
                if (blogId > maxBlogId) {
                    maxBlogId = blogId
                }

                blogIds.add(blogId)
            }
        }

        // 从大到小排序
        val newGroupTopicIds = groupTopicIds.sortedDescending().subListLimit(5)
        val newBlogIds = blogIds.sortedDescending().subListLimit(5)

        topics.onEach { item ->
            item.timeType.clear()

            val id = item.id.toLongOrNull().orEmpty()

            // 小组话题：坟贴、新帖、火标记
            if (item.topicType == TopicType.TYPE_GROUP && maxGroupTopicId != 0L) {
                if (id < maxGroupTopicId - 5000) {
                    item.timeType.add(TopicTimeType.TYPE_OLDEST)
                } else if (id < maxGroupTopicId - 1000) {
                    item.timeType.add(TopicTimeType.TYPE_OLD)
                }

                if (newGroupTopicIds.contains(id)) {
                    item.timeType.add(TopicTimeType.TYPE_NEW)
                }
                if (item.commentCount >= 100) {
                    item.timeType.add(TopicTimeType.TYPE_HOT)
                }
            }

            // 日志话题：坟贴、新帖、火标记
            if (item.pathType == BgmPathType.TYPE_BLOG && maxBlogId != 0L) {
                if (id < maxBlogId - 5000) {
                    item.timeType.add(TopicTimeType.TYPE_OLDEST)
                } else if (id < maxBlogId - 1000) {
                    item.timeType.add(TopicTimeType.TYPE_OLD)
                }

                if (newBlogIds.contains(id)) {
                    item.timeType.add(TopicTimeType.TYPE_NEW)
                }
                if (item.commentCount >= 100) {
                    item.timeType.add(TopicTimeType.TYPE_HOT)
                }
            }
        }
    }
    return topics
}