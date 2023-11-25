package com.xiaoyv.bangumi.ui.timeline

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xiaoyv.bangumi.ui.timeline.page.TimelinePageFragment
import com.xiaoyv.common.config.annotation.TimelineType
import com.xiaoyv.common.config.bean.TimelineTab

/**
 * Class: [TimelineAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class TimelineAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    internal val bottomTabs = listOf(
        TimelineTab("动态", TimelineType.TYPE_ALL),
        TimelineTab("吐槽", TimelineType.TYPE_SAY),
        TimelineTab("收藏", TimelineType.TYPE_SUBJECT),
        TimelineTab("进度", TimelineType.TYPE_PROGRESS),
        TimelineTab("日志", TimelineType.TYPE_BLOG),
        TimelineTab("人物", TimelineType.TYPE_MONO),
        TimelineTab("好友", TimelineType.TYPE_RELATION),
        TimelineTab("小组", TimelineType.TYPE_GROUP),
        TimelineTab("维基", TimelineType.TYPE_WIKI),
        TimelineTab("目录", TimelineType.TYPE_INDEX),
        TimelineTab("天窗", TimelineType.TYPE_WINDOW)
    )

    override fun createFragment(position: Int): Fragment {
        return TimelinePageFragment.newInstance(bottomTabs[position])
    }

    override fun getItemCount(): Int {
        return bottomTabs.size
    }
}