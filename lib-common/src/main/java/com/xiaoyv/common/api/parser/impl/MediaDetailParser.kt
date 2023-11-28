package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.MediaChapterEntity
import org.jsoup.nodes.Document

/**
 * @author why
 * @since 11/29/23
 */
fun Document.parserChapter(): List<MediaChapterEntity> {
    return select(".line_detail > ul > li").map {
        if (it.select("h6").isEmpty()) return@map null
        val entity = MediaChapterEntity()
        entity.id = it.select("h6 a").attr("href").substringAfterLast("/")
        entity.titleCn = it.select("h6 .tip").text().substringAfterLast("/").trim()
        entity.titleNative = it.select("h6 a").text()
        entity.time = it.select("small").getOrNull(0)?.text().orEmpty()
        entity.comment = it.select("small").getOrNull(1)?.text().orEmpty().substringAfterLast("/").trim()
        entity
    }.filterNotNull()
}