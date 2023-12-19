package com.xiaoyv.bangumi.ui.profile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xiaoyv.bangumi.ui.discover.blog.BlogFragment
import com.xiaoyv.bangumi.ui.discover.mono.MonoFragment
import com.xiaoyv.bangumi.ui.feature.empty.EmptyFragment
import com.xiaoyv.bangumi.ui.profile.page.friend.UserFriendFragment
import com.xiaoyv.bangumi.ui.profile.page.group.UserGroupFragment
import com.xiaoyv.bangumi.ui.profile.page.index.UserIndexFragment
import com.xiaoyv.bangumi.ui.profile.page.save.SaveListFragment
import com.xiaoyv.common.config.annotation.ProfileType
import com.xiaoyv.common.config.bean.ProfileTab
import com.xiaoyv.common.helper.UserHelper

/**
 * Class: [ProfileAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class ProfileAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    internal val tabs = listOf(
        ProfileTab("收藏", ProfileType.TYPE_COLLECTION),
        ProfileTab("目录", ProfileType.TYPE_INDEX),
        ProfileTab("日志", ProfileType.TYPE_BLOG),
        ProfileTab("小组", ProfileType.TYPE_GROUP),
        ProfileTab("好友", ProfileType.TYPE_FRIEND),
        ProfileTab("人物", ProfileType.TYPE_MONO),
        ProfileTab("维基", ProfileType.TYPE_WIKI)
    )

    override fun createFragment(position: Int): Fragment {
        val profileTab = tabs[position]
        val type = profileTab.type
        val myId = UserHelper.currentUser.id.orEmpty()

        return when (type) {
            // 收藏
            ProfileType.TYPE_COLLECTION -> SaveListFragment.newInstance(
                userId = myId,
                requireLogin = true
            )
            // 日志
            ProfileType.TYPE_BLOG -> BlogFragment.newInstance(myId, requireLogin = true)
            // 目录
            ProfileType.TYPE_INDEX -> UserIndexFragment.newInstance(myId, requireLogin = true)
            // 小组
            ProfileType.TYPE_GROUP -> UserGroupFragment.newInstance(myId, requireLogin = true)
            // 好友
            ProfileType.TYPE_FRIEND -> UserFriendFragment.newInstance(myId, requireLogin = true)
            // 人物
            ProfileType.TYPE_MONO -> MonoFragment.newInstance(myId, requireLogin = true)
            else -> EmptyFragment.newInstance()
        }
    }

    override fun getItemCount(): Int {
        return tabs.size
    }
}