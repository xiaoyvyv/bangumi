@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.parser.bgm

import com.fleeksoft.ksoup.nodes.Element
import com.xiaoyv.bangumi.shared.core.types.AppParserDsl
import com.xiaoyv.bangumi.shared.core.types.TopicDetailType
import com.xiaoyv.bangumi.shared.core.utils.href
import com.xiaoyv.bangumi.shared.core.utils.hrefId
import com.xiaoyv.bangumi.shared.core.utils.hrefLongId
import com.xiaoyv.bangumi.shared.core.utils.parseAgoToTimestamp
import com.xiaoyv.bangumi.shared.data.constant.userImage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroup
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeImages
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.topic.ComposeTopic
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import com.xiaoyv.bangumi.shared.data.parser.BaseParser

@AppParserDsl
class TopicTableParser : BaseParser() {

    suspend fun Element.fetchIndexTopicConverted(): List<ComposeTopic> {
        requireNoError()

        return select(".topic-list > li").map { item ->
            // 目录内复用解析填充
            item.select("#comment_box").text()
            item.attr("attr-index-related")
            val avatarUrl = item.select("a.avatar > span").styleAvatarUrl()
            val username = item.attr("data-item-user")
            val related = item.select("span.related > a")
            val relatedUrl = related.href()
            val relatedTitle = related.text()
            val time = item.select(".time").text()

            val subject: ComposeSubject
            val group: ComposeGroup
            val type: String
            when {
                relatedUrl.contains("subject") -> {
                    type = TopicDetailType.TYPE_SUBJECT
                    group = ComposeGroup.Empty
                    subject = ComposeSubject(
                        name = relatedTitle,
                        id = related.hrefLongId()
                    )
                }

                relatedUrl.contains("group") -> {
                    type = TopicDetailType.TYPE_GROUP
                    subject = ComposeSubject.Empty
                    group = ComposeGroup(
                        title = relatedTitle,
                        name = related.hrefId()
                    )
                }

                else -> {
                    type = TopicDetailType.TYPE_UNKNOWN
                    group = ComposeGroup.Empty
                    subject = ComposeSubject.Empty
                }
            }

            ComposeTopic(
                creator = ComposeUser(
                    id = avatarUrl.avatarUrlId(username),
                    username = username,
                    nickname = item.select("span.author > a").text()
                ),
                id = item.select("a.title").hrefLongId(),
                updatedAt = time.parseAgoToTimestamp(),
                title = item.select("a.title").text(),
                replyCount = item.select("small").text().parseCount(),
                topicType = type,
                subject = subject,
                group = group
            )
        }
    }

    suspend fun Element.fetchSubjectTopicTableItem(): List<ComposeTopic> {
        requireNoError()
        return select(".topic_list > tbody > tr").map {
            val tds = it.select("tr td")
            val time = tds.getOrNull(3)?.text().orEmpty()
            ComposeTopic(
                id = it.select(".subject a").hrefLongId(),
                topicType = TopicDetailType.TYPE_SUBJECT,
                title = it.select(".subject").text(),
                creator = ComposeUser(
                    nickname = tds.getOrNull(1)?.text().orEmpty(),
                    username = tds.getOrNull(1)?.select("a")?.hrefId().orEmpty(),
                ),
                replyCount = tds.getOrNull(2)?.text().orEmpty().parseCount(),
                updatedAt = time.parseAgoToTimestamp(),
            )
        }
    }

    suspend fun Element.fetchGroupTopicTableItem(): List<ComposeTopic> {
        requireNoError()
        return select(".topic_list > tbody > tr").mapNotNull {
            val tds = it.select("tr td")
            if (tds.size < 4) return@mapNotNull null
            val time = tds[3].text()

            ComposeTopic(
                id = tds[0].select("a").hrefLongId(),
                topicType = TopicDetailType.TYPE_GROUP,
                replyCount = tds[0].select("small").text().parseCount(),
                title = tds[0].select("a").text(),
                group = ComposeGroup(
                    name = tds[1].select("a").hrefId(),
                    title = tds[1].text()
                ),
                creator = ComposeUser(
                    nickname = tds[2].text(),
                    username = tds[2].select("a").hrefId(),
                    avatar = ComposeImages.fromUrl(userImage(tds[2].select("a").hrefId()))
                ),
                updatedAt = time.parseAgoToTimestamp(),
            )
        }
    }
}
