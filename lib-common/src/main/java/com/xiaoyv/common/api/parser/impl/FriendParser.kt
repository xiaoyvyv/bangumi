package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.FriendEntity
import com.xiaoyv.common.api.parser.hrefId
import com.xiaoyv.common.api.parser.parserFriendDeleteHash
import com.xiaoyv.common.api.parser.requireNoError
import com.xiaoyv.common.api.parser.styleBackground
import org.jsoup.nodes.Element

/**
 * Class: [FriendParser]
 *
 * @author why
 * @since 12/14/23
 */
fun Element.parserUserFriends(): List<FriendEntity> {
    requireNoError()

    return select("#memberUserList > li.user").map { item ->
        val entity = FriendEntity()
        entity.avatar = item.select("a.avatar span").styleBackground()
        entity.id = item.select("a.avatar").hrefId()
        entity.name = item.select("a.avatar").text()
        entity.deleteHash = item.select("a[onclick]").parserFriendDeleteHash()
        entity
    }
}