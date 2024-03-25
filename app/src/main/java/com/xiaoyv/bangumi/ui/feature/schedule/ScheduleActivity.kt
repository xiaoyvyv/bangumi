package com.xiaoyv.bangumi.ui.feature.schedule

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.StringUtils
import com.google.android.material.tabs.TabLayoutMediator
import com.xiaoyv.bangumi.databinding.ActivityScheduleBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.activity
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.currentWeekDay
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.widget.kts.adjustScrollSensitivity

/**
 * Class: [ScheduleActivity]
 *
 * @author why
 * @since 11/24/23
 */
class ScheduleActivity : BaseViewModelActivity<ActivityScheduleBinding, ScheduleViewModel>() {

    private val weekdays = listOf(
        "周一", "周二", "周三", "周四", "周五", "周六", "周日"
    )

    private val tabLayoutMediator by lazy {
        TabLayoutMediator(binding.tableLayout, binding.vpContent) { tab, position ->
            tab.text = weekdays.getOrNull(position)
        }
    }

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.isShowToday = bundle.getBoolean(NavKey.KEY_BOOLEAN)
    }

    override fun initView() {
        binding.toolbar.initNavBack(this)
        binding.toolbar.title = buildString {
            append(StringUtils.getString(CommonString.calendar_title))
            append("-")
            append(if (viewModel.isShowToday) "今日" else "明日")
        }
    }

    override fun initData() {
        binding.vpContent.offscreenPageLimit = weekdays.size
        binding.vpContent.adjustScrollSensitivity(ConfigHelper.vpTouchSlop.toFloat())
    }

    override fun initListener() {

    }

    override fun LifecycleOwner.initViewObserver() {
        binding.stateView.initObserver(
            lifecycleOwner = this,
            loadingViewState = viewModel.loadingViewState
        )

        viewModel.onScheduleLiveData.observe(this) {
            val entity = it ?: return@observe
            val adapter = ScheduleAdapter(activity, entity)
            binding.vpContent.adapter = adapter

            tabLayoutMediator.attach()

            scrollToTarget()
        }
    }

    /**
     * 滚动到当日放送位置
     */
    private fun scrollToTarget() {
        var weekDay = currentWeekDay - 1
        if (weekDay == 0) weekDay = 7

        // 滑到今天
        if (viewModel.isShowToday) {
            binding.vpContent.setCurrentItem(weekDay - 1, false)
        }
        // 滑到明天
        else {
            binding.vpContent.setCurrentItem(weekDay.let { if (it == 7) 0 else it }, false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        tabLayoutMediator.detach()
    }
}