@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.TimelineEntity
import com.xiaoyv.common.api.parser.entity.TimelineReplyEntity
import com.xiaoyv.common.api.parser.hrefId
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.parseCount
import com.xiaoyv.common.api.parser.parseHtml
import com.xiaoyv.common.api.parser.parseStar
import com.xiaoyv.common.api.parser.requireNoError
import com.xiaoyv.common.api.parser.styleBackground
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.TimelineAdapterType
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.groupValueOne
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

/**
 * @param isTotalTimeline 是否为网站全部时间线，禁止回复
 */
fun Document.parserTimelineForms(
    userId: String = "",
    isTotalTimeline: Boolean = false,
): List<TimelineEntity> {
    requireNoError()
    return select("#timeline ul > li").map { item ->
        val entity = TimelineEntity()
        item.handleItem(entity, userId)
        // 如果指定了用户的时间线，单独解析头像和用户名登
        if (userId.isNotBlank()) {
            entity.name = select(".nameSingle .name").text()
            entity.avatar = select(".headerAvatar a.avatar > span").styleBackground()
        }
        entity.isTotalTimeline = isTotalTimeline
        entity
    }
}

/**
 * 解析条目
 *
 * @param userId 不为空时，数据为对应用户的时间线
 */
private fun Element.handleItem(entity: TimelineEntity, userId: String) {
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

    // 当解析的指定用户的时间线时，设置用户ID
    if (userId.isNotBlank()) {
        entity.userId = userId

        // 如果为自己的时间线，填充头像
        if (userId == UserHelper.currentUser.id) {
            entity.avatar = UserHelper.currentUser.avatar?.medium.orEmpty()
        }
    }

    entity.adapterType = adapterType
}

/**
 * 解析多图网格类型的时间线
 */
fun parserTimelineGrid(item: Element, entity: TimelineEntity) {
    // 按文本先解析一次
    parserTimelineText(item, entity)

    // 右侧图片
    // 小组、好友等右侧单图片解析 a.rr -> img
    // 人物等右侧单图片解析 a -> img.rr
    val rightImage = item.select(".rr").let { rr ->
        val img = rr.select("img")
        val coverUrl = img.attr("src").optImageUrl()
        // 当 rr 是 a 标签，直接解析
        val rrA = rr.select("a")
        if (rrA.isNotEmpty()) {
            val (titleId, titleType) = rrA.first().fetchLinkIdAndType()
            TimelineEntity.GridTimeline(coverUrl, titleId, titleType)
        }
        // 否则 rr 是 img标签，则提取父级 a 标签解析
        else {
            val (titleId, titleType) = rr.parents().firstOrNull().fetchLinkIdAndType()
            TimelineEntity.GridTimeline(coverUrl, titleId, titleType)
        }
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
    val infoA = item.select(".info > a.l")
    val infoFullA = item.select(".info_full > a.l")
    val (titleId, titleType, titleLink) =
        (infoA.getOrNull(1) ?: infoFullA.firstOrNull()).fetchLinkIdAndType()

    entity.id = item.id().parseCount().toString()
    entity.titleId = titleId
    entity.titleType = titleType
    entity.titleLink = titleLink

    entity.userId = item.select("a.avatar").hrefId()
    entity.name = item.select(".info > a").firstOrNull()?.text().orEmpty()
    entity.avatar = item.select("a.avatar > span").styleBackground(false)
    entity.title = item.select(".info, .info_full").fetchHtmlTitle()
    entity.content = item.select("p.status, .info_sub").text()
    entity.deleteId = item.select(".tml_del").hrefId()

    // 解析 Action
    item.select(".post_actions").fetchTimeAndPlatform(entity)
}

/**
 * 解析时间和平台解析
 */
private fun Elements.fetchTimeAndPlatform(entity: TimelineEntity) {
    val actions = select(".post_actions")
    val commentA = select("a").remove()
    val timeInfo = actions.text().split("·").filter { it.isNotBlank() }
    val time = timeInfo.getOrNull(0).orEmpty().trim()
    val platform = timeInfo.getOrNull(1).orEmpty().trim()

    entity.time = time
    entity.platform = platform
    entity.commentAble = commentA.isNotEmpty()
    entity.commentCount = commentA.text().parseCount()
    entity.commentUserId = "user/(.*?)/timeline".toRegex().groupValueOne(commentA.attr("href"))

    if (entity.userId.isBlank()) {
        entity.userId = entity.commentUserId
    }
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
    if (infoUserActionText.isBlank()) infoUserActionText = "时间线"
    return infoUserActionText.parseHtml()
}

/**
 * 解析标题的链接和类型，目前仅解析结果用于 [TimelineAdapterType.TYPE_TEXT] 类型的条目点击使用
 */
private fun Element?.fetchLinkIdAndType(): Triple<String, String, String> {
    this ?: return Triple("", "", "")

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
        titleLink.contains(BgmPathType.TYPE_USER) -> BgmPathType.TYPE_USER
        else -> BgmPathType.TYPE_USER
    }
    return Triple(titleId, titleType, titleLink)
}

/**
 * 解析时间线回复
 */
fun Element.parserTimelineSayReply(): List<TimelineReplyEntity> {
    return select(".subReply > li[data-item-user]").map { item ->
        item.select(".cmt_reply").remove()
        val html = item.html()
        val userName = item.select("a").firstOrNull()?.text().orEmpty()

        TimelineReplyEntity(
            id = item.attr("data-item-user"),
            userName = userName,
            contentHtml = html,
            content = html.parseHtml(true),
        )
    }
}
