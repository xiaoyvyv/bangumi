package com.xiaoyv.bangumi.ui.media.detail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xiaoyv.bangumi.ui.feature.empty.EmptyFragment
import com.xiaoyv.bangumi.ui.media.detail.board.MediaBoardFragment
import com.xiaoyv.bangumi.ui.media.detail.chapter.MediaChapterFragment
import com.xiaoyv.bangumi.ui.media.detail.character.MediaCharacterFragment
import com.xiaoyv.bangumi.ui.media.detail.comments.MediaCommentFragment
import com.xiaoyv.bangumi.ui.media.detail.maker.MediaMakerFragment
import com.xiaoyv.bangumi.ui.media.detail.overview.OverviewFragment
import com.xiaoyv.bangumi.ui.media.detail.review.MediaReviewFragment
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

    var mediaId: String = ""

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
            MediaDetailType.TYPE_OVERVIEW -> OverviewFragment.newInstance(mediaId)
            MediaDetailType.TYPE_CHAPTER -> MediaChapterFragment.newInstance(mediaId)
            MediaDetailType.TYPE_CHARACTER -> MediaCharacterFragment.newInstance(mediaId)
            MediaDetailType.TYPE_MAKER -> MediaMakerFragment.newInstance(mediaId)
            MediaDetailType.TYPE_COMMENTS -> MediaCommentFragment.newInstance(mediaId)
            MediaDetailType.TYPE_REVIEW -> MediaReviewFragment.newInstance(mediaId)
            MediaDetailType.TYPE_BOARD -> MediaBoardFragment.newInstance(mediaId)
            MediaDetailType.TYPE_STATS -> EmptyFragment.newInstance()
            else -> EmptyFragment.newInstance()
        }
    }

    override fun getItemCount(): Int {
        return tabs.size
    }
}