package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.DollarsEntity
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.kts.randId
import org.jsoup.nodes.Element

/**
 * @author why
 * @since 1/4/24
 */
fun Element.parserDollars(): List<DollarsEntity> {
    return select("#chatList > ul > li").map { item ->
        val entity = DollarsEntity()
        entity.id = randId()
        entity.nickname = item.select(".icon p").text()
        entity.avatar = item.select(".icon img").attr("src").optImageUrl()
        entity.msg = item.select(".content").text()
        entity
    }
}