package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.SearchResultEntity
import com.xiaoyv.common.api.parser.hrefId
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.parseStar
import com.xiaoyv.common.api.parser.requireNoError
import com.xiaoyv.common.config.GlobalConfig
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.kts.decodeUrl
import org.jsoup.nodes.Element

/**
 * 解析搜索结果
 *
 * @author why
 * @since 12/10/23
 */
fun Element.parserSearchResult(@BgmPathType pathType: String): List<SearchResultEntity> {
    requireNoError()

    return when (pathType) {
        // 搜索条目解析
        BgmPathType.TYPE_SEARCH_SUBJECT -> {
            select("#browserItemList > li").map { item ->
                val entity = SearchResultEntity()
                entity.id = item.select("a.cover").hrefId()
                entity.coverImage = item.select("a.cover img").attr("src").optImageUrl()
                entity.title = item.select(".inner h3 a").text()
                entity.subtitle = item.select(".inner h3 small").text()
                entity.rank = item.select(".rank").text()
                entity.infoTip = BrowserParser.parserInfoTip(item.select(".tip").text())

                val rateInfo = item.select(".rateInfo")
                entity.rating = rateInfo.parseStar()
                entity.ratingScore = rateInfo.select(".fade").text()
                entity.count = rateInfo.select(".tip_j").text()

                val collectBlock = item.select(".collectBlock")
                entity.isCollection = collectBlock.select(".collectModify").isNotEmpty()

                val mediaType = item.select(".ico_subject_type").toString().let { clsName: String ->
                    when {
                        clsName.contains("subject_type_1") -> MediaType.TYPE_BOOK
                        clsName.contains("subject_type_2") -> MediaType.TYPE_ANIME
                        clsName.contains("subject_type_3") -> MediaType.TYPE_MUSIC
                        clsName.contains("subject_type_4") -> MediaType.TYPE_GAME
                        clsName.contains("subject_type_6") -> MediaType.TYPE_REAL
                        else -> MediaType.TYPE_UNKNOWN
                    }
                }
                entity.searchMediaType = mediaType
                entity.searchTip = GlobalConfig.mediaTypeName(mediaType)
                entity
            }
        }
        // 搜索人物解析
        BgmPathType.TYPE_SEARCH_MONO -> {
            select("#columnSearchB > div")
                .filter { item -> item.select("a").isNotEmpty() }
                .map { item ->
                    val entity = SearchResultEntity()
                    entity.id = item.select("a.avatar").hrefId()
                    entity.coverImage = item.select("a.avatar img").attr("src").optImageUrl()
                    entity.count = "讨论：" + item.select(".rr small").text()
                    item.select("h2 a.l").apply {
                        entity.subtitle = select(".tip").remove().text().trim().trim('/')
                        entity.title = text().trim().trim('/')
                    }
                    entity.infoTip =
                        BrowserParser.parserInfoTip(item.select(".prsn_info .tip").text().trim('/'))
                    entity.searchTip = "人物"
                    entity
                }
        }
        // 搜索标签解析
        BgmPathType.TYPE_SEARCH_TAG -> {
            select("#tagList > a").map { item ->
                val entity = SearchResultEntity()
                entity.id = item.hrefId().decodeUrl()
                entity.count = item.nextElementSibling()?.text().orEmpty()
                entity.title = item.text()
                entity.searchTip = "标签"
                entity
            }
        }

        else -> throw IllegalArgumentException("暂不支持搜索该类型")
    }
}