package com.xiaoyv.common.api.parser

import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.api.parser.entity.HomeImageCardEntity
import com.xiaoyv.common.api.parser.entity.HomeIndexEntity
import com.xiaoyv.common.kts.debugLog
import org.jsoup.nodes.Document

/**
 * Class: [HomeParser]
 *
 * @author why
 * @since 11/24/23
 */
object HomeParser {

    fun Document.parserHome(): HomeIndexEntity {
        val entity = HomeIndexEntity()

        entity.images = select("#featuredItems li").map {
            val imageCardEntity = HomeImageCardEntity()

            val titleRef = it.select("h2.title")
            imageCardEntity.title = titleRef.text()
            imageCardEntity.titleType = titleRef.select("a").attr("href").substringAfterLast("/")
            imageCardEntity.images = it.select("li > div").map { item ->
                val title: String
                val imageUrl: String
                val attention: String
                val subjectId: String
                if (item.hasClass("mainItem")) {
                    title = item.select("a").attr("title")
                    subjectId = item.select("a").attr("href")
                        .substringAfterLast("/")
                    attention = item.select(".grey").text()
                    imageUrl = item.select(".image").attr("style")
                        .fetchStyleBackgroundUrl().optImageUrl()
                } else {
                    title = item.select("a.grid").attr("title")
                    subjectId = item.select("a.grid").attr("href")
                        .substringAfterLast("/")
                    attention = item.select(".grey").text()
                    imageUrl = item.select(".grid").attr("style")
                        .fetchStyleBackgroundUrl().optImageUrl()
                }
                HomeImageCardEntity.HomeImageEntity(
                    title = title,
                    image = imageUrl,
                    attention = attention,
                    id = subjectId
                )
            }

            imageCardEntity
        }

        debugLog { entity.toJson(true) }

        return entity
    }
}