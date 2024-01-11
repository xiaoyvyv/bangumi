@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.ui.rakuen.page

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.util.addOnDebouncedChildClick
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentTimelinePageBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.bangumi.ui.rakuen.RakuenFragment
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.ReportType
import com.xiaoyv.common.config.annotation.TopicType
import com.xiaoyv.common.config.bean.SuperTopicTab
import com.xiaoyv.common.helper.showActionMenu
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.dialog.UiDialog
import com.xiaoyv.widget.kts.getAttrColor
import com.xiaoyv.widget.kts.getParcelObj
import com.xiaoyv.widget.kts.useNotNull

/**
 * Class: [RakuenPageFragment]
 *
 * @author why
 * @since 11/24/23
 */
class RakuenPageFragment :
    BaseViewModelFragment<FragmentTimelinePageBinding, RakuenPageViewModel>() {

    private val contentAdapter by lazy { RakuenPageAdapter() }

    private val rakuenFragment
        get() = parentFragment as? RakuenFragment

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.topicTab = arguments.getParcelObj(NavKey.KEY_PARCELABLE)
    }

    override fun initView() {
        binding.srlRefresh.initRefresh { true }
        binding.srlRefresh.setColorSchemeColors(hostActivity.getAttrColor(GoogleAttr.colorPrimary))
    }

    override fun initData() {
        binding.rvContent.adapter = contentAdapter

        // 懒加载
        // 小组条目初始化监听父级的 rakuenGroupType 会自动刷新，这里非小组类型初始化时才加载
        if (viewModel.isGroupType.not()) {
            viewModel.queryTimeline()
        }
    }

    override fun initListener() {
        binding.srlRefresh.setOnRefreshListener {
            viewModel.queryTimeline()
        }

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_super) {
            when (it.pathType) {
                BgmPathType.TYPE_BLOG -> {
                    RouteHelper.jumpBlogDetail(it.id)
                }

                BgmPathType.TYPE_TOPIC -> {
                    if (it.topicType.isNotBlank()) {
                        RouteHelper.jumpTopicDetail(it.id, it.topicType)
                    }
                }
            }
        }

        contentAdapter.setOnDebouncedChildClickListener(R.id.iv_avatar) {
            when (it.pathType) {
                BgmPathType.TYPE_BLOG -> {
                    RouteHelper.jumpUserDetail(it.avatarId)
                }

                BgmPathType.TYPE_TOPIC -> when (it.topicType) {
                    TopicType.TYPE_EP,
                    TopicType.TYPE_SUBJECT,
                    -> {
                        RouteHelper.jumpMediaDetail(it.attachId)
                    }

                    TopicType.TYPE_GROUP -> {
                        RouteHelper.jumpGroupDetail(it.attachId)
                    }

                    TopicType.TYPE_PERSON,
                    TopicType.TYPE_CRT,
                    -> {
                        RouteHelper.jumpTopicDetail(it.id, it.topicType)
                    }
                }
            }
        }

        // 屏蔽用户相关菜单
        contentAdapter.addOnDebouncedChildClick(R.id.iv_action) { _, view, position ->
            useNotNull(contentAdapter.getItem(position)) {
                requireActivity().showActionMenu(
                    view,
                    avatarId,
                    ReportType.TYPE_USER,
                    viewModel.loadingDialogState(cancelable = false)
                )
            }
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onSuperTopicLiveData.observe(this) {
            contentAdapter.submitList(it.orEmpty()) {
                val layoutManager = binding.rvContent.layoutManager as? LinearLayoutManager
                layoutManager?.scrollToPositionWithOffset(0, 0)
            }
        }

        // 小组类型切换
        if (viewModel.isGroupType) {
            rakuenFragment?.viewModel?.rakuenGroupType?.observe(this) {
                binding.srlRefresh.isRefreshing = true

                viewModel.topicTab?.type = it
                viewModel.queryTimeline()
            }
        }
    }


    override fun createLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(requireContext())
    }


    companion object {
        fun newInstance(topicTab: SuperTopicTab): RakuenPageFragment {
            return RakuenPageFragment().apply {
                arguments = bundleOf(NavKey.KEY_PARCELABLE to topicTab)
            }
        }
    }
}