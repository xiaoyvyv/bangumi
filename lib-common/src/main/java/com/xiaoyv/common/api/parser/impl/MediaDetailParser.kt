package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.MediaChapterEntity
import com.xiaoyv.common.api.parser.entity.MediaCommentEntity
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
        entity.time = it.select(".text small").text()
        entity.star = it.select(".starstop-s > span").attr("class").let {
            StarCommentView.parseScore(it)
        }
        entity
    }
}