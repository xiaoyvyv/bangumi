@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.parser

import com.fleeksoft.ksoup.nodes.Element
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.core.utils.firsTextNode
import com.xiaoyv.bangumi.shared.core.utils.href
import com.xiaoyv.bangumi.shared.core.utils.hrefLongId
import com.xiaoyv.bangumi.shared.core.utils.parseAsHtml
import com.xiaoyv.bangumi.shared.data.model.response.mikan.ComposeMikanGroup
import com.xiaoyv.bangumi.shared.data.model.response.mikan.ComposeMikanResource

/**
 * [MikanParser]
 *
 * @author why
 * @since 2025/1/14
 */
class MikanParser : BaseParser() {

    /**
     * 解析字幕组资源数据
     */
    fun Element.fetchMikanResourcesConverted(groupName: String): List<ComposeMikanResource> {
        return select("table > tbody > tr").map { item ->
            val tds = item.select("tr td")
            val element = tds.getOrNull(1)
            val name = element?.select(".magnet-link-wrap")?.text().orEmpty()
            val pageUrl = element?.select(".magnet-link-wrap")?.attr("href").orEmpty().let {
                "https://mikanime.tv$it"
            }
            val magnet = element?.select(".js-magnet")?.attr("data-clipboard-text").orEmpty()
            val size = tds.text(2)
            val time = tds.text(3)
            val torrent = tds.getOrNull(4)?.select("a")?.attr("href").orEmpty().let {
                "https://mikanime.tv$it"
            }

            debugLog { "magnet: $name" }

            ComposeMikanResource(
                fileSize = size,
                pageUrl = pageUrl,
                magnet = magnet,
                publishDate = time,
                title = name,
                titleHtml = name.parseAsHtml(),
                subgroupName = groupName,
                torrent = torrent
            )
        }
    }

    fun Element.fetchGardenResourceConverted(): List<ComposeMikanResource> {
        return select("#topic_list > tbody > tr").map {
            val tds = it.select("td")
            val group = it.select(".title > .tag").text()
            val groupId = it.select(".title > .tag > a").hrefLongId()
            val title = it.select(".title > a").html()
            ComposeMikanResource(
                publishDate = tds[0].firsTextNode().trim(),
                typeName = tds[1].firsTextNode().trim(),
                title = title.trim(),
                titleHtml = title.trim().parseAsHtml(),
                subgroupName = group.trim(),
                subgroupId = groupId,
                magnet = it.select(".download-arrow").href().trim(),
                fileSize = tds[4].text().trim(),
            )
        }
    }

    fun Element.fetchMikanGroupConverted(): List<ComposeMikanGroup> {
        return select(".leftbar-nav ul > li").map { item ->
            val time = item.select("span.date").text()
            val a = item.select(".subgroup-name")
            val groupId = a.attr("data-anchor").parseCount().toString()
            val groupName = a.text()
            val poster = select(".m-detail-avatar").attr("src").let {
                "https://mikanime.tv$it"
            }
            ComposeMikanGroup(
                id = groupId,
                name = groupName,
                time = time,
                poster = poster
            )
        }
    }
}