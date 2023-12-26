@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.MediaBoardEntity
import com.xiaoyv.common.api.parser.entity.MediaCharacterEntity
import com.xiaoyv.common.api.parser.entity.MediaCollectForm
import com.xiaoyv.common.api.parser.entity.MediaCommentEntity
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.api.parser.entity.MediaMakerEntity
import com.xiaoyv.common.api.parser.entity.MediaReviewBlogEntity
import com.xiaoyv.common.api.parser.fetchStyleBackgroundUrl
import com.xiaoyv.common.api.parser.firsTextNode
import com.xiaoyv.common.api.parser.hrefId
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.parseCount
import com.xiaoyv.common.api.parser.parseHtml
import com.xiaoyv.common.api.parser.parseStar
import com.xiaoyv.common.api.parser.parserTime
import com.xiaoyv.common.api.parser.requireNoError
import com.xiaoyv.common.api.parser.selectLegal
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.kts.decodeUrl
import com.xiaoyv.common.kts.groupValueOne
import com.xiaoyv.widget.kts.subListLimit
import com.xiaoyv.widget.kts.useNotNull
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element


/**
 * 解析媒体底部吐槽评论
 */
fun Element.parserMediaComments(): List<MediaCommentEntity> {
    requireNoError()

    return select("#comment_box > .item").map {
        val entity = MediaCommentEntity()
        entity.id = it.select("a.avatar").attr("href")
            .substringAfterLast("/")
        entity.avatar = it.select("a.avatar > span").attr("style")
            .fetchStyleBackgroundUrl().optImageUrl()
        it.select(".text a.l").apply {
            entity.userName = text()
            entity.userId = hrefId()
        }
        entity.comment = it.select(".comment").text()
        entity.time = it.select(".text small").text().replace("@", "").trim()
        entity.star = it.parseStar()
        entity
    }
}

/**
 * 解析媒体的评论日志
 */
fun Element.parserMediaBlog(): List<MediaReviewBlogEntity> {
    requireNoError()

    return select("#entry_list > .item").map { item ->
        val entity = MediaReviewBlogEntity()
        item.select(".entry .title").apply {
            entity.id = select("a").hrefId()
            entity.title = text()
        }
        entity.avatar = item.select("span.image > img").attr("src").optImageUrl()
        item.select("div.time .tip_j a").apply {
            entity.userName = text()
            entity.userId = hrefId()
        }
        entity.time = item.select("div.time small.time").text()
        entity.commentCount = item.select("div.time small.orange").text().parseCount()
        entity.comment = item.select(".content").text().removeSuffix("(more)").let { summary ->
            if (summary.length > 300) summary.substring(0, 300) else summary
        }
        entity
    }
}

/**
 * 解析媒体的话题讨论板
 */
fun Document.parserMediaBoards(): List<MediaBoardEntity> {
    requireNoError()

    return select(".topic_list > tbody > tr").map {
        if (it.select(".more").isNotEmpty()) return@map null
        val entity = MediaBoardEntity()
        val tds = it.select("tr td")
        entity.id = tds.getOrNull(0)?.select("a")?.attr("href").orEmpty()
            .substringAfterLast("/")
        entity.content = tds.getOrNull(0)?.text().orEmpty()
        entity.userName = tds.getOrNull(1)?.text().orEmpty()
        entity.userId = tds.getOrNull(1)?.select("a")?.attr("href").orEmpty()
            .substringAfterLast("/")
        entity.replies = tds.getOrNull(2)?.text().orEmpty()
        entity.time = tds.getOrNull(3)?.text().orEmpty()
        entity
    }.filterNotNull()
}

/**
 * 解析媒体的制作者
 */
fun Document.parserMediaMakers(): List<MediaMakerEntity> {
    requireNoError()

    return select("#columnInSubjectA > div").map {
        val entity = MediaMakerEntity()
        entity.id = it.select("h2 a").hrefId()
        entity.avatar = it.select(".avatar img").attr("src").optImageUrl()
        entity.titleCn = it.select("h2 .tip").text()
        entity.titleNative = it.select("h2 a").firsTextNode()
        entity.personInfo = it.select(".prsn_info > p > span.badge_job").map { job -> job.text() }
        entity.tip = it.select(".prsn_info > span.tip").text()
        entity.commentCount = it.select(".rr > .na").text()
        entity
    }
}

/**
 * 解析媒体的角色
 */
fun Document.parserMediaCharacters(): List<MediaCharacterEntity> {
    requireNoError()

    return select("#columnInSubjectA > div").map {
        val entity = MediaCharacterEntity()
        entity.id = it.select("h2 a").hrefId()
        entity.avatar = it.select(".avatar img").attr("src").optImageUrl()
        entity.titleCn = it.select("h2 .tip").text()
        entity.titleNative = it.select("h2 a").firsTextNode()
        entity.commentCount = it.select(".rr > .na").text()
        entity.personJob = it.select(".prsn_info > .badge_job").text()
        entity.personSex = it.select(".prsn_info > .tip").text()
        entity.actors = it.select(".actorBadge").map { actor ->
            val actorBadge = MediaCharacterEntity.ActorBadge()
            actorBadge.id = actor.select("a.avatar").hrefId()
            actorBadge.avatar = actor.select("a.avatar img").attr("src").optImageUrl()
            actorBadge.name = actor.select("p a.l").text()
            actorBadge.nameCn = actor.select("p small").text()
            actorBadge
        }
        entity
    }
}

/**
 * 解析媒体详情
 */
fun Document.parserMediaDetail(): MediaDetailEntity {
    requireNoError()

    val entity = MediaDetailEntity()
    val headerSubject = select("#headerSubject")
    headerSubject.select(".nameSingle > a").apply {
        entity.id = hrefId()
        entity.titleCn = attr("title")
        entity.titleNative = text()
    }
    
    entity.locked = headerSubject.select(".tipIntro").isNotEmpty()

    val infoBox = select("#infobox > li")
    val infoBoxText = infoBox.text()
    val coverUrl = select("img.cover").attr("src")

    entity.subtype = select(".nameSingle small").text()
    entity.cover = coverUrl.optImageUrl()
    entity.coverLarge = coverUrl.optImageUrl(largest = true)
    entity.infoHtml = infoBox.map { it.html() }
    entity.infoShort = entity.infoHtml.subListLimit(10).map { it.parseHtml() }
    entity.time = infoBoxText.parserTime()

    // 收藏状态
    entity.collectState = select("#panelInterestWrapper").let { item ->
        val collectForm = MediaCollectForm()
        collectForm.gh = select("#collectBoxForm").attr("action").substringAfterLast("=")
        collectForm.mediaId = entity.id
        collectForm.titleCn = entity.titleCn
        collectForm.titleNative = entity.titleNative
        collectForm.comment = item.select("#comment").text()
        collectForm.interest =
            item.select("input[checked=checked][name=interest]").attr("value").ifBlank { "0" }
        collectForm.referer = item.select("input[name=referer]").attr("value")
        collectForm.tags = item.select("input#tags").attr("value").trim()
        collectForm.update = item.select("input[name=update]").attr("value")
        collectForm.privacy = item.select("input[name=privacy]").attr("checked").let { checked ->
            if (checked.isNotBlank()) 1 else 0
        }
        collectForm.score = item.select("input[name=rate][checked]")
            .ifEmpty { item.select("input[name=rating][checked]") }
            .attr("value").toIntOrNull() ?: 0
        collectForm.normalTags = item.select(".tagList")
            .getOrNull(0)?.select("a").orEmpty()
            .map { a ->
                MediaDetailEntity.MediaTag(tagName = a.text())
            }
        collectForm.myTags = item.select(".tagList")
            .getOrNull(1)?.select("a").orEmpty()
            .map { a ->
                MediaDetailEntity.MediaTag(tagName = a.text())
            }
        collectForm
    }

    // 推荐的条目
    entity.recommendIndex = select("#subjectPanelIndex .groupsLine > li").map { item ->
        val mediaIndex = MediaDetailEntity.MediaIndex()

        item.select(".innerWithAvatar a.avatar").apply {
            mediaIndex.id = hrefId()
            mediaIndex.title = text()
        }
        item.select(".innerWithAvatar small.grey a").apply {
            mediaIndex.userId = hrefId()
            mediaIndex.userName = text()
        }
        mediaIndex.userAvatar = item.select("li > a.avatar > span")
            .attr("style")
            .fetchStyleBackgroundUrl().optImageUrl()
        mediaIndex
    }

    // 谁在看
    entity.whoSee = select("#subjectPanelCollect .groupsLine > li").map { item ->
        val whoSee = MediaDetailEntity.MediaWho()

        item.select(".innerWithAvatar a.avatar").apply {
            whoSee.id = hrefId()
            whoSee.name = text()
        }
        whoSee.avatar = item.select("li > a.avatar span")
            .attr("style")
            .fetchStyleBackgroundUrl().optImageUrl()

        whoSee.star = item.parseStar()
        whoSee.time = item.select(".innerWithAvatar small").text()
        whoSee
    }

    // 统计数据
    select("#subjectPanelCollect .tip_i > a").apply {
        entity.countWish = getOrNull(0)?.text().parseCount()
        entity.countDoing = getOrNull(1)?.text().parseCount()
        entity.countOnHold = getOrNull(2)?.text().parseCount()
        entity.countCollect = getOrNull(3)?.text().parseCount()
        entity.countDropped = getOrNull(4)?.text().parseCount()
    }

    // 简介
    entity.subjectSummary = select("#subject_summary").text()

    // 媒体类型
    entity.mediaType = select(".global_rating .global_score small.grey").text().lowercase().let {
        when {
            it.contains("anime") -> MediaType.TYPE_ANIME
            it.contains("book") -> MediaType.TYPE_BOOK
            it.contains("music") -> MediaType.TYPE_MUSIC
            it.contains("game") -> MediaType.TYPE_GAME
            it.contains("real") -> MediaType.TYPE_REAL
            else -> MediaType.TYPE_ANIME
        }
    }

    // 进度框
    val prgTexts = select(".prgText")
    when (entity.mediaType) {
        // 书籍进度
        MediaType.TYPE_BOOK -> {
            useNotNull(prgTexts.getOrNull(0)) {
                entity.progress = select("input").attr("value").parseCount()
                entity.progressMax = text().parseCount()
            }
            useNotNull(prgTexts.getOrNull(1)) {
                entity.progressSecond = select("input").attr("value").parseCount()
                entity.progressSecondMax = text().parseCount()
            }
        }
        // 动画或三次元总进度
        MediaType.TYPE_ANIME, MediaType.TYPE_REAL -> {
            useNotNull(prgTexts.getOrNull(0)) {
                entity.progress = select("input").attr("value").parseCount()
                entity.progressMax = text().parseCount()
            }

            // 想看状态的话数解析不到，直接从 infobox 读取话数
            if (entity.progressMax == 0) {
                entity.progressMax = "话数:\\s*(\\d+)".toRegex()
                    .groupValueOne(infoBoxText).parseCount()
            }
        }
    }

    // 标签
    entity.tags = select(".subject_tag_section .inner a").map { item ->
        if (item.id() == "show_user_tags") return@map null
        val mediaTag = MediaDetailEntity.MediaTag()

        item.select("a.l").apply {
            mediaTag.tagName = hrefId().decodeUrl()
            mediaTag.title = select("span").text()
            mediaTag.count = select("small").text().parseCount()
            mediaTag.mediaType = attr("href").trim('/').substringBefore("/").trim()
            mediaTag.url = attr("href")
        }

        mediaTag
    }.filterNotNull()

    // 角色
    entity.characters = select("#browserItemList > li").map { item ->
        val mediaCharacter = MediaDetailEntity.MediaCharacter()
        mediaCharacter.saveCount = item.select(".userContainer .fade").text().parseCount()
        item.select(".userContainer a.avatar").apply {
            mediaCharacter.id = hrefId()
            mediaCharacter.characterName = attr("title")
            mediaCharacter.avatar = select(".userImage > span").attr("style")
                .fetchStyleBackgroundUrl().optImageUrl()
        }
        item.select(".info .tip_j").apply {
            mediaCharacter.jobs = select(".badge_job_tip").map { it.text() }
            mediaCharacter.characterNameCn = select("span.tip").text()
            mediaCharacter.persons = select("a").map { person ->
                val characterPerson = MediaDetailEntity.MediaCharacterPerson()
                characterPerson.personName = person.text()
                characterPerson.personId = person.hrefId()
                characterPerson
            }
        }

        mediaCharacter
    }

    // 关联的条目
    entity.relativeMedia = select(".browserCoverMedium > li").map { item ->
        val mediaRelative = MediaDetailEntity.MediaRelative()
        mediaRelative.type = item.select("span.sub").text()
        item.select("a.avatar").apply {
            mediaRelative.id = hrefId()
            mediaRelative.titleCn = attr("title")
            mediaRelative.cover = select("span").attr("style")
                .fetchStyleBackgroundUrl().optImageUrl()
        }
        mediaRelative.titleNative = item.select("a.title").text()
        mediaRelative
    }

    // 喜欢的会员大概会喜欢
    entity.sameLikes = select(".coversSmall > li").map { item ->
        val mediaRelative = MediaDetailEntity.MediaRelative()
        mediaRelative.type = item.select("span.sub").text()
        item.select("a.avatar").apply {
            mediaRelative.id = hrefId()
            mediaRelative.titleCn = attr("title")
            mediaRelative.cover = select("span").attr("style")
                .fetchStyleBackgroundUrl().optImageUrl()
        }
        mediaRelative.titleNative = item.select(".info").text()
        mediaRelative
    }

    // 评分
    entity.rating = select(".global_rating, #ChartWarpper").let { item ->
        val rating = MediaDetailEntity.MediaRating()
        rating.globalRating = item.select(".global_score .number").text().toFloatOrNull()
        rating.description = item.select(".description").text()
        rating.globalRank = item.select("small.alarm").text().parseCount()
        rating.ratingCount = item.select(".chart_desc span").text().parseCount()
        rating.ratingDetail = item.select(".horizontalChart > li").map { chart ->
            val ratingItem = MediaDetailEntity.RatingItem()
            ratingItem.percent = chart.select("a.textTip").attr("title")
                .substringBefore("%").toFloatOrNull() ?: 0f
            ratingItem.label = chart.select(".label").text().parseCount()
            ratingItem.count = chart.select(".count").text().parseCount()
            ratingItem
        }
        rating.standardDeviation = rating.calculateStandardDeviation()
        rating
    }

    entity.reviews = parserMediaBlog()
    entity.boards = parserMediaBoards()
    entity.comments = parserMediaComments()
    return entity
}