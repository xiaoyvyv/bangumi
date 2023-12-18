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
    internal var selectedMode: Boolean = false
    internal var requireLogin: Boolean = false

    internal val isMine: Boolean
        get() = userId.isNotBlank() && userId == UserHelper.currentUser.id

    internal val queryForUser: Boolean
        get() = userId.isNotBlank()

    override suspend fun onRequestListImpl(): List<IndexItemEntity> {
        if (requireLogin) {
            require(UserHelper.isLogin) { "你还没有登录呢" }
        }

        return when {
            // 查询指定用户的目录
            queryForUser -> {
                // 是否拼接查询收藏路径，复用下 isSortByNewest 字段
                // 指定用户ID时，isSortByNewest == true，查询创建的目录，反之查询收藏的目录
                val queryCollect = if (isSortByNewest) "" else "/collect"
                BgmApiManager.bgmWebApi.queryUserIndex(userId, queryCollect, current).let {
                    if (isSortByNewest) it.parserUserIndexList()
                    else it.parserIndexList()
                }
            }
            // 查询全部目录列表
            else -> {
                BgmApiManager.bgmWebApi.queryIndexList(
                    orderBy = if (isSortByNewest) null else "collect",
                    page = current
                ).parserIndexList()
            }
        }
    }
}