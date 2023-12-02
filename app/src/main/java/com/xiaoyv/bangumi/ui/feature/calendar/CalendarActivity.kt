package com.xiaoyv.bangumi.ui.feature.calendar

import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.ActivityCalendarBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.api.response.CalendarEntity
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.kts.getAttrColor
import com.xiaoyv.widget.kts.orEmpty
import com.xiaoyv.widget.kts.useNotNull

/**
 * Class: [CalendarActivity]
 *
 * @author why
 * @since 11/24/23
 */
class CalendarActivity : BaseViewModelActivity<ActivityCalendarBinding, CalendarViewModel>() {

    private val calendarAdapter by lazy {
        CalendarAdapter()
    }

    override fun initView() {
        binding.toolbar.initNavBack(this)

        binding.srlRefresh.initRefresh()
        binding.srlRefresh.setColorSchemeColors(getAttrColor(GoogleAttr.colorPrimary))

        binding.srlRefresh.isRefreshing = true
    }

    override fun initData() {
        binding.rvContent.adapter = calendarAdapter
    }

    override fun initListener() {
        binding.srlRefresh.setOnRefreshListener {
            viewModel.queryCalendar()
        }

        calendarAdapter.setOnDebouncedChildClickListener(R.id.item_card) {
            useNotNull(it as? CalendarEntity.CalendarEntityItem.Item) {
                RouteHelper.jumpMediaDetail(id.toString())
            }
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onCalendarLiveData.observe(this) {
            val contentList = it.orEmpty().flatMap { item ->
                val weekday = item.weekday ?: CalendarEntity.CalendarEntityItem.Weekday()
                val items = item.items.orEmpty()
                val subItems = arrayListOf<Any>()
                subItems.add(weekday)
                subItems.addAll(items)
                subItems
            }
            debugLog { contentList.toJson(true) }
            calendarAdapter.submitList(contentList)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}