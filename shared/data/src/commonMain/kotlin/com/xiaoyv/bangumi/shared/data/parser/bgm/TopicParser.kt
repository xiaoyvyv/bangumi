@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.parser.bgm

import com.fleeksoft.ksoup.nodes.Element
import com.xiaoyv.bangumi.shared.core.types.CommentType
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.RakuenFlagType
import com.xiaoyv.bangumi.shared.core.types.RakuenIdType
import com.xiaoyv.bangumi.shared.core.utils.firsTextNode
import com.xiaoyv.bangumi.shared.core.utils.groupValueOne
import com.xiaoyv.bangumi.shared.core.utils.hrefId
import com.xiaoyv.bangumi.shared.core.utils.hrefLongId
import com.xiaoyv.bangumi.shared.core.utils.lastTextNode
import com.xiaoyv.bangumi.shared.core.utils.parseAgoToTimestamp
import com.xiaoyv.bangumi.shared.core.utils.parseAsHtml
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeMap
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEmojiParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroup
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeImages
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMono
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoInfo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReaction
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeTopic
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeTopicDetail
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUser
import com.xiaoyv.bangumi.shared.data.parser.BaseParser
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList

class TopicParser(private val commentParser: CommentParser) : BaseParser() {
    private var isTopicTimeFlag = true

    suspend fun Element.fetchRakuenTopicConverted(): List<ComposeTopic> {
        requireNoError()

        val topics = select("#eden_tpc_list > ul > li").map { item ->
            val key = item.attr("id")
            val id = item.select("a.title").hrefLongId()
            val username = item.attr("data-item-user")
            val nickname = item.select("a.avatar").attr("title")
            val avatarUrl = item.select("a.avatar > span").styleAvatarUrl()

            val title = item.select("a.title").text()
            val commentCount = item.select(".inner small.grey").text().parseCount()
            val time = item.select(".inner small.time").text().trim().removePrefix("...")

            val attachId = item.select(".inner .row a").hrefId()
            val attachLongId = item.select(".inner .row a").hrefLongId()
            val attachTitle = item.select(".inner .row a").text()

            val idType = when {
                key.contains(RakuenIdType.TYPE_SUBJECT) -> RakuenIdType.TYPE_SUBJECT
                key.contains(RakuenIdType.TYPE_EP) -> RakuenIdType.TYPE_EP
                key.contains(RakuenIdType.TYPE_GROUP) -> RakuenIdType.TYPE_GROUP
                key.contains(RakuenIdType.TYPE_PERSON) -> RakuenIdType.TYPE_PERSON
                key.contains(RakuenIdType.TYPE_CRT) -> RakuenIdType.TYPE_CRT
                key.contains(RakuenIdType.TYPE_INDEX) -> RakuenIdType.TYPE_INDEX
                key.contains(RakuenIdType.TYPE_BLOG) -> RakuenIdType.TYPE_BLOG
                else -> RakuenIdType.TYPE_UNKNOWN
            }

            // 用户能发帖的条目
            val user = when (idType) {
                RakuenIdType.TYPE_SUBJECT,
                RakuenIdType.TYPE_GROUP,
                RakuenIdType.TYPE_BLOG,
                    -> ComposeUser(
                    id = avatarUrl.avatarUrlId(username),
                    username = username,
                    nickname = nickname,
                    avatar = ComposeImages.fromUrl(avatarUrl)
                )

                else -> ComposeUser.Empty
            }

            // 附带条目名称的条目
            val subject = when (idType) {
                RakuenIdType.TYPE_SUBJECT,
                RakuenIdType.TYPE_EP,
                RakuenIdType.TYPE_BLOG,
                    -> {
                    ComposeSubject(
                        id = attachLongId,
                        name = attachTitle,
                        nameCn = attachTitle,
                        images = ComposeImages.fromUrl(avatarUrl)
                    )
                }

                else -> ComposeSubject.Empty
            }

            // 虚拟人物和现实人物条目
            val mono = when (idType) {
                RakuenIdType.TYPE_PERSON -> {
                    ComposeMonoDisplay(
                        type = MonoType.PERSON,
                        info = ComposeMonoInfo(
                            mono = ComposeMono(
                                id = id,
                                name = title,
                                images = ComposeImages.fromUrl(avatarUrl),
                            )
                        )
                    )
                }

                RakuenIdType.TYPE_CRT -> {
                    ComposeMonoDisplay(
                        type = MonoType.CHARACTER,
                        info = ComposeMonoInfo(
                            mono = ComposeMono(
                                id = id,
                                name = title,
                                images = ComposeImages.fromUrl(avatarUrl),
                            )
                        )
                    )
                }

                else -> ComposeMonoDisplay.Empty
            }

            ComposeTopic(
                id = id,
                topicType = idType,
                title = title,
                updatedAt = time.parseAgoToTimestamp(),
                replyCount = commentCount,
                creator = user,
                subject = subject,
                group = if (idType == RakuenIdType.TYPE_GROUP) ComposeGroup(name = attachId, title = attachTitle) else ComposeGroup.Empty,
                mono = mono
            )
        }

        // 新、旧、坟
        return if (isTopicTimeFlag) convertTopicFlags(topics) else topics
    }

    /**
     * 解析日志类型话题详情
     */
    suspend fun Element.fetchRakuenBlogDetailConverted(id: Long): ComposeTopicDetail {
        requireNoError()
        val main = select("#main")
        val viewEntry = main.select("#viewEntry")
        val authorBar = viewEntry.select(".author")
        val title = viewEntry.select(".header > h1.title").lastTextNode()

        val subjects = viewEntry.select("#related_subject_list > li").map { item ->
            ComposeSubject(
                id = item.select("a.avatar").hrefLongId(),
                name = item.select(".ll a").text(),
                nameCn = item.select(".ll a").text(),
                images = ComposeImages.fromUrl(item.select("img").src())
            )
        }
        val time = viewEntry.select(".re_info").text().substringAfter("/").trim()

        val userAvatar = authorBar.select("img.avatar").src()
        val username = authorBar.select("a.avatar").hrefId()
        val user = ComposeUser(
            id = userAvatar.avatarUrlId(username),
            username = username,
            nickname = authorBar.select("a.avatar").text(),
            avatar = ComposeImages.fromUrl(userAvatar),
        )

        val content = viewEntry.select("#entry_content").html()

        return ComposeTopicDetail(
            id = id,
            type = RakuenIdType.TYPE_BLOG,
            contentId = id.toString(),
            title = title,
            subjects = subjects.toPersistentList(),
            user = user,
            time = time,
            content = content,
            contentHtml = content.parseAsHtml(),
            replyParam = with(commentParser) { with(main) { parserReplyForm() } },
            comments = with(commentParser) { with(main) { parserBottomComment(CommentType.BLOG_COMMENT) }.toPersistentList() },
            reactions = covertReactions()
        )
    }

    /**
     * 解析日志普通类型话题详情
     */
    suspend fun Element.fetchRakuenTopicDetailConverted(
        id: Long,
        @RakuenIdType type: String,
    ): ComposeTopicDetail {
        requireNoError()
        val pageHeader = select("#pageHeader")
        val title = pageHeader.select("h1").lastTextNode()

        val subject = when (type) {
            RakuenIdType.TYPE_SUBJECT,
            RakuenIdType.TYPE_EP,
                -> {
                ComposeSubject(
                    id = pageHeader.select("a.avatar").hrefLongId(),
                    name = pageHeader.select("a.avatar").attr("title"),
                    nameCn = pageHeader.select("a.avatar").attr("title"),
                    images = ComposeImages.fromUrl(pageHeader.select("img.avatar").src())
                )
            }

            else -> ComposeSubject.Empty
        }

        val group = when (type) {
            RakuenIdType.TYPE_GROUP -> {
                ComposeGroup(
                    name = pageHeader.select("a.avatar").hrefId(),
                    title = pageHeader.select("a.avatar").text().trim(),
                    images = ComposeImages.fromUrl(pageHeader.select("img.avatar").src())
                )
            }

            else -> ComposeGroup.Empty
        }

        val mono = when (type) {
            RakuenIdType.TYPE_PERSON,
            RakuenIdType.TYPE_CRT,
                -> {
                ComposeMonoDisplay(
                    info = ComposeMonoInfo(
                        mono = ComposeMono(
                            id = pageHeader.select("a.avatar").hrefLongId(),
                            name = pageHeader.select("a.avatar").attr("title"),
                            images = ComposeImages.fromUrl(pageHeader.select("img.avatar").src()),
                        )
                    ),
                    type = if (type == RakuenIdType.TYPE_CRT) MonoType.CHARACTER else MonoType.PERSON
                )
            }

            else -> ComposeMonoDisplay.Empty
        }

        val user: ComposeUser
        val time: String
        val content: String
        val emojiParam: ComposeEmojiParam
        val contentId: String

        when (type) {
            RakuenIdType.TYPE_SUBJECT,
            RakuenIdType.TYPE_GROUP,
                -> {
                val postTopic = select(".postTopic")
                val userAvatar = postTopic.select("a.avatar > span").styleAvatarUrl()
                val article = postTopic.select(".topic_content").html()

                content = article
                contentId = postTopic.attr("id").substringAfter("_")

                user = ComposeUser(
                    id = userAvatar.substringAfterLast("/").parseCount().toLong(),
                    avatar = ComposeImages.fromUrl(userAvatar),
                    username = postTopic.select("a.avatar").hrefId(),
                    nickname = postTopic.select(".inner strong").text(),
                    sign = postTopic.select(".inner .sign").text()
                )
                time = postTopic.select(".re_info .action small")
                    .firsTextNode()
                    .substringAfter("-")
                    .trim()
                emojiParam = postTopic.select(".topic_actions .like_dropdown").parserLikeParam()
            }

            else -> {
                contentId = ""
                user = ComposeUser.Empty
                time = ""
                content = ""
                emojiParam = ComposeEmojiParam()
            }
        }

        val commentType = CommentType.fromRakuenIdType(type)

        return ComposeTopicDetail(
            id = id,
            contentId = contentId,
            type = type,
            title = title,
            subjects = persistentListOf(subject),
            group = group,
            user = user,
            time = time,
            mono = mono,
            content = content,
            contentHtml = content.parseAsHtml(),
            emojiParam = emojiParam,
            replyParam = with(commentParser) { parserReplyForm() },
            comments = with(commentParser) { parserBottomComment(commentType) }.toPersistentList(),
            reactions = covertReactions()
        )
    }

    /**
     * 话题全部的贴贴列表
     */
    private fun Element.covertReactions(): SerializeMap<String, SerializeList<ComposeReaction>> {
        val likeJson = "data_likes_list\\s*=\\s*([\\s\\S]+?);\\s+?</script>"
            .toRegex()
            .groupValueOne(html())
        if (likeJson.isBlank()) return persistentMapOf()
        return ComposeReaction.fromJson(likeJson)
    }

    /**
     * 标记旧帖，新帖，坟贴，火热
     */
    private fun convertTopicFlags(topics: List<ComposeTopic>): List<ComposeTopic> {
        var maxGroupTopicId = 0L
        var maxBlogId = 0L
        val groupTopicIds = mutableListOf<Long>()
        val blogIds = mutableListOf<Long>()

        // 读取最新的日志ID和小组话题ID
        topics.forEach {
            if (it.topicType == RakuenIdType.TYPE_GROUP) {
                val topicId = it.id
                if (topicId > maxGroupTopicId) {
                    maxGroupTopicId = topicId
                }

                groupTopicIds.add(topicId)
            }

            if (it.topicType == RakuenIdType.TYPE_BLOG) {
                val blogId = it.id
                if (blogId > maxBlogId) {
                    maxBlogId = blogId
                }

                blogIds.add(blogId)
            }
        }

        // 从大到小排序
        val newGroupTopicIds = groupTopicIds.sortedDescending().take(5)
        val newBlogIds = blogIds.sortedDescending().take(5)

        return topics.map { item ->
            val flags = mutableListOf<String>()

            val id = item.id

            // 小组话题：坟贴、新帖、火标记
            if (item.topicType == RakuenIdType.TYPE_GROUP && maxGroupTopicId != 0L) {
                if (id < maxGroupTopicId - 10000) {
                    flags.add(RakuenFlagType.TYPE_OLDEST)
                } else if (id < maxGroupTopicId - 4000) {
                    flags.add(RakuenFlagType.TYPE_OLD)
                }

                if (newGroupTopicIds.contains(id)) {
                    flags.add(RakuenFlagType.TYPE_NEW)
                }
                if (item.replyCount >= 100) {
                    flags.add(RakuenFlagType.TYPE_HOT)
                }
            }

            // 日志话题：坟贴、新帖、火标记
            if (item.topicType == RakuenIdType.TYPE_BLOG && maxBlogId != 0L) {
                if (id < maxBlogId - 5000) {
                    flags.add(RakuenFlagType.TYPE_OLDEST)
                } else if (id < maxBlogId - 1000) {
                    flags.add(RakuenFlagType.TYPE_OLD)
                }

                if (newBlogIds.contains(id)) {
                    flags.add(RakuenFlagType.TYPE_NEW)
                }
                if (item.replyCount >= 50) {
                    flags.add(RakuenFlagType.TYPE_HOT)
                }
            }

            item.copy(flags = flags.toPersistentList())
        }
    }


}