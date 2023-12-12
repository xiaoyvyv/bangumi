@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.TimelineEntity
import com.xiaoyv.common.api.parser.hrefId
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.parseHtml
import com.xiaoyv.common.api.parser.parseStar
import com.xiaoyv.common.api.parser.requireNoError
import com.xiaoyv.common.api.parser.styleBackground
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.TimelineAdapterType
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

/**
 * Class: [TimeParser]
 *
 * @author why
 * @since 11/25/23
 */
fun Document.parserTimelineForms(
    isUserTimeline: Boolean
): List<TimelineEntity> {
    requireNoError()
    return select("#timeline ul > li").map { item ->
        val entity = TimelineEntity()
        item.handleItem(entity)
        entity
    }
}

/**
 * 解析条目
 */
private fun Element.handleItem(entity: TimelineEntity) {
    // 辨别条目的 UI 类型
    val adapterType = when {
        select(".imgs, .rr").isNotEmpty() -> TimelineAdapterType.TYPE_GRID
        select(".card").isNotEmpty() -> TimelineAdapterType.TYPE_MEDIA
        else -> TimelineAdapterType.TYPE_TEXT
    }

    // 根据类型UI解析
    when (adapterType) {
        TimelineAdapterType.TYPE_TEXT -> parserTimelineText(this, entity)
        TimelineAdapterType.TYPE_MEDIA -> parserTimelineMedia(this, entity)
        TimelineAdapterType.TYPE_GRID -> parserTimelineGrid(this, entity)
    }

    entity.adapterType = adapterType
}

/*

    // 用户的时间线单独解析头像和ID
    var userId = ""
    var userAvatar = ""
    if (isUserTimeline) {
        userId = select(".headerAvatar > a").attr("href")
            .substringAfterLast("/")
        userAvatar = select(".headerAvatar > a > span").attr("style")
            .fetchStyleBackgroundUrl().optImageUrl()
    }

    select("#timeline ul > li").map {
        val entity = TimelineEntity()

        if (isUserTimeline) {
            entity.id = userId
            entity.avatar = userAvatar
        } else {
            entity.id = it.select("li").attr("data-item-user")
            entity.avatar = it.select("li a.avatar span").styleBackground()
            entity.time = it.select(".post_actions").text()
        }


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
        entity.title = infoUserActionText.parseHtml()


        // images


        entities.add(entity)
    }
*/
/*
*/
/**
 * 解析媒体类型时间线
 *//*
private fun Element.parserTimelineMedia(): List<TimelineEntity> {
    return parserTimelineText { entity ->

    }
}*/

/**
 * 解析多图网格类型的时间线
 */
fun parserTimelineGrid(item: Element, entity: TimelineEntity) {
    // 按文本先解析一次
    parserTimelineText(item, entity)

    // 右侧图片
    val rightImage = item.select("img.rr").let { img ->
        val a = img.parents()
        val coverUrl = img.attr("src").optImageUrl()
        val (titleId, titleType) = a.firstOrNull().fetchLinkIdAndType()
        TimelineEntity.GridTimeline(coverUrl, titleId, titleType)
    }

    // 图片 Grid
    val images = item.select(".imgs > a").map { a ->
        val coverUrl = a.select("a img").attr("src").optImageUrl()
        val (titleId, titleType) = a.fetchLinkIdAndType()
        TimelineEntity.GridTimeline(coverUrl, titleId, titleType)
    }.toMutableList()

    if (rightImage.id.isNotBlank()) {
        images.add(0, rightImage)
    }

    entity.gridCard = images
}

/**
 * 解析媒体类型的时间线
 */
fun parserTimelineMedia(item: Element, entity: TimelineEntity) {
    // 按文本先解析一次
    parserTimelineText(item, entity)

    // 覆盖 content 内容
    entity.content = item.select(".collectInfo").select(".comment").text()

    // MediaCard
    val cardContainer = item.select(".card .container")
    val subjectId = cardContainer.select(".container > a").hrefId()
    val coverUrl = cardContainer.select(".cover img").attr("src").optImageUrl()
    val inner = cardContainer.select(".inner")
    val cardTitle = inner.select("p.title").text()
    val cardTip = inner.select("p.tip").text()
    val cardRate = inner.select(".rateInfo .fade").text()
    val cardRateTotal = inner.select(".rateInfo .rate_total").text()
    val score = item.select(".collectInfo").parseStar()

    entity.mediaCard = TimelineEntity.MediaTimeline(
        title = cardTitle,
        id = subjectId,
        cover = coverUrl,
        info = cardTip,
        cardRate = cardRate,
        cardRateTotal = cardRateTotal,
        score = score
    )
}

/**
 * 解析文本类型的时间线
 */
fun parserTimelineText(item: Element, entity: TimelineEntity) {
    val (time, platform) = item.select(".post_actions").fetchTimeAndPlatform()
    entity.time = time
    entity.platform = platform

    val (titleId, titleType) = item.select(".info > a.l").getOrNull(1).fetchLinkIdAndType()
    entity.titleId = titleId
    entity.titleType = titleType

    entity.id = item.select("a.avatar").hrefId()
    entity.name = item.select(".info > a").firstOrNull()?.text().orEmpty()
    entity.avatar = item.select("a.avatar > span").styleBackground()
    entity.title = item.select(".info").fetchHtmlTitle()
    entity.content = item.select("p.status, .info_sub").text()
}

/**
 * 解析时间和平台解析
 */
private fun Elements.fetchTimeAndPlatform(): Pair<String, String> {
    val timeInfo = select(".post_actions")
        .apply { select("a").remove() }
        .text().trim().trim('·').trim()
        .split("·")
    val time = timeInfo.getOrNull(0).orEmpty().trim()
    val platform = timeInfo.getOrNull(1).orEmpty().trim()
    return time to platform
}

/**
 * 解析基础信息标题
 */
private fun Elements.fetchHtmlTitle(): CharSequence {
    // 标题解析
    var infoUserActionText = ""
    val nodes = firstOrNull()?.childNodes().orEmpty()
    for (node in nodes) {
        if (node is Element) {
            if (node.select(".rr").isNotEmpty()) continue
            val tagName = node.tagName().lowercase()
            if (tagName == "div" || tagName == "p") break
        }
        infoUserActionText += node.toString()
    }
    return infoUserActionText.parseHtml()
}

/**
 * 解析标题的链接和类型，目前仅解析结果用于 [TimelineAdapterType.TYPE_TEXT] 类型的条目点击使用
 */
private fun Element?.fetchLinkIdAndType(): Pair<String, String> {
    this ?: return ("" to "")

    // 解析标题的链接 ID
    val titleLink = attr("href").orEmpty()
    val titleId = hrefId()
    val titleType = when {
        // TimelineAdapterType.TYPE_TEXT 条目点击使用
        titleLink.contains(BgmPathType.TYPE_BLOG) -> BgmPathType.TYPE_BLOG
        titleLink.contains(BgmPathType.TYPE_INDEX) -> BgmPathType.TYPE_INDEX

        // TimelineAdapterType.TYPE_GRID 子宫格点击使用
        titleLink.contains(BgmPathType.TYPE_GROUP) -> BgmPathType.TYPE_GROUP
        titleLink.contains(BgmPathType.TYPE_CHARACTER) -> BgmPathType.TYPE_CHARACTER
        titleLink.contains(BgmPathType.TYPE_PERSON) -> BgmPathType.TYPE_PERSON
        titleLink.contains(BgmPathType.TYPE_SUBJECT) -> BgmPathType.TYPE_SUBJECT
        else -> BgmPathType.TYPE_USER
    }
    return titleId to titleType
}
