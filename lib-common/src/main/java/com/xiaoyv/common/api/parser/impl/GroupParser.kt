package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.GroupDetailEntity
import com.xiaoyv.common.api.parser.fetchStyleBackgroundUrl
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.parseHtml
import com.xiaoyv.common.config.bean.SampleAvatar
import com.xiaoyv.widget.kts.useNotNull
import org.jsoup.nodes.Document

/**
 * @author why
 * @since 12/7/23
 */
fun Document.parserGroupDetail(groupId: String): GroupDetailEntity {
    val entity = GroupDetailEntity()
    entity.id = groupId

    select("#columnA").apply {
        entity.avatar = select(".grp_box > img").attr("src").optImageUrl()
        entity.name = select("h1.SecondaryNavTitle").text()
        entity.time = select(".grp_box > .tip").text()
        entity.summary = select(".line_detail > .tip").html()
        entity.summaryText = entity.summary.parseHtml().toString()
    }

    select("#columnB > .SidePanel").apply {
        useNotNull(getOrNull(0)) {
            entity.recently = select("dl").map { item ->
                val avatar = SampleAvatar()
                avatar.id = item.select(".avatar").attr("href").substringAfterLast("/")
                avatar.image = item.select(".avatar > span").attr("style")
                    .fetchStyleBackgroundUrl().optImageUrl()
                avatar.title = item.select(".l").text()
                avatar
            }
        }
        useNotNull(getOrNull(1)) {
            entity.otherGroups = select("dl").map { item ->
                val avatar = SampleAvatar()
                avatar.id = item.select(".avatar").attr("href").substringAfterLast("/")
                avatar.image = item.select(".avatar > span").attr("style")
                    .fetchStyleBackgroundUrl().optImageUrl()
                avatar.title = item.select(".l").text()
                avatar.desc = item.select(".grey").text()
                avatar
            }
        }
    }
    return entity
}