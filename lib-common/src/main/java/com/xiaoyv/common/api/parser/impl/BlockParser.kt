package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.BlockEntity
import com.xiaoyv.common.api.parser.entity.PrivacyEntity
import com.xiaoyv.common.api.parser.hrefId
import com.xiaoyv.common.config.annotation.PrivacyType
import org.jsoup.nodes.Element

/**
 * @author why
 * @since 12/17/23
 */
fun Element.parserBlockUser(): List<BlockEntity> {
    return select(".settings > tbody > tr").toList()
        .filter { it.select("a").isNotEmpty() }
        .map { item ->
            val avatar = BlockEntity()
            avatar.id = item.select("td > a").hrefId()
            avatar.name = item.select("td > a").text()
            avatar.numberId = item.select("a.tip_i").attr("href").let {
                "(\\d{6,})".toRegex().find(it)?.groupValues?.getOrNull(1).orEmpty()
            }
            avatar
        }
}

/**
 * 解析隐私设置
 */
fun Element.parserPrivacy(): List<PrivacyEntity> {
    return select(".settings > tbody > tr").toList()
        .filter { it.select("select").isNotEmpty() }
        .map { item ->
            val entity = PrivacyEntity()
            entity.id = item.select("select").attr("name")
            entity.privacyType = item.select("select option[selected]").attr("value")
                .toIntOrNull() ?: PrivacyType.TYPE_ALL
            entity.title = item.select("td").firstOrNull()?.text().orEmpty()
            entity
        }
}
