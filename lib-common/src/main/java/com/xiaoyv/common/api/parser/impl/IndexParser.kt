@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.IndexAttachEntity
import com.xiaoyv.common.api.parser.entity.IndexDetailEntity
import com.xiaoyv.common.api.parser.entity.IndexEntity
import com.xiaoyv.common.api.parser.entity.IndexItemEntity
import com.xiaoyv.common.api.parser.fetchStyleBackgroundUrl
import com.xiaoyv.common.api.parser.hrefId
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.parseCount
import com.xiaoyv.common.api.parser.parseHtml
import com.xiaoyv.common.api.parser.requireNoError
import com.xiaoyv.common.api.parser.styleBackground
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.IndexTabCatType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.config.bean.IndexDetailAttachTab
import org.jsoup.nodes.Element

/**
 * @author why
 * @since 12/12/23
 */
fun Element.parserIndex(): IndexEntity {
    requireNoError()
    val entity = IndexEntity()

    select("#columnA").apply {
        entity.gridItems = select(".indexFocus").map { item ->
            val grid = IndexEntity.Grid()
            grid.images = item.select(".coverGrid img").map {
                it.attr("src").optImageUrl()
            }
            grid.id = item.select("a").hrefId()
            grid.title = item.select("a span").text()
            grid.desc = item.select("a h3").text()
            grid
        }

        val list = select("#timeline").map { timeline ->
            timeline.select("ul > li").map { item ->
                val itemEntity = IndexItemEntity()
                itemEntity.userId = item.select("a.avatar").hrefId()
                itemEntity.userAvatar = item.select("a.avatar > span").attr("style")
                    .fetchStyleBackgroundUrl().optImageUrl()
                itemEntity.userName = item.select(".info .tip_i a").text()
                itemEntity.time = item.select(".info .tip_j").text()
                itemEntity.id = item.select("h3 a").hrefId()
                itemEntity.title = item.select("h3").text()
                itemEntity.desc = item.select(".info > p").text()
                itemEntity.mediaType = item.select(".ico_subject_type").toString().let {
                    when {
                        it.contains("subject_type_1") -> MediaType.TYPE_BOOK
                        it.contains("subject_type_2") -> MediaType.TYPE_ANIME
                        it.contains("subject_type_3") -> MediaType.TYPE_MUSIC
                        it.contains("subject_type_4") -> MediaType.TYPE_GAME
                        it.contains("subject_type_6") -> MediaType.TYPE_REAL
                        else -> MediaType.TYPE_UNKNOWN
                    }
                }
                itemEntity.mediaCount = item.select(".ico_subject_type").text().parseCount()
                itemEntity
            }
        }
        entity.newItems = list.getOrNull(0).orEmpty()
        entity.hotItems = list.getOrNull(1).orEmpty()
    }
    return entity
}

/**
 * @author why
 * @since 12/12/23
 */
fun Element.parserIndexList(): List<IndexItemEntity> {
    requireNoError()
    return select("#timeline ul > li").map { item ->
        val itemEntity = IndexItemEntity()
        itemEntity.userId = item.select("a.avatar").hrefId()
        itemEntity.userAvatar = item.select("a.avatar > span").attr("style")
            .fetchStyleBackgroundUrl().optImageUrl()
        itemEntity.userName = item.select(".info .tip_i a").text()
        itemEntity.time = item.select(".info .tip_j").text()
        itemEntity.id = item.select("h3 a").hrefId()
        itemEntity.title = item.select("h3").text()
        itemEntity.desc = item.select(".info > p").text()
        itemEntity.mediaType = item.select(".ico_subject_type").toString().let {
            when {
                it.contains("subject_type_1") -> MediaType.TYPE_BOOK
                it.contains("subject_type_2") -> MediaType.TYPE_ANIME
                it.contains("subject_type_3") -> MediaType.TYPE_MUSIC
                it.contains("subject_type_4") -> MediaType.TYPE_GAME
                it.contains("subject_type_6") -> MediaType.TYPE_REAL
                else -> MediaType.TYPE_UNKNOWN
            }
        }
        itemEntity.mediaCount = item.select(".ico_subject_type").text().parseCount()
        itemEntity
    }
}

/**
 * 解析用户资料页面目录
 */
fun Element.parserUserIndexList(): List<IndexItemEntity> {
    requireNoError()
    val headerContainer = select(".headerContainer")
    val userId = headerContainer.select("a.avatar").hrefId()
    val userAvatar = headerContainer.select("a.avatar > span").styleBackground()
    val userName = headerContainer.select(".inner .name a").text()

    return select(".line_list > li").map { item ->
        val itemEntity = IndexItemEntity()
        itemEntity.userId = userId
        itemEntity.userAvatar = userAvatar
        itemEntity.userName = userName

        itemEntity.time = item.select("cite").text().removePrefix("创建于:")
        itemEntity.id = item.select("h6 a").hrefId()
        itemEntity.title = item.select("h6").text()
        itemEntity.mediaCount = item.select(".tip_j").text().parseCount()
        itemEntity
    }
}

/**
 * 解析目录详情
 */
fun Element.parserIndexDetail(indexId: String): IndexDetailEntity {
    requireNoError()
    val entity = IndexDetailEntity()
    entity.id = indexId
    entity.title = select("#header").text()

    select("#columnSubjectBrowserA").apply {
        entity.userId = select("a.avatar").hrefId()
        entity.userAvatar = select("a.avatar img").attr("src").optImageUrl()
        entity.userName = select(".grp_box > a").text()
        entity.isCollected = select(".chiiBtn").attr("href").contains("erase_collect")
        entity.mediaCount = select("#indexCatBox .selected small").text().parseCount()
        entity.contentHtml = select(".line_detail .tip").html()
        entity.content = entity.contentHtml.parseHtml().toString().trim()

        select(".grp_box .tip_j .tip").apply {
            entity.time = firstOrNull()?.text().orEmpty()
            entity.collectCount = getOrNull(1)?.text().parseCount()
        }
    }

    // 解析 TAB
    entity.tabs = select("#indexCatBox > ul > li").toList()
        .filter { it.hasClass("add").not() }
        .map { item ->
            val catUrl = item.select("a").attr("href")
            IndexDetailAttachTab(
                title = item.text(),
                type = if (catUrl.contains("=")) catUrl.substringAfterLast("=")
                else IndexTabCatType.TYPE_ALL
            )
        }

    // 删除相关的表单
    entity.deleteForms = select("#IndexEraseForm input")
        .map { it.attr("name") to it.attr("value") }
        .associate { it }

    // 解析全部的附件条目
    entity.totalAttach = parserIndexAttach(IndexTabCatType.TYPE_ALL)
    return entity
}

/**
 * 解析页面的附件条目
 */
fun Element.parserIndexAttach(@IndexTabCatType type: String): List<IndexAttachEntity> {
    return when (type) {
        // 人物角色类型
        IndexTabCatType.TYPE_CHARACTER, IndexTabCatType.TYPE_PERSON -> parserIndexAttachPerson()
        // 章节类型
        IndexTabCatType.TYPE_EP -> parserIndexAttachEp()
        // 条目类型
        IndexTabCatType.TYPE_BOOK,
        IndexTabCatType.TYPE_ANIME,
        IndexTabCatType.TYPE_MUSIC,
        IndexTabCatType.TYPE_GAME,
        IndexTabCatType.TYPE_REAL,
        -> parserIndexAttachSubject()
        // 全部
        else -> {
            val entities = mutableListOf<IndexAttachEntity>()
            entities.addAll(parserIndexAttachSubject())
            entities.addAll(parserIndexAttachPerson())
            entities.addAll(parserIndexAttachEp())
            entities
        }
    }
}

/**
 * 解析页面的附件条目
 */
private fun Element.parserIndexAttachSubject(): List<IndexAttachEntity> {
    return select("#browserItemList > li").mapIndexed { index, item ->
        val entity = IndexAttachEntity()
        entity.id = item.select("a.cover").hrefId()
        entity.coverImage = item.select("a.cover img").attr("src").optImageUrl()
        entity.title = item.select(".inner h3 a").text()
        entity.desc = item.select(".info").text()
        entity.isCollection = item.select(".collectBlock .collectModify").isNotEmpty()
        entity.comment = item.select("#comment_box").text()
        entity.pathType = BgmPathType.TYPE_SUBJECT
        entity.no = index + 1
        entity.mediaType = item.select(".ico_subject_type").toString().let { clsName ->
            when {
                clsName.contains("subject_type_1") -> MediaType.TYPE_BOOK
                clsName.contains("subject_type_2") -> MediaType.TYPE_ANIME
                clsName.contains("subject_type_3") -> MediaType.TYPE_MUSIC
                clsName.contains("subject_type_4") -> MediaType.TYPE_GAME
                clsName.contains("subject_type_6") -> MediaType.TYPE_REAL
                else -> MediaType.TYPE_UNKNOWN
            }
        }
        entity
    }
}

/**
 * 解析页面的附件章节
 */
private fun Element.parserIndexAttachEp(): List<IndexAttachEntity> {
    return select(".browserList > li").mapIndexed { index, item ->
        val entity = IndexAttachEntity()
        entity.id = item.select("a.avatar").hrefId()
        entity.coverImage = item.select("a.avatar img").attr("src").optImageUrl()
        entity.title = item.select(".inner h3 a").text()
        entity.desc = item.select(".tip").text()
        entity.comment = item.select("#comment_box").text()
        entity.pathType = BgmPathType.TYPE_EP
        entity.no = index + 1
        entity.mediaType = item.select(".ico_subject_type").toString().let { clsName ->
            when {
                clsName.contains("subject_type_1") -> MediaType.TYPE_BOOK
                clsName.contains("subject_type_2") -> MediaType.TYPE_ANIME
                clsName.contains("subject_type_3") -> MediaType.TYPE_MUSIC
                clsName.contains("subject_type_4") -> MediaType.TYPE_GAME
                clsName.contains("subject_type_6") -> MediaType.TYPE_REAL
                else -> MediaType.TYPE_UNKNOWN
            }
        }
        entity
    }
}

/**
 * 解析页面的附件人物或角色
 */
private fun Element.parserIndexAttachPerson(): List<IndexAttachEntity> {
    return select(".browserCrtList > div").mapIndexed { index, item ->
        val entity = IndexAttachEntity()
        val href = item.select("a.avatar").attr("href")
        entity.id = item.select("a.avatar").hrefId()
        entity.coverImage = item.select("a.avatar img").attr("src").optImageUrl()
        entity.title = item.select("h3 a").text()
        entity.desc = item.select(".prsn_info").text()
        entity.comment = item.select("#comment_box").text()
        entity.no = index + 1
        when {
            href.contains(BgmPathType.TYPE_CHARACTER) -> {
                entity.pathType = BgmPathType.TYPE_CHARACTER
            }

            href.contains(BgmPathType.TYPE_PERSON) -> {
                entity.pathType = BgmPathType.TYPE_PERSON
            }
        }
        entity
    }
}