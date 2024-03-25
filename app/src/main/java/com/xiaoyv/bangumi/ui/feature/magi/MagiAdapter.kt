package com.xiaoyv.bangumi.ui.feature.magi

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xiaoyv.bangumi.ui.feature.empty.EmptyFragment
import com.xiaoyv.bangumi.ui.feature.magi.history.MagiHistoryFragment
import com.xiaoyv.bangumi.ui.feature.magi.question.MagiQuestionFragment
import com.xiaoyv.bangumi.ui.feature.magi.rank.MagiRankFragment
import com.xiaoyv.common.config.annotation.MagiTabType
import com.xiaoyv.common.config.bean.tab.MagiTab

/**
 * Class: [MagiAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class MagiAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    internal val tabs = listOf(
        MagiTab("中央教条", MagiTabType.TYPE_QUESTION),
        MagiTab("同步率", MagiTabType.TYPE_RANK),
        MagiTab("LCL 之海", MagiTabType.TYPE_HISTORY)
    )

    override fun createFragment(position: Int): Fragment {
        val tab = tabs[position]
        val type = tab.type
        return when (type) {
            MagiTabType.TYPE_QUESTION -> MagiQuestionFragment.newInstance()
            MagiTabType.TYPE_RANK -> MagiRankFragment.newInstance()
            MagiTabType.TYPE_HISTORY -> MagiHistoryFragment.newInstance()
            else -> EmptyFragment.newInstance()
        }
    }

    override fun getItemCount(): Int {
        return tabs.size
    }
}