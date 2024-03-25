package com.xiaoyv.bangumi.ui.discover.index.detail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xiaoyv.bangumi.ui.discover.index.detail.page.IndexAttachFragment
import com.xiaoyv.common.config.bean.tab.IndexDetailAttachTab

/**
 * Class: [IndexDetailAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class IndexDetailAdapter(
    private val indexId: String = "",
    private val indexAttachTab: List<IndexDetailAttachTab>,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return IndexAttachFragment.newInstance(indexId, indexAttachTab[position].type)
    }

    override fun getItemCount(): Int {
        return indexAttachTab.size
    }
}