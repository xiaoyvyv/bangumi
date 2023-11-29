package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.MediaBoardEntity
import com.xiaoyv.common.api.parser.entity.MediaChapterEntity
import com.xiaoyv.common.api.parser.entity.MediaCharacterEntity
import com.xiaoyv.common.api.parser.entity.MediaCommentEntity
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.api.parser.entity.MediaMakerEntity
import com.xiaoyv.common.api.parser.entity.MediaReviewEntity
import com.xiaoyv.common.api.parser.fetchStyleBackgroundUrl
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.parseCount
import com.xiaoyv.common.kts.decodeUrl
import com.xiaoyv.common.widget.star.StarCommentView
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * @author why
 * @since 11/29/23
 */
fun Document.parserMediaChapters(): List<MediaChapterEntity> {
    return select(".line_detail > ul > li").map {
        if (it.select("h6").isEmpty()) return@map null
        val entity = MediaChapterEntity()
        entity.id = it.select("h6 a").attr("href").substringAfterLast("/")
        entity.titleCn = it.select("h6 .tip").text().substringAfterLast("/").trim()
        entity.titleNative = it.select("h6 a").text()
        entity.time = it.select("small").getOrNull(0)?.text().orEmpty()
        entity.comment =
            it.select("small").getOrNull(1)?.text().orEmpty().substringAfterLast("/").trim()
        entity
    }.filterNotNull()
}

fun Element.parserMediaComments(): List<MediaCommentEntity> {
    return select("#comment_box > .item").map {
        val entity = MediaCommentEntity()
        entity.id = it.select("a.avatar").attr("href")
            .substringAfterLast("/")
        entity.avatar = it.select("a.avatar > span").attr("style")
            .fetchStyleBackgroundUrl().optImageUrl()
        it.select(".text a.l").apply {
            entity.userName = text()
            entity.userId = attr("href").substringAfterLast("/")
        }
        entity.comment = it.select(".comment").text()
        entity.time = it.select(".text small").text().replace("@", "").trim()
        entity.star = it.select(".starstop-s > span").attr("class").let { starClass ->
            StarCommentView.parseScore(starClass)
        }
        entity
    }
}

fun Document.parserMediaReviews(): List<MediaReviewEntity> {
    return select("#entry_list > .item").map { item ->
        val entity = MediaReviewEntity()
        item.select(".entry .title").apply {
            entity.id = select("a").attr("href").substringAfterLast("/")
            entity.title = text()
        }
        entity.avatar = item.select("span.image > img").attr("src").optImageUrl()
        item.select("div.time .tip_j a").apply {
            entity.userName = text()
            entity.userId = attr("href").substringAfterLast("/")
        }
        entity.time = item.select("div.time small.time").text()
        entity.commentCount = item.select("div.time small.orange").text().parseCount()
        entity.comment = item.select(".content").text().removeSuffix("(more)").let { summary ->
            if (summary.length > 300) summary.substring(0, 300) else summary
        }
        entity
    }
}

fun Document.parserMediaBoards(): List<MediaBoardEntity> {
    return select(".topic_list > tbody > tr").map {
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
    }
}


fun Document.parserMediaMakers(): List<MediaMakerEntity> {
    return select("#columnInSubjectA > div").map {
        val entity = MediaMakerEntity()
        entity.id = it.select("h2 a").attr("href").substringAfterLast("/")
        entity.avatar = it.select(".avatar img").attr("src").optImageUrl()
        entity.titleCn = it.select("h2 .tip").text()
        entity.titleNative = it.select("h2 a").textNodes().firstOrNull()?.text().orEmpty()
        entity.personInfo = it.select(".prsn_info > p > span.badge_job").map { job -> job.text() }
        entity.tip = it.select(".prsn_info > span.tip").text()
        entity.commentCount = it.select(".rr > .na").text()
        entity
    }
}

fun Document.parserMediaCharacters(): List<MediaCharacterEntity> {
    return select("#columnInSubjectA > div").map {
        val entity = MediaCharacterEntity()
        entity.id = it.select("h2 a").attr("href").substringAfterLast("/")
        entity.avatar = it.select(".avatar img").attr("src").optImageUrl()
        entity.titleCn = it.select("h2 .tip").text()
        entity.titleNative = it.select("h2 a").textNodes().firstOrNull()?.text().orEmpty()
        entity.commentCount = it.select(".rr > .na").text()
        entity.personJob = it.select(".prsn_info > .badge_job").text()
        entity.personSex = it.select(".prsn_info > .tip").text()
        entity.actors = it.select(".actorBadge").map { actor ->
            val actorBadge = MediaCharacterEntity.ActorBadge()
            actorBadge.id = actor.select("a.avatar").attr("href").substringAfterLast("/")
            actorBadge.avatar = actor.select("a.avatar img").attr("src").optImageUrl()
            actorBadge.name = actor.select("p a.l").text()
            actorBadge.nameCn = actor.select("p small").text()
            actorBadge
        }
        entity
    }
}


fun Document.parserMediaDetail(): MediaDetailEntity {
    val entity = MediaDetailEntity()

    select(".nameSingle > a").apply {
        entity.id = attr("href").substringAfterLast("/")
        entity.titleCn = attr("title")
        entity.titleNative = text()
    }

    entity.cover = select("img.cover").attr("src").optImageUrl()
    entity.infos = select("#infobox > li").map { it.html() }

    // 推荐的条目
    entity.recommendIndex = select("#subjectPanelIndex .groupsLine > li").map { item ->
        val mediaIndex = MediaDetailEntity.MediaIndex()

        item.select(".innerWithAvatar a.avatar").apply {
            mediaIndex.id = attr("href").substringAfterLast("/")
            mediaIndex.title = text()
        }
        item.select(".innerWithAvatar small.grey a").apply {
            mediaIndex.userId = attr("href").substringAfterLast("/")
            mediaIndex.userName = text()
        }
        mediaIndex.userAvatar = item.select("li > a.avatar > span")
            .attr("style")
            .fetchStyleBackgroundUrl().optImageUrl()
        mediaIndex
    }

    // 谁在看
    entity.whoSee = select("#subjectPanelCollect .groupsLine > li").map { item ->
        val whoSee = MediaDetailEntity.MediaWhoSee()

        item.select(".innerWithAvatar a.avatar").apply {
            whoSee.userId = attr("href").substringAfterLast("/")
            whoSee.userName = text()
        }
        whoSee.userAvatar = item.select("li > a.avatar")
            .attr("style")
            .fetchStyleBackgroundUrl().optImageUrl()

        whoSee.star = item.select(".starstop-s > span")
            .attr("class").let { starClass ->
                StarCommentView.parseScore(starClass)
            }
        whoSee.time = item.select(".innerWithAvatar > small").text()
        whoSee
    }

    select("#subjectPanelCollect .tip_i > a").apply {
        entity.countWish = getOrNull(0)?.text().parseCount()
        entity.countDoing = getOrNull(1)?.text().parseCount()
        entity.countOnHold = getOrNull(2)?.text().parseCount()
        entity.countCollect = getOrNull(3)?.text().parseCount()
        entity.countDropped = getOrNull(4)?.text().parseCount()
    }

    entity.progressList = select(".prg_list > li").map { item ->
        val progress = MediaDetailEntity.MediaProgress()
        item.select("a").apply {
            progress.id = attr("href").substringAfterLast("/")
            progress.titleNative = attr("title")
            progress.no = text()
        }

        val relId = item.select("a").attr("rel")
        if (relId.isNotBlank()) select(relId).apply {
            select("span.tip").textNodes().forEach { text ->
                val t = text.text()
                if (t.startsWith("中文标题")) {
                    progress.titleCn = t.removePrefix("中文标题").removePrefix(":").trim()
                }
                if (t.startsWith("首播")) {
                    progress.firstTime = t.removePrefix("首播").removePrefix(":").trim()
                }
                if (t.startsWith("时长")) {
                    progress.duration = t.removePrefix("时长").removePrefix(":").trim()
                }
            }
            progress.commentCount = select("span.cmt small").text().parseCount()
        }
        progress
    }

    entity.subjectSummary = select("#subject_summary").text()

    entity.tags = select(".subject_tag_section .inner a").map { item ->
        val mediaTag = MediaDetailEntity.MediaTag()

        item.select("a.l").apply {
            mediaTag.tagName = attr("href").substringAfterLast("/").decodeUrl()
            mediaTag.title = select("span").text()
            mediaTag.count = select("small").text().parseCount()
        }

        mediaTag
    }

    entity.characters = select("#browserItemList > li").map { item ->
        val mediaCharacter = MediaDetailEntity.MediaCharacter()
        mediaCharacter.saveCount = item.select(".userContainer .fade").text().parseCount()
        item.select(".userContainer a.avatar").apply {
            mediaCharacter.id = attr("href").substringAfterLast("/")
            mediaCharacter.characterName = attr("title")
            mediaCharacter.avatar = select(".userImage > span").attr("style")
                .fetchStyleBackgroundUrl().optImageUrl()
        }
        item.select(".info .tip_j").apply {
            mediaCharacter.job = select(".badge_job_tip").text()
            mediaCharacter.characterNameCn = select("span.tip").text()
            mediaCharacter.persons = select("a").map { person ->
                val characterPerson = MediaDetailEntity.MediaCharacterPerson()
                characterPerson.personName = person.text()
                characterPerson.personId = person.attr("href").substringAfterLast("/")
                characterPerson
            }
        }

        mediaCharacter
    }

    entity.relativeMedia = select(".browserCoverMedium > li").map { item ->
        val mediaRelative = MediaDetailEntity.MediaRelative()
        mediaRelative.type = item.select("span.sub").text()
        item.select("a.avatar").apply {
            mediaRelative.id = attr("href").substringAfterLast("/")
            mediaRelative.titleCn = attr("title")
            mediaRelative.cover = select("span").attr("style")
                .fetchStyleBackgroundUrl().optImageUrl()
        }
        mediaRelative.titleNative = item.select("a.title").text()
        mediaRelative
    }

    entity.sameLikes = select(".coversSmall > li").map { item ->
        val mediaRelative = MediaDetailEntity.MediaRelative()
        mediaRelative.type = item.select("span.sub").text()
        item.select("a.avatar").apply {
            mediaRelative.id = attr("href").substringAfterLast("/")
            mediaRelative.titleCn = attr("title")
            mediaRelative.cover = select("span").attr("style")
                .fetchStyleBackgroundUrl().optImageUrl()
        }
        mediaRelative.titleNative = item.select(".info").text()
        mediaRelative
    }
    entity.rating = select(".global_rating, #ChartWarpper").let { item ->
        val rating = MediaDetailEntity.MediaRating()
        rating.globalRating = item.select(".global_score .number").text().toFloatOrNull()
        rating.globalRank = item.select("small.alarm").text().parseCount()
        rating.ratingCount = item.select(".chart_desc span").text().parseCount()
        rating.ratingDetail = item.select(".horizontalChart > li").map { chart ->
            val ratingItem = MediaDetailEntity.RatingItem()
            ratingItem.percent = chart.select("a.textTip").attr("title")
                .substringBefore("%").toFloatOrNull() ?: 0f
            ratingItem.label = chart.select(".label").text()
            ratingItem.count = chart.select(".count").text().parseCount()
            ratingItem
        }
        rating
    }

    entity.reviews = parserMediaReviews()
    entity.boards = parserMediaBoards()
    entity.comments = parserMediaComments()
    return entity
}