package com.xiaoyv.bangumi.ui.discover

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xiaoyv.bangumi.ui.discover.blog.BlogFragment
import com.xiaoyv.bangumi.ui.discover.character.CharacterFragment
import com.xiaoyv.bangumi.ui.discover.friend.FriendFragment
import com.xiaoyv.bangumi.ui.discover.group.GroupFragment
import com.xiaoyv.bangumi.ui.discover.home.HomeFragment
import com.xiaoyv.bangumi.ui.discover.index.IndexFragment
import com.xiaoyv.bangumi.ui.discover.wiki.WikiFragment
import com.xiaoyv.bangumi.ui.feature.empty.EmptyFragment
import com.xiaoyv.common.config.annotation.DiscoverType
import com.xiaoyv.common.config.bean.DiscoverTab

/**
 * Class: [DiscoverAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class DiscoverAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    internal val tabs = listOf(
        DiscoverTab("首页", DiscoverType.TYPE_HOME),
//        DiscoverTab("人物", DiscoverType.TYPE_MONO),
        DiscoverTab("日志", DiscoverType.TYPE_BLOG),
        DiscoverTab("目录", DiscoverType.TYPE_INDEX),
        DiscoverTab("小组", DiscoverType.TYPE_GROUP),
//        DiscoverTab("好友", DiscoverType.TYPE_FRIEND),
//        DiscoverTab("维基", DiscoverType.TYPE_WIKI)
    )

    override fun createFragment(position: Int): Fragment {
        val discoverTab = tabs[position]
        val type = discoverTab.type
        return when (type) {
            DiscoverType.TYPE_HOME -> HomeFragment.newInstance()
            DiscoverType.TYPE_MONO -> CharacterFragment.newInstance()
            DiscoverType.TYPE_BLOG -> BlogFragment.newInstance()
            DiscoverType.TYPE_INDEX -> IndexFragment.newInstance()
            DiscoverType.TYPE_GROUP -> GroupFragment.newInstance()
            DiscoverType.TYPE_FRIEND -> FriendFragment.newInstance()
            DiscoverType.TYPE_WIKI -> WikiFragment.newInstance()
            else -> EmptyFragment.newInstance()
        }
    }

    override fun getItemCount(): Int {
        return tabs.size
    }
}