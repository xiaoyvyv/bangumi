package com.xiaoyv.bangumi.ui.feature.tag

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.SearchResultEntity
import com.xiaoyv.common.api.parser.impl.parserTagDetail
import com.xiaoyv.common.config.annotation.BrowserSortType
import com.xiaoyv.common.config.annotation.MediaType

/**
 * Class: [TagDetailViewModel]
 *
 * @author why
 * @since 12/10/23
 */
class TagDetailViewModel : BaseListViewModel<SearchResultEntity>() {
    internal var mediaType = MediaType.TYPE_UNKNOWN
    internal var tag = ""
    internal var time = ""

    @BrowserSortType
    internal var sort = BrowserSortType.TYPE_DEFAULT

    override suspend fun onRequestListImpl(): List<SearchResultEntity> {
        require(mediaType.isNotBlank()) { "标签条目类型不存在" }
        require(tag.isNotBlank()) { "标签不存在" }
        return BgmApiManager.bgmWebApi.queryTagDetail(mediaType, tag, time, sort, current)
            .parserTagDetail()
    }
}