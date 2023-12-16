package com.xiaoyv.bangumi.ui.profile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xiaoyv.bangumi.ui.discover.blog.BlogFragment
import com.xiaoyv.bangumi.ui.feature.empty.EmptyFragment
import com.xiaoyv.bangumi.ui.profile.page.friend.UserFriendFragment
import com.xiaoyv.bangumi.ui.profile.page.group.UserGroupFragment
import com.xiaoyv.bangumi.ui.profile.page.index.UserIndexFragment
import com.xiaoyv.bangumi.ui.profile.page.save.SaveListFragment
import com.xiaoyv.bangumi.ui.timeline.page.TimelinePageFragment
import com.xiaoyv.common.config.annotation.ProfileType
import com.xiaoyv.common.config.annotation.TimelineType
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
        ProfileTab("日志", ProfileType.TYPE_BLOG),
        ProfileTab("目录", ProfileType.TYPE_INDEX),
        ProfileTab("好友", ProfileType.TYPE_FRIEND),
        ProfileTab("人物", ProfileType.TYPE_MONO),
        ProfileTab("小组", ProfileType.TYPE_GROUP),
        ProfileTab("时间胶囊", ProfileType.TYPE_TIMELINE),
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
                isMine = true
            )
            // 时间线
            ProfileType.TYPE_TIMELINE -> TimelinePageFragment.newInstance(
                type = TimelineType.TYPE_USER,
                userId = myId
            )
            // 日志
            ProfileType.TYPE_BLOG -> BlogFragment.newInstance(myId)
            // 目录
            ProfileType.TYPE_INDEX -> UserIndexFragment.newInstance(myId)
            // 小组
            ProfileType.TYPE_GROUP -> UserGroupFragment.newInstance(myId)
            // 好友
            ProfileType.TYPE_FRIEND-> UserFriendFragment.newInstance(myId)
            else -> EmptyFragment.newInstance()
        }
    }

    override fun getItemCount(): Int {
        return tabs.size
    }
}