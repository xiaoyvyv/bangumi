package com.xiaoyv.bangumi.ui.profile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xiaoyv.bangumi.ui.profile.page.ProfilePageFragment
import com.xiaoyv.common.config.annotation.ProfileType
import com.xiaoyv.common.config.bean.ProfileTab

/**
 * Class: [ProfileAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class ProfileAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    internal val tabs = listOf(
        ProfileTab("收藏", ProfileType.TYPE_BLOG),
        ProfileTab("人物", ProfileType.TYPE_MONO),
        ProfileTab("日志", ProfileType.TYPE_BLOG),
        ProfileTab("目录", ProfileType.TYPE_INDEX),
        ProfileTab("时间胶囊", ProfileType.TYPE_TIMELINE),
        ProfileTab("小组", ProfileType.TYPE_GROUP),
        ProfileTab("好友", ProfileType.TYPE_FRIEND),
        ProfileTab("维基", ProfileType.TYPE_WIKI)
    )

    override fun createFragment(position: Int): Fragment {
        return ProfilePageFragment.newInstance(tabs[position])
    }

    override fun getItemCount(): Int {
        return tabs.size
    }
}