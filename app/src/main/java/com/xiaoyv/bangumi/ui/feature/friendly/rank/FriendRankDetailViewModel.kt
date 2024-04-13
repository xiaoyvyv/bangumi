package com.xiaoyv.bangumi.ui.feature.friendly.rank

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.database.BgmDatabaseManager
import com.xiaoyv.common.database.friendly.FriendlyRankEntity

class FriendRankDetailViewModel : BaseListViewModel<FriendlyRankEntity>() {
    /**
     * 页数大小
     */
    private var pageSize = 30

    /**
     * 超过 n 人评分的条目才统计
     */
    internal var overScoreCount = 1

    override val emptyCheck: Boolean
        get() = false

    override suspend fun onRequestListImpl(): List<FriendlyRankEntity> {
        return BgmDatabaseManager.friendlyRank.getFriendRank(
            scoreCount = overScoreCount,
            offset = (current - 1) * pageSize,
            limit = pageSize
        )
    }
}