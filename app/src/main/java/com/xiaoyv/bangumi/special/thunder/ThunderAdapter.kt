package com.xiaoyv.bangumi.special.thunder

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xiaoyv.bangumi.special.thunder.page.ThunderTaskFragment
import com.xiaoyv.common.config.annotation.ThunderTabType
import com.xiaoyv.common.config.bean.tab.ThunderTab
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.i18n

/**
 * Class: [ThunderAdapter]
 *
 * @author why
 * @since 11/25/23
 */
class ThunderAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    internal val tabs = listOf(
        ThunderTab(i18n(CommonString.download_running), ThunderTabType.TYPE_DOWNLOADING),
        ThunderTab(i18n(CommonString.download_finish), ThunderTabType.TYPE_DOWNLOADED),
    )

    override fun createFragment(position: Int): Fragment {
        return ThunderTaskFragment.newInstance(tabs[position])
    }

    override fun getItemCount(): Int {
        return tabs.size
    }
}