package com.xiaoyv.bangumi.ui.feature.user

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xiaoyv.bangumi.ui.feature.empty.EmptyFragment
import com.xiaoyv.bangumi.ui.feature.user.chart.ChartFragment
import com.xiaoyv.bangumi.ui.feature.user.overview.UserOverviewFragment
import com.xiaoyv.bangumi.ui.feature.user.sign.SignFragment
import com.xiaoyv.bangumi.ui.profile.page.save.SaveListFragment
import com.xiaoyv.bangumi.ui.timeline.page.TimelinePageFragment
import com.xiaoyv.common.config.annotation.TimelineType
import com.xiaoyv.common.config.annotation.UserCenterType
import com.xiaoyv.common.config.bean.tab.UserCenterTab

/**
 * Class: [UserAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class UserAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    internal var userId = ""

    internal val tabs = listOf(
        UserCenterTab("时光机", UserCenterType.TYPE_OVERVIEW),
        UserCenterTab("收藏", UserCenterType.TYPE_SAVE),
        UserCenterTab("统计", UserCenterType.TYPE_CHART),
        UserCenterTab("时间线", UserCenterType.TYPE_TIMELINE),
        UserCenterTab("关于", UserCenterType.TYPE_ABOUT)
    )

    override fun createFragment(position: Int): Fragment {
        val profileTab = tabs[position]
        val type = profileTab.type
        return when (type) {
            UserCenterType.TYPE_OVERVIEW -> UserOverviewFragment.newInstance()
            UserCenterType.TYPE_SAVE -> SaveListFragment.newInstance(userId)
            UserCenterType.TYPE_ABOUT -> SignFragment.newInstance()
            UserCenterType.TYPE_CHART -> ChartFragment.newInstance()
            UserCenterType.TYPE_TIMELINE -> TimelinePageFragment.newInstance(
                type = TimelineType.TYPE_ALL,
                userId = userId
            )
            else -> EmptyFragment.newInstance()
        }
    }

    override fun getItemCount(): Int {
        return tabs.size
    }
}