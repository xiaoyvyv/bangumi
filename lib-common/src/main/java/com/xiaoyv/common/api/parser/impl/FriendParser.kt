package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.FriendEntity
import com.xiaoyv.common.api.parser.hrefId
import com.xiaoyv.common.api.parser.parserFormHash
import com.xiaoyv.common.api.parser.requireNoError
import com.xiaoyv.common.api.parser.styleBackground
import org.jsoup.nodes.Element

/**
 * [parserUserFriends]
 */
fun Element.parserUserFriends(): List<FriendEntity> {
    requireNoError()
    val gh = parserFormHash()

    return select("#memberUserList > li.user").map { item ->
        val entity = FriendEntity()
        entity.avatar = item.select("a.avatar span").styleBackground()
        entity.id = item.select("a.avatar").hrefId()
        entity.name = item.select("a.avatar").text()
        entity.gh = gh
        entity
    }
}