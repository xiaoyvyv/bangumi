package com.xiaoyv.bangumi.ui.media.detail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xiaoyv.bangumi.ui.feature.empty.EmptyFragment
import com.xiaoyv.bangumi.ui.media.detail.board.MediaBoardFragment
import com.xiaoyv.bangumi.ui.media.detail.chapter.MediaChapterFragment
import com.xiaoyv.bangumi.ui.media.detail.character.MediaCharacterFragment
import com.xiaoyv.bangumi.ui.media.detail.chart.MediaChartFragment
import com.xiaoyv.bangumi.ui.media.detail.comments.MediaCommentFragment
import com.xiaoyv.bangumi.ui.media.detail.maker.MediaMakerFragment
import com.xiaoyv.bangumi.ui.media.detail.overview.OverviewFragment
import com.xiaoyv.bangumi.ui.media.detail.review.MediaReviewFragment
import com.xiaoyv.common.config.annotation.MediaDetailType
import com.xiaoyv.common.config.bean.tab.MediaDetailTab

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
        MediaDetailTab("吐槽", MediaDetailType.TYPE_RATING),
        MediaDetailTab("日志", MediaDetailType.TYPE_BLOG),
        MediaDetailTab("讨论版", MediaDetailType.TYPE_TOPIC),
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
            MediaDetailType.TYPE_RATING -> MediaCommentFragment.newInstance(mediaId)
            MediaDetailType.TYPE_BLOG -> MediaReviewFragment.newInstance(mediaId)
            MediaDetailType.TYPE_TOPIC -> MediaBoardFragment.newInstance(mediaId)
            MediaDetailType.TYPE_STATS -> MediaChartFragment.newInstance(mediaId)
            else -> EmptyFragment.newInstance()
        }
    }

    override fun getItemCount(): Int {
        return tabs.size
    }
}