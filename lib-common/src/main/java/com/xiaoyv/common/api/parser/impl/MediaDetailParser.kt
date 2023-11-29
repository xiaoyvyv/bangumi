package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.MediaBoardEntity
import com.xiaoyv.common.api.parser.entity.MediaChapterEntity
import com.xiaoyv.common.api.parser.entity.MediaCommentEntity
import com.xiaoyv.common.api.parser.entity.MediaReviewEntity
import com.xiaoyv.common.api.parser.fetchStyleBackgroundUrl
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.widget.star.StarCommentView
import org.jsoup.nodes.Document

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

fun Document.parserMediaComments(): List<MediaCommentEntity> {
    return select("#comment_box > .item").map {
        val entity = MediaCommentEntity()
        entity.id = it.select("a.avatar").attr("href")
            .substringAfterLast("/")
        entity.avatar = it.select("a.avatar > span").attr("style")
            .fetchStyleBackgroundUrl().optImageUrl()
        entity.userName = it.select(".text a.l").text()
        entity.comment = it.select(".comment").text()
        entity.time = it.select(".text small").text().replace("@", "").trim()
        entity.star = it.select(".starstop-s > span").attr("class").let { starClass ->
            StarCommentView.parseScore(starClass)
        }
        entity
    }
}

fun Document.parserMediaReviews(): List<MediaReviewEntity> {
    return select("#entry_list > .item").map {
        val entity = MediaReviewEntity()
        val entry = it.select(".entry")
        entity.id = entry.select(".title a").attr("href").substringAfterLast("/")
        entity.title = entry.select(".title").text()
        entity.avatar = it.select("span.image > img").attr("src").optImageUrl()
        entity.userName = it.select("div.time .tip_j a.j").text()
        entity.userId = it.select("div.time .tip_j a.j").attr("href").substringAfterLast("/")
        entity.time = it.select("div.time small.time").text()
        entity.commentCount = it.select("div.time small.orange").text()
        entity.comment = it.select(".content").text().removeSuffix("(more)").let { summary ->
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