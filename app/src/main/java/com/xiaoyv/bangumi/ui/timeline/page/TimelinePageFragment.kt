package com.xiaoyv.bangumi.ui.timeline.page

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentTimelinePageBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.TimelineEntity
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.TimelineAdapterType
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

    private val contentAdapter by lazy {
        TimelinePageAdapter {
            if (it is TimelineEntity.GridTimeline && it.id.isNotBlank()) {
                when (it.pathType) {
                    // 跳转媒体详情
                    BgmPathType.TYPE_SUBJECT -> {
                        RouteHelper.jumpMediaDetail(it.id)
                    }
                    // 跳转小组详情
                    BgmPathType.TYPE_GROUP -> {
                        RouteHelper.jumpGroupDetail(it.id)
                    }
                    // 跳转人物详情
                    BgmPathType.TYPE_CHARACTER -> {
                        RouteHelper.jumpPerson(it.id, true)
                    }
                    // 跳转人物详情
                    BgmPathType.TYPE_PERSON -> {
                        RouteHelper.jumpPerson(it.id, false)
                    }
                    // 跳转用户详情
                    BgmPathType.TYPE_USER -> {
                        RouteHelper.jumpUserDetail(it.id)
                    }
                }
            }
        }
    }

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

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_timeline) {
            when (it.adapterType) {
                TimelineAdapterType.TYPE_MEDIA -> {
                    // 跳转媒体详情
                    RouteHelper.jumpMediaDetail(it.mediaCard.id)
                }

                TimelineAdapterType.TYPE_GRID -> {
                    it.gridCard.firstOrNull()
                }

                TimelineAdapterType.TYPE_TEXT -> {
                    if (it.titleId.isNotBlank()) when (it.titleType) {
                        // 跳转用户详情
                        BgmPathType.TYPE_USER -> {
                            RouteHelper.jumpIndexDetail(it.id)
                        }
                        // 跳转日志详情
                        BgmPathType.TYPE_BLOG -> {
                            RouteHelper.jumpBlogDetail(it.titleId)
                        }
                        // 跳转目录详情
                        BgmPathType.TYPE_INDEX -> {
                            RouteHelper.jumpIndexDetail(it.titleId)
                        }
                    }
                }
            }
        }

        contentAdapter.setOnDebouncedChildClickListener(R.id.iv_avatar) {
            RouteHelper.jumpUserDetail(it.id)
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onTimelineLiveData.observe(this) {
            contentAdapter.submitList(it.orEmpty())
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