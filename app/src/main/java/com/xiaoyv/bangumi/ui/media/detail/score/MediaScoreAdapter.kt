package com.xiaoyv.bangumi.ui.media.detail.score

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xiaoyv.bangumi.ui.media.detail.score.page.MediaScorePageFragment
import com.xiaoyv.common.config.annotation.InterestType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.config.bean.tab.MediaScoreTab

/**
 * Class: [MediaScoreAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class MediaScoreAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    @MediaType mediaType: String,
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    internal var mediaId = ""
    internal var friend = false

    internal val tabs = listOf(
        MediaScoreTab(
            InterestType.string(InterestType.TYPE_WISH, mediaType),
            InterestType.TYPE_WISH
        ),
        MediaScoreTab(
            InterestType.string(InterestType.TYPE_COLLECT, mediaType),
            InterestType.TYPE_COLLECT
        ),
        MediaScoreTab(
            InterestType.string(InterestType.TYPE_DO, mediaType),
            InterestType.TYPE_DO
        ),
        MediaScoreTab(
            InterestType.string(InterestType.TYPE_ON_HOLD, mediaType),
            InterestType.TYPE_ON_HOLD
        ),
        MediaScoreTab(
            InterestType.string(InterestType.TYPE_DROPPED, mediaType),
            InterestType.TYPE_DROPPED
        )
    )

    override fun createFragment(position: Int): Fragment {
        return MediaScorePageFragment.newInstance(mediaId, friend, tabs[position].type)
    }

    override fun getItemCount(): Int {
        return tabs.size
    }
}