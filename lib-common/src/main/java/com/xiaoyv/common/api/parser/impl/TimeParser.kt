package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.hrefId
import com.xiaoyv.common.api.parser.entity.TimelineEntity
import com.xiaoyv.common.api.parser.fetchStyleBackgroundUrl
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.parseHtml
import com.xiaoyv.common.api.parser.requireNoError
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * Class: [TimeParser]
 *
 * @author why
 * @since 11/25/23
 */
object TimeParser {

    fun Document.parserTimelineForms(isUser: Boolean): List<TimelineEntity> {
        requireNoError()

        val entities = arrayListOf<TimelineEntity>()

        // 用户的时间线单独解析头像和ID
        var userId = ""
        var userAvatar = ""
        if (isUser) {
            userId = select(".headerAvatar > a").attr("href")
                .substringAfterLast("/")
            userAvatar = select(".headerAvatar > a > span").attr("style")
                .fetchStyleBackgroundUrl().optImageUrl()
        }

        select("#timeline ul > li").map {
            val entity = TimelineEntity()

            if (isUser) {
                entity.userId = userId
                entity.avatar = userAvatar
            } else {
                entity.userId = it.select("li").attr("data-item-user")
                entity.avatar = it.select("li a.avatar span").attr("style")
                    .fetchStyleBackgroundUrl().optImageUrl()
            }


            entity.timeText = it.select(".post_actions").text()

            var infoUserActionText = ""
            val infoSpan = it.select("li > .info").ifEmpty {
                it.select("li > .info_full")
            }

            // 右侧人物解析
            var avatar = ""
            var characterSubjectId = ""
            var userId = ""
            infoSpan.select("a").forEach { a ->
                val characterImg = a.select("img.rr")
                if (characterImg.isNotEmpty()) {
                    avatar = characterImg.attr("src").optImageUrl()
                    characterSubjectId = a.hrefId()
                    a.remove()
                }

                val userImage = a.select("a.rr img")
                if (userImage.isNotEmpty()) {
                    avatar = userImage.attr("src").optImageUrl()
                    userId = a.hrefId()
                    a.remove()
                }
            }
            if (avatar.isNotBlank()) {
                entity.character = TimelineEntity.Character(
                    avatar = avatar,
                    subjectId = characterSubjectId,
                    userId = userId
                )
            }

            val nodes = infoSpan.firstOrNull()?.childNodes().orEmpty()
            for (node in nodes) {
                if (node is Element && node.tagName().lowercase() == "div") break
                infoUserActionText += node.outerHtml()
            }
            entity.userActionText = infoUserActionText.parseHtml()


            // images
            val images = it.select(".imgs > a")
            if (images.isNotEmpty()) {
                entity.images = images.map { item ->
                    val subjectId = item.hrefId()
                    val coverUrl = item.select("a img").attr("src").optImageUrl()
                    TimelineEntity.Image(coverUrl, subjectId)
                }
            }

            // collectInfo
            val collectInfo = it.select(".collectInfo")
            val comment = collectInfo.select(".comment").text()
            val scoreStar = collectInfo.select(".starstop-s > span").attr("class")
            if (collectInfo.isNotEmpty()) {
                entity.collectInfo = TimelineEntity.CollectionInfo(scoreStar, comment)
            }

            // Card
            val cardContainer = it.select(".card .container")
            val subjectId =
                cardContainer.select(".container > a").hrefId()
            val coverUrl = cardContainer.select(".cover img").attr("src").optImageUrl()
            val inner = cardContainer.select(".inner")
            val cardTitle = inner.select("p.title").text()
            val cardTip = inner.select("p.tip").text()
            val cardRate = inner.select(".rateInfo .fade").text()
            val cardRateTotal = inner.select(".rateInfo .rate_total").text()
            if (cardContainer.isNotEmpty()) {
                entity.card = TimelineEntity.Card(
                    title = cardTitle,
                    subjectId = subjectId,
                    coverUrl = coverUrl,
                    cardTip = cardTip,
                    cardRate = cardRate,
                    cardRateTotal = cardRateTotal
                )
            }

            entities.add(entity)
        }

        return entities
    }
}