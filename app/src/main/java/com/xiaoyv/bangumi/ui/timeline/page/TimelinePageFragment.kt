package com.xiaoyv.bangumi.ui.timeline.page

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.BaseDifferAdapter
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListFragment
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.TimelineEntity
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.TimelineAdapterType
import com.xiaoyv.common.config.annotation.TimelineType
import com.xiaoyv.common.config.bean.TimelineTab
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.widget.kts.getParcelObj

/**
 * Class: [TimelinePageFragment]
 *
 * @author why
 * @since 11/24/23
 */
class TimelinePageFragment : BaseListFragment<TimelineEntity, TimelinePageViewModel>() {

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.timelineTab = arguments.getParcelObj(NavKey.KEY_PARCELABLE)
    }

    override val isOnlyOnePage: Boolean
        get() = !viewModel.hasMultiPage

    override val loadingBias: Float
        get() = if (viewModel.userId.isNotBlank()) 0.3f else super.loadingBias

    override fun onCreateContentAdapter(): BaseDifferAdapter<TimelineEntity, *> {
        return TimelinePageAdapter {
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

    override fun initListener() {
        super.initListener()

        // 文字内容部分点击
        contentAdapter.setOnDebouncedChildClickListener(R.id.tv_content) {
            if (it.adapterType == TimelineAdapterType.TYPE_MEDIA) {
                RouteHelper.jumpSummaryDetail(it.content.toString())
            } else {
                onClickTimelineItem(it)
            }
        }

        // 条目点击
        contentAdapter.setOnDebouncedChildClickListener(R.id.item_timeline) {
            onClickTimelineItem(it)
        }

        contentAdapter.setOnDebouncedChildClickListener(R.id.iv_avatar) {
            RouteHelper.jumpUserDetail(it.userId)
        }

        // 长按
        contentAdapter.addOnItemChildLongClickListener(R.id.item_timeline) { adapter, _, position ->
            val entity = adapter.getItem(position)
            val deleteId = entity?.deleteId.orEmpty()

            // 删除
            if (deleteId.isNotBlank()) {
                requireActivity().showConfirmDialog(
                    message = "是否删除该条时间线？",
                    onConfirmClick = {
                        contentAdapter.removeAt(position)
                        viewModel.deleteTimeline(deleteId)
                    }
                )
            }
            true
        }
    }

    override fun LifecycleOwner.initViewObserverExt() {
        // 时间线类型切换刷新
        UserHelper.observeAction(this) {
            if (it == BgmPathType.TYPE_TIMELINE) {
                // 刷新适配器类型
                refreshAdapter()

                // 刷新数据
                viewModel.refresh()
            }
        }
    }

    override fun autoInitData() {
        UserHelper.observeUserInfo(this) {
            super.autoInitData()
        }
    }

    private fun onClickTimelineItem(it: TimelineEntity) {
        when (it.adapterType) {
            TimelineAdapterType.TYPE_MEDIA -> {
                // 跳转媒体详情
                RouteHelper.jumpMediaDetail(it.mediaCard.id)
            }

            TimelineAdapterType.TYPE_GRID -> {
                it.gridCard.firstOrNull()
            }

            TimelineAdapterType.TYPE_TEXT -> {
                // 是否为吐槽时间线
                if (it.isSpitOut) {
                    RouteHelper.jumpTimeline(it.id)
                    return
                }

                // 跳转 Title Id 对应的页面
                if (it.titleId.isNotBlank()) when (it.titleType) {
                    // 跳转用户详情
                    BgmPathType.TYPE_USER -> {
                        RouteHelper.jumpIndexDetail(it.userId)
                    }
                    // 跳转日志详情
                    BgmPathType.TYPE_BLOG -> {
                        RouteHelper.jumpBlogDetail(it.titleId)
                    }
                    // 跳转目录详情
                    BgmPathType.TYPE_INDEX -> {
                        RouteHelper.jumpIndexDetail(it.titleId)
                    }
                    // 跳转小组详情
                    BgmPathType.TYPE_GROUP -> {
                        RouteHelper.jumpGroupDetail(it.titleId)
                    }
                }
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
                    title = "User: $userId",
                    timelineType = type,
                    userId = userId
                )
                arguments = bundleOf(NavKey.KEY_PARCELABLE to timeline)
            }
        }
    }
}