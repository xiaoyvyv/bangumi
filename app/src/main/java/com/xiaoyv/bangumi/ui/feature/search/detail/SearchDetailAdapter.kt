package com.xiaoyv.bangumi.ui.feature.search.detail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xiaoyv.bangumi.ui.feature.empty.EmptyFragment
import com.xiaoyv.bangumi.ui.feature.search.detail.page.index.SearchIndexFragment
import com.xiaoyv.bangumi.ui.feature.search.detail.page.media.SearchMediaFragment
import com.xiaoyv.bangumi.ui.feature.search.detail.page.tag.SearchTagFragment
import com.xiaoyv.bangumi.ui.feature.search.detail.page.topic.SearchTopicFragment
import com.xiaoyv.common.config.annotation.SearchType
import com.xiaoyv.common.config.bean.SearchTab

/**
 * Class: [SearchDetailAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class SearchDetailAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    internal val tabs = listOf(
        SearchTab("条目", SearchType.TYPE_MEDIA),
        SearchTab("人物", SearchType.TYPE_MONO),
        SearchTab("标签", SearchType.TYPE_TAG),
        SearchTab("目录", SearchType.TYPE_INDEX),
        SearchTab("话题", SearchType.TYPE_TOPIC),
    )

    override fun createFragment(position: Int): Fragment {
        val profileTab = tabs[position]
        val type = profileTab.type

        return when (type) {
            SearchType.TYPE_MEDIA -> SearchMediaFragment.newInstance(true)
            SearchType.TYPE_MONO -> SearchMediaFragment.newInstance(false)
            SearchType.TYPE_TAG -> SearchTagFragment.newInstance()
            SearchType.TYPE_INDEX -> SearchIndexFragment.newInstance()
            SearchType.TYPE_TOPIC -> SearchTopicFragment.newInstance()
            else -> EmptyFragment.newInstance()
        }
    }

    override fun getItemCount(): Int {
        return tabs.size
    }
}