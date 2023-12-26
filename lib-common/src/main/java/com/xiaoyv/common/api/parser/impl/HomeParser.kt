package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.BgmMediaEntity
import com.xiaoyv.common.api.parser.entity.HomeIndexCalendarEntity
import com.xiaoyv.common.api.parser.entity.HomeIndexCardEntity
import com.xiaoyv.common.api.parser.entity.HomeIndexEntity
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.api.parser.fetchStyleBackgroundUrl
import com.xiaoyv.common.api.parser.hrefId
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.parseCount
import com.xiaoyv.common.api.parser.parseHtml
import com.xiaoyv.common.api.parser.requireNoError
import com.xiaoyv.common.api.response.api.ApiEpisodeEntity
import com.xiaoyv.common.api.response.api.ApiUserEpEntity
import com.xiaoyv.common.config.annotation.EpApiType
import com.xiaoyv.common.config.annotation.EpCollectType
import com.xiaoyv.common.config.annotation.InterestType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.config.annotation.SearchCatType
import com.xiaoyv.common.config.bean.SampleImageEntity
import com.xiaoyv.common.kts.randId
import com.xiaoyv.widget.kts.orEmpty
import com.xiaoyv.widget.kts.subListLimit
import com.xiaoyv.widget.kts.useNotNull
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * 解析主页进度
 */
fun Element.parserHomePageProcess(): List<MediaDetailEntity> {
    requireNoError()
    val subjectPrgContent = select("#subject_prg_content")

    return select("#cloumnSubjectInfo > div > div").map { item ->
        val entity = MediaDetailEntity()
        entity.collectState.interest = InterestType.TYPE_DO
        entity.id = item.id().substringAfterLast("_")
        entity.mediaType = when (item.attr("subject_type")) {
            SearchCatType.TYPE_ANIME -> MediaType.TYPE_ANIME
            SearchCatType.TYPE_REAL -> MediaType.TYPE_REAL
            SearchCatType.TYPE_BOOK -> MediaType.TYPE_BOOK
            else -> MediaType.TYPE_UNKNOWN
        }
        entity.cover = item.select(".image img").attr("src").optImageUrl()

        item.select(".textTip").apply {
            entity.titleNative = attr("data-subject-name")
            entity.titleCn = attr("data-subject-name-cn")
        }

        entity.rating.ratingCount = item.select(".tip .grey").text().parseCount()

        // 进度框
        when (entity.mediaType) {
            // 书籍进度
            MediaType.TYPE_BOOK -> {
                val prgTexts = item.select(".prgText")
                useNotNull(prgTexts.getOrNull(0)) {
                    entity.progress = select("input[type=text]").attr("value").parseCount()
                    entity.progressMax = text().parseCount()
                }
                useNotNull(prgTexts.getOrNull(1)) {
                    entity.progressSecond = select("input[type=text]").attr("value").parseCount()
                    entity.progressSecondMax = text().parseCount()
                }
            }
            // 动画或三次元总进度
            MediaType.TYPE_ANIME, MediaType.TYPE_REAL -> {
                val form = item.select(".prgBatchManagerForm")
                entity.progress = form.select("input[type=text]").attr("value").parseCount()
                entity.progressMax = form.text().parseCount()
            }
        }

        // 进度
        var hasSplitter = false
        entity.epList = item.select(".prg_list > li").map li@{ ep ->
            val epA = ep.select("a")
            if (epA.isEmpty()) {
                hasSplitter = true
                return@li ApiUserEpEntity(
                    splitter = true,
                    episode = ApiEpisodeEntity(ep = ep.text()),
                ).apply { id = randId() }
            }

            val relQuery = epA.attr("rel")
            val rel = if (relQuery.isNotBlank()) subjectPrgContent.select(relQuery) else null
            val relTip = rel?.select(".tip")

            val epEntity = ApiUserEpEntity()
            val episode = ApiEpisodeEntity()
            episode.id = epA.hrefId().toLongOrNull().orEmpty()
            episode.name = epA.attr("title")
            episode.subjectId = epA.attr("subject_id").toLongOrNull().orEmpty()
            episode.ep = epA.text()
            episode.sort = epA.text().toDoubleOrNull() ?: 0.0
            episode.comment = relTip?.select(".cmt")?.remove()?.text().parseCount()
            episode.type = if (hasSplitter) EpApiType.TYPE_OTHER else EpApiType.TYPE_MAIN

            // 中文标题:xxx
            // 首播:xxx
            val relInfos = relTip?.html().orEmpty().split("<br>")
            episode.nameCn = relInfos
                .find { it.contains("中文标题") }.orEmpty()
                .replace("中文标题:", "").trim()

            episode.airdate = relInfos
                .find { it.contains("首播") }.orEmpty()
                .replace("首播:", "").trim()


            // 收藏状态
            epEntity.type = when {
                epA.select(".epBtnWatched").isNotEmpty() -> EpCollectType.TYPE_COLLECT
                epA.select(".epBtnQueue").isNotEmpty() -> EpCollectType.TYPE_WISH
                epA.select(".epBtnDrop").isNotEmpty() -> EpCollectType.TYPE_DROPPED
                else -> EpCollectType.TYPE_NONE
            }

            // 填充放送等状态
            episode.fillState(epEntity.type, mediaType = entity.mediaType)

            epEntity.episode = episode
            epEntity.splitter = false
            epEntity
        }

        // 截取合适位置的12个数据
        useNotNull(entity.epList) {
            if (size > 12) {
                // 最后一个看过的位置
                val lastCollectEpIndex = indexOfLast {
                    it.type == EpCollectType.TYPE_COLLECT && it.episode?.type == EpApiType.TYPE_MAIN
                }
                if (lastCollectEpIndex == -1) {
                    entity.epList = subListLimit(12)
                } else {
                    val endList = subList(lastCollectEpIndex, size)
                    if (endList.size >= 12) {
                        entity.epList = endList.subList(0, 12)
                    } else {
                        // 向前补充
                        val repair = 12 - endList.size
                        val repairList = subList(lastCollectEpIndex - repair, lastCollectEpIndex)
                        val list = repairList.toMutableList()
                        list.addAll(endList)
                        entity.epList = list
                    }
                }
            }
        }
        entity
    }
}

/**
 * 解析未登录的主页
 */
fun Document.parserHomePageWithoutLogin(): HomeIndexEntity {
    val entity = HomeIndexEntity()
    entity.images = select("#featuredItems li").map {
        val imageCardEntity = HomeIndexCardEntity()

        val titleRef = it.select("h2.title")
        imageCardEntity.title = titleRef.text()
        imageCardEntity.titleType = titleRef.select("a").hrefId()
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
            BgmMediaEntity(
                title = title,
                image = imageUrl,
                attention = attention,
                id = subjectId
            )
        }

        imageCardEntity
    }

    val tip = select("#home_calendar .tip").text()
    entity.calendar = select("#home_calendar .week").let {
        val today = it.getOrNull(0)
        val tomorrow = it.getOrNull(1)

        HomeIndexCalendarEntity(
            tip = tip,
            today = today.selectCalendarItem(),
            tomorrow = tomorrow.selectCalendarItem()
        )
    }

    entity.banner.banners = entity.images.map {
        val media = it.images.firstOrNull() ?: return@map null
        SampleImageEntity(
            id = media.id,
            image = media.image.optImageUrl(largest = true),
            title = media.title.toString()
        )
    }.filterNotNull()
    return entity
}

private fun Element?.selectCalendarItem(): List<BgmMediaEntity> {
    val element = this ?: return emptyList()
    return element.select(".coverList .thumbTip").map {
        BgmMediaEntity(
            title = it.select("a").attr("title").parseHtml().toString(),
            id = it.select("a").hrefId(),
            image = it.select("img").attr("src").optImageUrl()
        )
    }
}
