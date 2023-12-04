package com.xiaoyv.bangumi.ui.timeline.page

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentTimelinePageBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.config.annotation.TimelineType
import com.xiaoyv.common.config.bean.TimelineTab
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.kts.getAttrColor
import com.xiaoyv.widget.kts.getParcelObj

/**
 * Class: [TimelinePageFragment]
 *
 * @author why
 * @since 11/24/23
 */
class TimelinePageFragment :
    BaseViewModelFragment<FragmentTimelinePageBinding, TimelinePageViewModel>() {

    private val contentAdapter by lazy { TimelinePageAdapter() }

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.timelineTab = arguments.getParcelObj(NavKey.KEY_PARCELABLE)
    }

    override fun initView() {
        binding.srlRefresh.initRefresh()
        binding.srlRefresh.setColorSchemeColors(hostActivity.getAttrColor(GoogleAttr.colorPrimary))
    }

    override fun initData() {
        binding.rvContent.adapter = contentAdapter
        binding.srlRefresh.isRefreshing = true

        viewModel.queryTimeline()
    }

    override fun initListener() {
        binding.srlRefresh.setOnRefreshListener {
            viewModel.queryTimeline()
        }

        contentAdapter.setOnDebouncedChildClickListener(R.id.iv_avatar) {
            RouteHelper.jumpUserDetail(it.userId)
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onTimelineLiveData.observe(this) {
            contentAdapter.submitList(it.orEmpty()) {
                val layoutManager = binding.rvContent.layoutManager as? LinearLayoutManager
                layoutManager?.scrollToPositionWithOffset(0, 0)
            }
        }
    }

    companion object {
        fun newInstance(timelineTab: TimelineTab): TimelinePageFragment {
            return TimelinePageFragment().apply {
                arguments = bundleOf(NavKey.KEY_PARCELABLE to timelineTab)
            }
        }

        /**
         * 指定人物的时间胶囊
         */
        fun newInstance(@TimelineType type: String, userId: String): TimelinePageFragment {
            return TimelinePageFragment().apply {
                val timeline = TimelineTab(
                    title = "User:$userId",
                    timelineType = type,
                    userId = userId
                )
                arguments = bundleOf(NavKey.KEY_PARCELABLE to timeline)
            }
        }
    }
}