@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.parser.bgm

import com.fleeksoft.ksoup.nodes.Element
import com.xiaoyv.bangumi.shared.core.types.AppParserDsl
import com.xiaoyv.bangumi.shared.core.utils.hrefId
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroup
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroupHomepage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeImages
import com.xiaoyv.bangumi.shared.data.parser.BaseParser
import kotlinx.collections.immutable.toPersistentList

/**
 * [GroupParser]
 *
 * @author why
 * @since 2025/1/16
 */
@AppParserDsl
class GroupParser(val topicTableParser: TopicTableParser) : BaseParser() {

    suspend fun Element.fetchGroupHomepageConverted(): ComposeGroupHomepage {
        requireNoError()

        val hotGroups = select(".groupsLarge > li").map { item ->
            ComposeGroup(
                name = item.select("a[title]").hrefId(),
                title = item.select("a[title]").attr("title"),
                members = item.select("small").text().parseCount(),
                images = ComposeImages.fromUrl(item.select("img").src())
            )
        }
        val newestGroups = select(".groupsSmall > li").map { item ->
            ComposeGroup(
                name = item.select(".inner > a.avatar").hrefId(),
                title = item.select(".inner > a.avatar").text(),
                members = item.select("small").text().parseCount(),
                images = ComposeImages.fromUrl(item.select("img.avatar").src())
            )
        }

        val topicItems = with(topicTableParser) { fetchGroupTopicTableItem() }

        return ComposeGroupHomepage(
            hotGroups = hotGroups.toPersistentList(),
            newestGroups = newestGroups.toPersistentList(),
            newestTopics = topicItems.toPersistentList()
        )
    }
}