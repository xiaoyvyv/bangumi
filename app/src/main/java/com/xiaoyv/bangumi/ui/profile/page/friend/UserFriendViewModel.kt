package com.xiaoyv.bangumi.ui.profile.page.friend

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.FriendEntity
import com.xiaoyv.common.api.parser.impl.parserUserFriends
import com.xiaoyv.common.helper.UserHelper

/**
 * Class: [UserFriendViewModel]
 *
 * @author why
 * @since 12/14/23
 */
class UserFriendViewModel : BaseListViewModel<FriendEntity>() {
    internal var userId: String = ""
    internal var requireLogin: Boolean = false

    override suspend fun onRequestListImpl(): List<FriendEntity> {
        if (requireLogin) require(UserHelper.isLogin) { "你还没有登录呢" }

        return BgmApiManager.bgmWebApi.queryUserFriends(userId).parserUserFriends()
    }
}