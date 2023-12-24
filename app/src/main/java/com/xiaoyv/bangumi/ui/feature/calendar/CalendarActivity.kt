package com.xiaoyv.bangumi.ui.feature.calendar

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.ActivityCalendarBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.response.api.ApiCalendarEntity
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.currentWeekDay
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.kts.getAttrColor
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

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.isShowToday = bundle.getBoolean(NavKey.KEY_BOOLEAN)
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
            useNotNull(it as? ApiCalendarEntity.CalendarEntityItem.Item) {
                RouteHelper.jumpMediaDetail(id.toString())
            }
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onCalendarLiveData.observe(this) {
            val contentList = it.orEmpty().flatMap { item ->
                val weekday = item.weekday ?: ApiCalendarEntity.CalendarEntityItem.Weekday()
                val items = item.items.orEmpty()
                val subItems = arrayListOf<Any>()
                subItems.add(weekday)
                subItems.addAll(items)
                subItems
            }
            calendarAdapter.submitList(contentList)

            // 滚动
            scrollToTarget()
        }
    }

    /**
     * 滚动到当日放送位置
     */
    private fun scrollToTarget() {
        val layoutManager = binding.rvContent.layoutManager as LinearLayoutManager

        // 滑到今天
        if (viewModel.isShowToday) {
            var weekDay = currentWeekDay - 1
            if (weekDay == 0) weekDay = 7
            val targetIndex = calendarAdapter.items.indexOfFirst {
                it is ApiCalendarEntity.CalendarEntityItem.Weekday && it.id == weekDay
            }
            if (targetIndex != -1) {
                layoutManager.scrollToPositionWithOffset(targetIndex, 0)
            }
        }
        // 滑到明天
        else {
            val weekDay = currentWeekDay
            val targetIndex = calendarAdapter.items.indexOfFirst {
                it is ApiCalendarEntity.CalendarEntityItem.Weekday && it.id == weekDay
            }
            if (targetIndex != -1) {
                layoutManager.scrollToPositionWithOffset(targetIndex, 0)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}