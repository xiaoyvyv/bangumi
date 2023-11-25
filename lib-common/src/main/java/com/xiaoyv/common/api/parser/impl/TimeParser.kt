package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.TimelineEntity
import com.xiaoyv.common.api.parser.fetchStyleBackgroundUrl
import com.xiaoyv.common.api.parser.optImageUrl
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * Class: [TimeParser]
 *
 * @author why
 * @since 11/25/23
 */
object TimeParser {

    fun Document.parserTimelineForms(): List<TimelineEntity> {
        val entities = arrayListOf<TimelineEntity>()

        select("#timeline ul > li").map {
            val entity = TimelineEntity()

            entity.userId = it.select("li").attr("data-item-user")
            entity.avatar = it.select("li a.avatar span").attr("style")
                .fetchStyleBackgroundUrl().optImageUrl()
            entity.timeText = it.select(".post_actions").text()

            var infoUserActionText = ""
            val infoSpan = it.select("li > .info")

            // 右侧人物解析
            var avatar = ""
            var characterSubjectId = ""
            var userId = ""
            infoSpan.select("a").forEach { a ->
                val characterImg = a.select("img.rr")
                if (characterImg.isNotEmpty()) {
                    avatar = characterImg.attr("src").optImageUrl()
                    characterSubjectId = a.attr("href").substringAfterLast("/")
                    a.remove()
                }

                val userImage = a.select("a.rr img")
                if (userImage.isNotEmpty()) {
                    avatar = userImage.attr("src").optImageUrl()
                    userId = a.attr("href").substringAfterLast("/")
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
            entity.userActionText = infoUserActionText


            // images
            val images = it.select(".imgs > a")
            if (images.isNotEmpty()) {
                entity.images = images.map { item ->
                    val subjectId = item.attr("href").substringAfterLast("/")
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
                cardContainer.select(".container > a").attr("href").substringAfterLast("/")
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