package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.GroupDetailEntity
import com.xiaoyv.common.api.parser.entity.GroupIndexEntity
import com.xiaoyv.common.api.parser.entity.TopicSampleEntity
import com.xiaoyv.common.api.parser.fetchStyleBackgroundUrl
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.parseCount
import com.xiaoyv.common.api.parser.parseHtml
import com.xiaoyv.common.api.parser.requireNoError
import com.xiaoyv.common.api.parser.selectLegal
import com.xiaoyv.common.config.bean.SampleAvatar
import com.xiaoyv.widget.kts.useNotNull
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * @author why
 * @since 12/7/23
 */
fun Document.parserGroupDetail(groupId: String): GroupDetailEntity {
    requireNoError()

    val entity = GroupDetailEntity(id = groupId)

    selectLegal("#columnA").apply {
        entity.avatar = select(".grp_box > img").attr("src").optImageUrl()
        entity.name = select("h1.SecondaryNavTitle").text()
        entity.time = select(".grp_box > .tip").text()
        entity.summaryHtml = select(".line_detail > .tip").html()
        entity.summary = entity.summaryHtml.parseHtml().toString().trim()

        select(".chiiBtn").attr("href").also { actionUrl ->
            entity.gh = actionUrl.substringAfterLast("=")
            entity.isJoin = actionUrl.contains("bye")
        }
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

/**
 * 解析小组首页
 */
fun Element.parserGroupIndex(): GroupIndexEntity {
    val entity = GroupIndexEntity()

    selectLegal("#columnA").apply {
        entity.hotGroups = select(".groupsLarge > li").map { item ->
            val avatar = SampleAvatar()
            avatar.image = item.select("img").attr("src").optImageUrl()
            avatar.id = item.select("a").attr("href").substringAfterLast("/")
            avatar.title = item.select("a").text()
            avatar.desc = item.select("small").text()
            avatar
        }
        entity.hotTopics = select(".topic_list > tbody > tr")
            .filterNot { it.select("td").isEmpty() }
            .map { item ->
                val sampleEntity = TopicSampleEntity()
                val tds = item.select("tr td")

                useNotNull(tds.getOrNull(0)) {
                    sampleEntity.id = select("a").attr("href").substringAfterLast("/")
                    sampleEntity.commentCount = select("small").text().parseCount()
                    sampleEntity.title = select("a").text()
                }

                useNotNull(tds.getOrNull(1)) {
                    sampleEntity.groupId = select("a").attr("href").substringAfterLast("/")
                    sampleEntity.groupName = select("a").text()
                }

                useNotNull(tds.getOrNull(2)) {
                    sampleEntity.userId = select("a").attr("href").substringAfterLast("/")
                    sampleEntity.userName = select("a").text()
                }

                useNotNull(tds.getOrNull(3)) {
                    sampleEntity.time = text()
                }

                sampleEntity
            }
    }

    select("#columnB").apply {
        entity.newGroups = select(".groupsSmall > li").map { item ->
            val avatar = SampleAvatar()
            avatar.image = item.select("img").attr("src").optImageUrl()
            avatar.id = item.select(".inner a").attr("href").substringAfterLast("/")
            avatar.title = item.select(".inner a").text()
            avatar.desc = item.select("small").text()
            avatar
        }
    }

    return entity
}

/**
 * 解析小组的全部话题列表
 */
fun Element.parserGroupTopics(groupId: String): Pair<String, List<TopicSampleEntity>> {
    val groupName = select("h1.SecondaryNavTitle").text()
    return groupName to select(".topic_list > tbody > tr")
        .filterNot { it.select("td").isEmpty() }
        .map { item ->
            val sampleEntity = TopicSampleEntity()
            sampleEntity.groupId = groupId
            sampleEntity.groupName = groupName

            val tds = item.select("tr td")

            useNotNull(tds.getOrNull(0)) {
                sampleEntity.id = select("a").attr("href").substringAfterLast("/")
                sampleEntity.title = select("a").text()
            }

            useNotNull(tds.getOrNull(1)) {
                sampleEntity.userId = select("a").attr("href").substringAfterLast("/")
                sampleEntity.userName = select("a").text()
            }

            useNotNull(tds.getOrNull(2)) {
                sampleEntity.commentCount = text().parseCount()
            }

            useNotNull(tds.getOrNull(3)) {
                sampleEntity.time = text()
            }

            sampleEntity
        }
}