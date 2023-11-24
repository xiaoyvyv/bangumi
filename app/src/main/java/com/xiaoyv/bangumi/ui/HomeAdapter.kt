package com.xiaoyv.bangumi.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xiaoyv.bangumi.ui.blog.BlogFragment
import com.xiaoyv.bangumi.ui.group.GroupFragment
import com.xiaoyv.bangumi.ui.media.MediaFragment
import com.xiaoyv.bangumi.ui.home.HomeFragment
import com.xiaoyv.bangumi.ui.profile.ProfileFragment

/**
 * Class: [HomeAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class HomeAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    internal val bottomTabs = listOf(
        "广场",
        "日志",
        "媒体",
        "讨论",
        "我的"
    )

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment.newInstance()
            1 -> BlogFragment.newInstance()
            2 -> MediaFragment.newInstance()
            3 -> GroupFragment.newInstance()
            4 -> ProfileFragment.newInstance()
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemCount(): Int {
        return bottomTabs.size
    }
}