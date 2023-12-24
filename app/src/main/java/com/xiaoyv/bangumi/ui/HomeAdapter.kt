package com.xiaoyv.bangumi.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xiaoyv.bangumi.ui.discover.DiscoverFragment
import com.xiaoyv.bangumi.ui.media.MediaFragment
import com.xiaoyv.bangumi.ui.process.ProcessFragment
import com.xiaoyv.bangumi.ui.profile.ProfileFragment
import com.xiaoyv.bangumi.ui.rakuen.RakuenFragment
import com.xiaoyv.bangumi.ui.timeline.TimelineFragment
import com.xiaoyv.common.config.GlobalConfig
import com.xiaoyv.common.helper.ConfigHelper

/**
 * Class: [HomeAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class HomeAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    /**
     * 中心TAB
     */
    private val centerTab: Fragment
        get() = when (ConfigHelper.centerTabType) {
            GlobalConfig.PAGE_RANK -> MediaFragment.newInstance()
            GlobalConfig.PAGE_PROCESS -> ProcessFragment.newInstance()
            else -> MediaFragment.newInstance()
        }

    /**
     * MediaFragment.newInstance()
     */
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DiscoverFragment.newInstance()
            1 -> TimelineFragment.newInstance()
            2 -> centerTab
            3 -> RakuenFragment.newInstance()
            4 -> ProfileFragment.newInstance()
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemCount(): Int {
        return 5
    }
}