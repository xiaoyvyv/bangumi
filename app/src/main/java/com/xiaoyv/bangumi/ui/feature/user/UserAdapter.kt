package com.xiaoyv.bangumi.ui.feature.user

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xiaoyv.bangumi.ui.feature.empty.EmptyFragment
import com.xiaoyv.bangumi.ui.feature.user.chart.ChartFragment
import com.xiaoyv.bangumi.ui.feature.user.sign.SignFragment
import com.xiaoyv.common.config.annotation.UserCenterType
import com.xiaoyv.common.config.bean.UserCenterTab

/**
 * Class: [UserAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class UserAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    internal val tabs = listOf(
        UserCenterTab("时光机", UserCenterType.TYPE_OVERVIEW),
        UserCenterTab("收藏", UserCenterType.TYPE_SAVE),
        UserCenterTab("统计", UserCenterType.TYPE_CHART),
        UserCenterTab("时间线", UserCenterType.TYPE_TIMELINE),
        UserCenterTab("超展开", UserCenterType.TYPE_SUPER),
        UserCenterTab("关于", UserCenterType.TYPE_ABOUT)
    )

    override fun createFragment(position: Int): Fragment {
        val profileTab = tabs[position]
        val type = profileTab.type
        return when (type) {
            UserCenterType.TYPE_ABOUT -> SignFragment.newInstance()
            UserCenterType.TYPE_CHART -> ChartFragment.newInstance()
            else -> EmptyFragment.newInstance()
        }
    }

    override fun getItemCount(): Int {
        return tabs.size
    }
}