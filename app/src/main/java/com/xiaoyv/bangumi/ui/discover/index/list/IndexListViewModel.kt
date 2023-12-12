package com.xiaoyv.bangumi.ui.discover.index.list

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.IndexItemEntity
import com.xiaoyv.common.api.parser.impl.parserIndexList

/**
 * Class: [GroupListViewModel]
 *
 * @author why
 * @since 12/12/23
 */
class IndexListViewModel : BaseListViewModel<IndexItemEntity>() {
    internal var isSortByNewest = false

    override suspend fun onRequestListImpl(): List<IndexItemEntity> {
        return BgmApiManager.bgmWebApi.queryIndexList(
            orderBy = if (isSortByNewest) null else "collect",
            page = current
        ).parserIndexList()
    }
}