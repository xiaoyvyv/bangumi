package com.xiaoyv.bangumi.ui.feature.schedule

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xiaoyv.bangumi.ui.feature.schedule.page.SchedulePageFragment
import com.xiaoyv.common.api.response.api.ApiCalendarEntity

/**
 * Class: [ScheduleAdapter]
 *
 * @author why
 * @since 11/25/23
 */
class ScheduleAdapter(fragmentActivity: FragmentActivity, val data: ApiCalendarEntity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return SchedulePageFragment.newInstance(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}