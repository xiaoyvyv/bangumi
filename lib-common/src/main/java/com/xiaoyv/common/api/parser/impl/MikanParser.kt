package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.MikanEntity
import com.xiaoyv.common.api.parser.parseCount
import com.xiaoyv.common.api.response.anime.AnimeMagnetEntity
import org.jsoup.nodes.Document


/**
 * @author why
 * @since 11/26/23
 */
fun Document.parserMikanInfo(id: String): MikanEntity {
    val groups = select(".leftbar-nav ul > li").map { item ->
        val time = item.select("span.date").text()
        val a = item.select(".subgroup-name")
        val groupId = a.attr("data-anchor").parseCount().toString()
        val groupName = a.text()
        val poster = select(".m-detail-avatar").attr("src").let {
            "https://mikanime.tv$it"
        }
        MikanEntity.Group(
            id = groupId,
            name = groupName,
            time = time,
            poster = poster
        )
    }
    return MikanEntity(id = id, groups = groups)
}

/**
 * 解析字幕组资源数据
 */
fun Document.parserMikanGroupInfo(groupName: String): List<AnimeMagnetEntity.Resource> {
    return select("table > tbody > tr").map { item ->
        val tds = item.select("tr td")
        val td1 = tds.getOrNull(0)

        val name = td1?.select(".magnet-link-wrap")?.text().orEmpty()
        val pageUrl = td1?.select(".magnet-link-wrap")?.attr("href").orEmpty().let {
            "https://mikanime.tv$it"
        }
        val magnet = td1?.select(".js-magnet")?.attr("data-clipboard-text").orEmpty()
        val size = tds.getOrNull(1)?.text().orEmpty()
        val time = tds.getOrNull(2)?.text().orEmpty()
        val torrent = tds.getOrNull(3)?.select("a")?.attr("href").orEmpty().let {
            "https://mikanime.tv$it"
        }

        AnimeMagnetEntity.Resource(
            fileSize = size,
            pageUrl = pageUrl,
            magnet = magnet,
            publishDate = time,
            title = name,
            typeName = "动画",
            subgroupName = groupName
        )
    }
}