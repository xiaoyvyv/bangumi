package com.xiaoyv.bangumi.ui.media.detail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xiaoyv.bangumi.ui.discover.blog.BlogFragment
import com.xiaoyv.bangumi.ui.discover.character.CharacterFragment
import com.xiaoyv.bangumi.ui.discover.friend.FriendFragment
import com.xiaoyv.bangumi.ui.discover.group.GroupFragment
import com.xiaoyv.bangumi.ui.discover.index.IndexFragment
import com.xiaoyv.bangumi.ui.discover.wiki.WikiFragment
import com.xiaoyv.bangumi.ui.feature.empty.EmptyFragment
import com.xiaoyv.bangumi.ui.media.detail.overview.OverviewFragment
import com.xiaoyv.bangumi.ui.media.detail.state.MediaStateFragment
import com.xiaoyv.common.config.annotation.MediaDetailType
import com.xiaoyv.common.config.bean.MediaDetailTab

/**
 * Class: [MediaDetailAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class MediaDetailAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    internal val tabs = listOf(
        MediaDetailTab("概览", MediaDetailType.TYPE_OVERVIEW),
        MediaDetailTab("章节", MediaDetailType.TYPE_CHAPTER),
        MediaDetailTab("角色", MediaDetailType.TYPE_CHARACTER),
        MediaDetailTab("制作人员", MediaDetailType.TYPE_MAKER),
        MediaDetailTab("吐槽", MediaDetailType.TYPE_COMMENTS),
        MediaDetailTab("评论", MediaDetailType.TYPE_REVIEW),
        MediaDetailTab("讨论版", MediaDetailType.TYPE_BOARD),
        MediaDetailTab("透视", MediaDetailType.TYPE_STATS)
    )

    override fun createFragment(position: Int): Fragment {
        val discoverTab = tabs[position]
        val type = discoverTab.type
        return when (type) {
            MediaDetailType.TYPE_OVERVIEW -> OverviewFragment.newInstance()
            MediaDetailType.TYPE_CHAPTER -> CharacterFragment.newInstance()
            MediaDetailType.TYPE_CHARACTER -> BlogFragment.newInstance()
            MediaDetailType.TYPE_MAKER -> IndexFragment.newInstance()
            MediaDetailType.TYPE_COMMENTS -> GroupFragment.newInstance()
            MediaDetailType.TYPE_REVIEW -> FriendFragment.newInstance()
            MediaDetailType.TYPE_BOARD -> WikiFragment.newInstance()
            MediaDetailType.TYPE_STATS -> MediaStateFragment.newInstance()
            else -> EmptyFragment.newInstance()
        }
    }

    override fun getItemCount(): Int {
        return tabs.size
    }
}