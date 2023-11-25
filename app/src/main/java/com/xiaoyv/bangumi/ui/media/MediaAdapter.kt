package com.xiaoyv.bangumi.ui.media

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xiaoyv.bangumi.ui.media.type.MediaPageFragment
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.config.bean.MediaTab

/**
 * Class: [MediaAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class MediaAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    internal val bottomTabs = listOf(
        MediaTab("动漫", MediaType.TYPE_ANIME),
        MediaTab("书籍", MediaType.TYPE_BOOK),
        MediaTab("音乐", MediaType.TYPE_MUSIC),
        MediaTab("游戏", MediaType.TYPE_GAME),
        MediaTab("三次元", MediaType.TYPE_REAL),
    )

    override fun createFragment(position: Int): Fragment {
        return MediaPageFragment.newInstance(bottomTabs[position])
    }

    override fun getItemCount(): Int {
        return bottomTabs.size
    }
}