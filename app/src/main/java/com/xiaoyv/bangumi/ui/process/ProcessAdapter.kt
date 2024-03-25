package com.xiaoyv.bangumi.ui.process

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xiaoyv.bangumi.ui.process.page.ProcessPageFragment
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.config.bean.tab.ProcessTab

/**
 * Class: [ProcessAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class ProcessAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    internal val tabs = listOf(
        ProcessTab("动画", MediaType.TYPE_ANIME),
        ProcessTab("书籍", MediaType.TYPE_BOOK),
        ProcessTab("三次元", MediaType.TYPE_REAL),
    )

    override fun createFragment(position: Int): Fragment {
        return ProcessPageFragment.newInstance(tabs[position].type)
    }

    override fun getItemCount(): Int {
        return tabs.size
    }
}