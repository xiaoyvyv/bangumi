package com.xiaoyv.common.helper.callback

import com.google.android.material.tabs.TabLayout

/**
 * Class: [SimpleTabSelectedListener]
 *
 * @author why
 * @since 12/27/23
 */
interface SimpleTabSelectedListener : TabLayout.OnTabSelectedListener {
    override fun onTabSelected(p0: TabLayout.Tab?) {
    }

    override fun onTabUnselected(p0: TabLayout.Tab?) {
    }

    override fun onTabReselected(p0: TabLayout.Tab?) {
    }
}
