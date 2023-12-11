package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.hrefId
import com.xiaoyv.common.api.parser.entity.BlogCreateEntity
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.parser.requireNoError
import org.jsoup.nodes.Document

fun Document.parserCreateBlog(): BlogCreateEntity {
    requireNoError()

    val createEntity = BlogCreateEntity()
    createEntity.formHash = parserFormHash()
    createEntity.related = select("#columnB ul > li").map { item ->
        val relative = MediaDetailEntity.MediaRelative()
        item.select("li > a.avatar").apply {
            relative.id = hrefId()
            relative.titleCn = attr("title")
            relative.titleNative = item.select(".ll .avatar").text()
            relative.cover = select("img").attr("src").optImageUrl()
        }
        relative
    }
    return createEntity
}

/**
 * @author why
 * @since 12/2/23
 */
fun Document.parserFormHash(): String {
    return select("#editTopicForm input[name=formhash]").attr("value")
}
