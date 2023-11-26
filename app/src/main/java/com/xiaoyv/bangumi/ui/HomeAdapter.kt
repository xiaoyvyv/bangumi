package com.xiaoyv.bangumi.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xiaoyv.bangumi.ui.home.HomeFragment
import com.xiaoyv.bangumi.ui.media.MediaFragment
import com.xiaoyv.bangumi.ui.profile.ProfileFragment
import com.xiaoyv.bangumi.ui.rakuen.RakuenFragment
import com.xiaoyv.bangumi.ui.timeline.TimelineFragment

/**
 * Class: [HomeAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class HomeAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment.newInstance()
            1 -> TimelineFragment.newInstance()
            2 -> MediaFragment.newInstance()
            3 -> RakuenFragment.newInstance()
            4 -> ProfileFragment.newInstance()
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemCount(): Int {
        return 5
    }
}