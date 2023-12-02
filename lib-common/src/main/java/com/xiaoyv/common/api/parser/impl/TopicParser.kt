package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.LikeEntity
import com.xiaoyv.common.api.parser.entity.TopicDetailEntity
import com.xiaoyv.common.api.parser.fetchStyleBackgroundUrl
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.replaceSmiles
import com.xiaoyv.common.kts.fromJson
import com.xiaoyv.widget.kts.useNotNull
import org.jsoup.nodes.Document

/**
 * @author why
 * @since 12/2/23
 */
fun Document.parserTopic(blogId: String): TopicDetailEntity {
    return select("#news_list > .item, .entry_list > .item").let {
        val entity = TopicDetailEntity()
        entity.id = blogId

        select(".postTopic .re_info small").outerHtml().let {
            val groupValues = "eraseEntry\\(\\s*(.*?)\\s*,\\s*'(.*?)'\\s*\\)".toRegex()
                .find(it)?.groupValues.orEmpty()
            if (entity.id.isBlank()) {
                entity.id = groupValues.getOrNull(1).orEmpty()
            }
            entity.deleteHash = groupValues.getOrNull(2).orEmpty()
        }

        select("#pageHeader a").apply {
            useNotNull(getOrNull(0)) {
                entity.headUrl = attr("href").substringAfterLast("/")
                entity.headerAvatar = select("img.avatar").attr("src").optImageUrl()
                entity.headerName = text()
            }
            useNotNull(getOrNull(1)) {
                entity.subHeadUrl = attr("href")
                entity.subHeaderName = text()
            }
        }

        select(".postTopic").apply {
            entity.time = select(".re_info small")
                .firstOrNull()?.textNodes()?.firstOrNull()?.text()
                .orEmpty().trim()

            entity.userId = select("a.avatar").attr("href").substringAfterLast("/")
            entity.userAvatar = select("a.avatar > span").attr("style")
                .fetchStyleBackgroundUrl().optImageUrl()
            entity.userName = select(".inner strong a").text()
            entity.userSign = select(".inner .sign").text()

            // src="/img/smiles/tv/19.gif" -> src="https://bgm.tv/img/smiles/tv/19.gif"
            entity.content = select(".topic_content").html().replaceSmiles()

            select(".topic_actions").let { topicActions ->
                entity.likeEmojis = topicActions.select(".post_actions .grid a").map { a ->
                    val emoji = TopicDetailEntity.LikeEmoji()
                    emoji.emojiUrl = a.select("img").attr("src").optImageUrl()
                    emoji.likeValue = a.attr("data-like-value")
                    emoji
                }
            }
        }

        if (entity.content.isBlank()) {
            entity.content = select("#columnCrtB .detail").html().replaceSmiles()
        }

        val likeJson = "data_likes_list\\s*=\\s*([\\s\\S]+?);".toRegex()
            .find(html())?.groupValues?.getOrNull(1).orEmpty()

        entity.likeMap = likeJson.fromJson<LikeEntity>() ?: LikeEntity(0)

        entity.title = select("#pageHeader h1")
            .firstOrNull()?.lastChild()?.toString()
            .orEmpty().trim()

        entity.comments = parserBottomComment()
        entity.replyForm = parserReplyForm()
        entity
    }
}