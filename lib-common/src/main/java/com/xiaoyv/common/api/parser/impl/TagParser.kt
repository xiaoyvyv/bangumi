package com.xiaoyv.common.api.parser.impl

import com.xiaoyv.common.api.parser.entity.SearchResultEntity
import com.xiaoyv.common.config.annotation.BgmPathType
import org.jsoup.nodes.Element

/**
 * @author why
 * @since 12/10/23
 */
fun Element.parserTagDetail(): List<SearchResultEntity> {
    // 标签详情页面复用 BgmPathType.TYPE_SEARCH_SUBJECT 的解析逻辑
    return parserSearchResult(BgmPathType.TYPE_SEARCH_SUBJECT)
}