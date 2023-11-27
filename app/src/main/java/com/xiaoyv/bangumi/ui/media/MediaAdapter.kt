package com.xiaoyv.bangumi.ui.media

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xiaoyv.bangumi.ui.media.type.MediaPageFragment
import com.xiaoyv.common.config.GlobalConfig
import com.xiaoyv.common.config.annotation.MediaType

/**
 * Class: [MediaAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class MediaAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    internal val bottomTabs
        get() = GlobalConfig.mediaTypes

    fun getCurrentMediaType(currentItem: Int): String {
        return bottomTabs[currentItem].type
    }

    fun getCurrentMediaTypeName(currentItem: Int): String {
        return bottomTabs[currentItem].title
    }

    fun getMediaTypeName(@MediaType type: String): String {
        return bottomTabs.find { it.type == type }?.title.orEmpty()
    }

    override fun createFragment(position: Int): Fragment {
        return MediaPageFragment.newInstance(bottomTabs[position])
    }

    override fun getItemCount(): Int {
        return bottomTabs.size
    }
}