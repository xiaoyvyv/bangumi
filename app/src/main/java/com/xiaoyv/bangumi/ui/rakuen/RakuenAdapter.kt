@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.ui.rakuen

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xiaoyv.bangumi.ui.rakuen.page.RakuenPageFragment
import com.xiaoyv.common.config.annotation.SuperType
import com.xiaoyv.common.config.bean.tab.SuperTopicTab

/**
 * Class: [RakuenAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class RakuenAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    internal val tabs = listOf(
        SuperTopicTab("全部", SuperType.TYPE_ALL),
        SuperTopicTab("小组", SuperType.TYPE_GROUP),
        SuperTopicTab("章节", SuperType.TYPE_EP),
        SuperTopicTab("条目", SuperType.TYPE_SUBJECT),
        SuperTopicTab("人物", SuperType.TYPE_MONO),
    )

    override fun createFragment(position: Int): Fragment {
        return RakuenPageFragment.newInstance(tabs[position])
    }

    override fun getItemCount(): Int {
        return tabs.size
    }
}