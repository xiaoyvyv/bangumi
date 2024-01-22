package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.MediaScoreEntity
import com.xiaoyv.common.api.parser.hrefId
import com.xiaoyv.common.api.parser.lastTextNode
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.parseStar
import com.xiaoyv.common.api.parser.styleBackground
import org.jsoup.nodes.Element

fun Element.parserMediaScore(): List<MediaScoreEntity> {
    return select("#memberUserList > li").map { item ->
        val entity = MediaScoreEntity()
        val a = item.select("a.avatar")
        entity.id = a.hrefId()
        entity.avatar = item.select(".userImage span").styleBackground().optImageUrl()
        entity.name = a.text().trim()
        entity.score = item.parseStar()
        entity.time = item.select("p.info").text()
        entity.comment = item.select(".userContainer").lastTextNode()
        entity
    }
}