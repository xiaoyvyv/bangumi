package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.IndexEntity
import com.xiaoyv.common.api.parser.entity.IndexItemEntity
import com.xiaoyv.common.api.parser.fetchStyleBackgroundUrl
import com.xiaoyv.common.api.parser.hrefId
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.parseCount
import com.xiaoyv.common.api.parser.requireNoError
import com.xiaoyv.common.config.annotation.MediaType
import org.jsoup.nodes.Element

/**
 * @author why
 * @since 12/12/23
 */
fun Element.parserIndex(): IndexEntity {
    requireNoError()
    val entity = IndexEntity()

    select("#columnA").apply {
        entity.gridItems = select(".indexFocus").map { item ->
            val grid = IndexEntity.Grid()
            grid.images = item.select(".coverGrid img").map {
                it.attr("src").optImageUrl()
            }
            grid.id = select("a").hrefId()
            grid.title = select("a h3").text()
            grid.desc = select("a span").text()
            grid
        }

        val list = select("#timeline").map { timeline ->
            timeline.select("ul > li").map { item ->
                val itemEntity = IndexItemEntity()
                itemEntity.userId = item.select("a.avatar").hrefId()
                itemEntity.userAvatar = item.select("a.avatar > span").attr("style")
                    .fetchStyleBackgroundUrl().optImageUrl()
                itemEntity.userName = item.select(".info .tip_i a").text()
                itemEntity.time = item.select(".info .tip_j").text()
                itemEntity.id = item.select("h3 a").hrefId()
                itemEntity.title = item.select("h3").text()
                itemEntity.desc = item.select(".info > p").text()
                itemEntity.mediaType = item.select(".ico_subject_type").toString().let {
                    when {
                        it.contains("subject_type_1") -> MediaType.TYPE_BOOK
                        it.contains("subject_type_2") -> MediaType.TYPE_ANIME
                        it.contains("subject_type_3") -> MediaType.TYPE_MUSIC
                        it.contains("subject_type_4") -> MediaType.TYPE_GAME
                        it.contains("subject_type_6") -> MediaType.TYPE_REAL
                        else -> MediaType.TYPE_UNKNOWN
                    }
                }
                itemEntity.mediaCount = item.select(".ico_subject_type").text().parseCount()
                itemEntity
            }
        }
        entity.newItems = list.getOrNull(0).orEmpty()
        entity.hotItems = list.getOrNull(1).orEmpty()
    }
    return entity
}