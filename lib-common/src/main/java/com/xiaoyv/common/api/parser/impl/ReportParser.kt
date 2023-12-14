package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.ReportEntity
import org.jsoup.nodes.Element

/**
 * @author why
 * @since 12/14/23
 */
fun Element.parserReportForm(): ReportEntity {
    val entity = ReportEntity()
    entity.hiddenForm = select("input[type=hidden], input[type=submit]")
        .map { it.attr("name") to it.attr("value") }
        .associate { it }
    return entity
}