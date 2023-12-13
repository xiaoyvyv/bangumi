package com.xiaoyv.bangumi.ui.profile.page.friend

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.FriendEntity
import com.xiaoyv.common.api.parser.impl.parserUserFriends

/**
 * Class: [UserFriendViewModel]
 *
 * @author why
 * @since 12/14/23
 */
class UserFriendViewModel : BaseListViewModel<FriendEntity>() {
    internal var userId: String = ""

    override suspend fun onRequestListImpl(): List<FriendEntity> {
        return BgmApiManager.bgmWebApi.queryUserFriends(userId).parserUserFriends()
    }
}