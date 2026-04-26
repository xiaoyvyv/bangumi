@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.parser.bgm

import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.TextNode
import com.fleeksoft.ksoup.select.Elements
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.TimelineTab
import com.xiaoyv.bangumi.shared.core.types.TimelineTarget
import com.xiaoyv.bangumi.shared.core.utils.firsTextNode
import com.xiaoyv.bangumi.shared.core.utils.hrefLongId
import com.xiaoyv.bangumi.shared.core.utils.lastTextNode
import com.xiaoyv.bangumi.shared.core.utils.parseAsHtml
import com.xiaoyv.bangumi.shared.core.utils.parseStar
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.constant.userImage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeCollection
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeImages
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMono
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoInfo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeRating
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectWebInfo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeWebTimeline
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import com.xiaoyv.bangumi.shared.data.parser.BaseParser
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlin.math.roundToInt

class TimelineParser : BaseParser() {

    suspend fun Element.fetchTimelineConverted(
        @TimelineTarget target: String,
        @TimelineTab type: String,
    ): List<ComposeWebTimeline> {
        requireNoError()
        return select("#timeline ul > li").map { item ->
            fetchTimelineItemConverted(target, type, item)
        }
    }

    private fun fetchTimelineItemConverted(
        @TimelineTarget target: String,
        @TimelineTab type: String,
        item: Element,
    ): ComposeWebTimeline {
        val user = if (target == TimelineTarget.USER) ComposeUser.Empty else {
            val avatarUrl = item.select("a.avatar > span").styleAvatarUrl()
            val username = item.select("li").attr("data-item-user")
            ComposeUser(
                id = avatarUrl.avatarUrlId(username),
                username = username,
                nickname = item.select("span.info > a").firsTextNode(),
                avatar = ComposeImages.fromUrl(avatarUrl.ifBlank { userImage(username) })
            )
        }

        // 媒体条目数据
        val subjects = when (type) {
            TimelineTab.DYNAMIC,
            TimelineTab.BOOKMARK,
            TimelineTab.PROGRESS,
            TimelineTab.WIKI,
                -> item.covertSubjectItems()

            else -> persistentListOf()
        }

        // 人物数据
        val monos = if (type == TimelineTab.MONO) {
            item.covertMonoItems()
        } else {
            persistentListOf()
        }

        // 收藏数据
        val collectInfo = item.select(".collectInfo")
        val collection = if (collectInfo.isNotEmpty()) {
            ComposeCollection(
                comment = collectInfo.select(".comment").text(),
                rate = collectInfo.parseStar().roundToInt()
            )
        } else {
            ComposeCollection.Empty
        }

        return ComposeWebTimeline(
            id = item.select("li").attr("id"),
            user = user,
            title = item.select(".info, .info_full").htmlTitle().parseAsHtml(),
            content = item.select("p.status").html().parseAsHtml(),
            blog = item.select(".info_sub").html().parseAsHtml(),
            time = item.select(".titleTip").text(),
            subjects = subjects,
            monos = monos,
            platform = item.select(".post_actions.date")
                .lastTextNode()
                .substringAfterLast("·")
                .trim()
                .ifBlank { item.select("small > a").text() },
            timelineType = type,
            collection = collection
        )
    }

    /**
     * 解析时间线标题
     */
    private fun Elements.htmlTitle(): String {
        var infoUserActionText = ""
        val nodes = firstOrNull()?.childNodes().orEmpty()
        var reachTextNode = false
        for (node in nodes) {
            if (node is TextNode) reachTextNode = true
            if (reachTextNode) {
                if (node is Element) {
                    val tagName = node.tagName().lowercase()
                    if (tagName == "div" || tagName == "p") break
                }
                infoUserActionText += node.toString()
            }
        }
        return infoUserActionText
    }

    /**
     * 解析时间线附带的条目数据
     */
    private fun Element.covertSubjectItems(): SerializeList<ComposeSubject> {
        val grid = select(".imgs, .rr")
        val card = select(".card")
        return when {
            // 宫格
            grid.isNotEmpty() -> {
                grid.select("a").map { item ->
                    ComposeSubject(
                        id = item.hrefLongId(),
                        images = ComposeImages.fromUrl(item.select("img").src()),
                    )
                }.toImmutableList()
            }
            // 卡片
            card.isNotEmpty() -> {
                card.select(".container").map { item ->
                    val rateInfo = item.select(".rateInfo")

                    ComposeSubject(
                        id = item.select("a").hrefLongId(),
                        images = ComposeImages.fromUrl(item.select("img").src()),
                        nameCn = item.select("p.title > a").firsTextNode(),
                        name = item.select("p.title > small").text(),
                        webInfo = ComposeSubjectWebInfo(info = item.select("p.info").text()),
                        rating = if (rateInfo.isEmpty()) ComposeRating.Empty else {
                            ComposeRating(
                                score = rateInfo.select("small.fade").doubleValue(),
                                total = rateInfo.select(".rate_total").text().parseCount()
                            )
                        }
                    )
                }.toImmutableList()
            }

            else -> persistentListOf()
        }
    }

    /**
     * 解析时间线附带的人物数据
     */
    private fun Element.covertMonoItems(): SerializeList<ComposeMonoDisplay> {
        return select("img.grid, img.rr").map { item ->
            val parent = item.parent()
            val href = parent?.attr("href").orEmpty()
            val id = parent?.hrefLongId() ?: 0

            ComposeMonoDisplay(
                type = when {
                    href.contains("character") -> MonoType.CHARACTER
                    else -> MonoType.PERSON
                },
                info = ComposeMonoInfo(
                    mono = ComposeMono(
                        id = id,
                        images = ComposeImages.fromUrl(item.src()),
                    )
                ),
            )
        }.toImmutableList()
    }
}