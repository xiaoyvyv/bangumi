package com.xiaoyv.bangumi.ui.feature.schedule.page

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseDifferAdapter
import com.chad.library.adapter.base.layoutmanager.QuickGridLayoutManager
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListFragment
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.response.api.ApiCalendarEntity
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.kts.dpi
import com.xiaoyv.widget.kts.getParcelObj

/**
 * Class: [SchedulePageFragment]
 *
 * @author why
 * @since 3/20/24
 */
class SchedulePageFragment :
    BaseListFragment<ApiCalendarEntity.MediaItem, SchedulePageViewModel>() {

    override val isOnlyOnePage: Boolean
        get() = true

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.scheduleData = arguments.getParcelObj(NavKey.KEY_PARCELABLE)
    }

    override fun initView() {
        super.initView()
        binding.rvContent.updatePadding(8.dpi, 8.dpi, 8.dpi, 16.dpi)
    }

    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_card) {
            RouteHelper.jumpMediaDetail(it.id)
        }
    }

    override fun onCreateContentAdapter(): BaseDifferAdapter<ApiCalendarEntity.MediaItem, *> {
        return SchedulePageAdapter()
    }

    override fun onCreateLayoutManager(): LinearLayoutManager {
        return QuickGridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
    }

    companion object {
        fun newInstance(calendarItem: ApiCalendarEntity.CalendarItem): SchedulePageFragment {
            return SchedulePageFragment().apply {
                arguments = bundleOf(NavKey.KEY_PARCELABLE to calendarItem)
            }
        }
    }
}