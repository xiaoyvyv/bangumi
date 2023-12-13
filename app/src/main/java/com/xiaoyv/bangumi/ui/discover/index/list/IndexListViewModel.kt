package com.xiaoyv.bangumi.ui.discover.index.list

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.IndexItemEntity
import com.xiaoyv.common.api.parser.impl.parserIndexList
import com.xiaoyv.common.api.parser.impl.parserUserIndexList
import com.xiaoyv.common.helper.UserHelper

/**
 * Class: [GroupListViewModel]
 *
 * @author why
 * @since 12/12/23
 */
class IndexListViewModel : BaseListViewModel<IndexItemEntity>() {
    internal var isSortByNewest = false

    internal var userId: String = ""

    internal val isMine: Boolean
        get() = userId.isNotBlank() && userId == UserHelper.currentUser.id

    override suspend fun onRequestListImpl(): List<IndexItemEntity> {
        return if (userId.isNotBlank()) {
            BgmApiManager.bgmWebApi.queryUserIndex(userId, current)
                .parserUserIndexList()
        } else {
            BgmApiManager.bgmWebApi.queryIndexList(
                orderBy = if (isSortByNewest) null else "collect",
                page = current
            ).parserIndexList()
        }
    }
}