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
import com.xiaoyv.common.config.annotation.FeatureType
import com.xiaoyv.common.config.bean.tab.HomeBottomTab

/**
 * Class: [HomeAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class HomeAdapter(fragmentActivity: FragmentActivity, private val mainTabs: List<HomeBottomTab>) :
    FragmentStateAdapter(fragmentActivity) {

    /**
     * MediaFragment.newInstance()
     */
    override fun createFragment(position: Int): Fragment {
        return when (mainTabs[position].type) {
            FeatureType.TYPE_DISCOVER -> DiscoverFragment.newInstance()
            FeatureType.TYPE_TIMELINE -> TimelineFragment.newInstance()
            FeatureType.TYPE_RANK -> MediaFragment.newInstance()
            FeatureType.TYPE_PROCESS -> ProcessFragment.newInstance()
            FeatureType.TYPE_RAKUEN -> RakuenFragment.newInstance()
            FeatureType.TYPE_PROFILE -> ProfileFragment.newInstance()
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemCount(): Int {
        return mainTabs.size
    }
}