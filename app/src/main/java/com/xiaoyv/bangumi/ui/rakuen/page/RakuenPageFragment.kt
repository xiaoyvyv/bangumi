@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.ui.rakuen.page

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentTimelinePageBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.config.bean.SuperTopicTab
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.kts.getAttrColor
import com.xiaoyv.widget.kts.getParcelObj

/**
 * Class: [RakuenPageFragment]
 *
 * @author why
 * @since 11/24/23
 */
class RakuenPageFragment :
    BaseViewModelFragment<FragmentTimelinePageBinding, RakuenPageViewModel>() {

    private val contentAdapter by lazy { RakuenPageAdapter() }

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.topicTab = arguments.getParcelObj(NavKey.KEY_PARCELABLE)
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

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_super) {
            val titleLink = it.titleLink
            val targetId = titleLink.substringAfterLast("/")

            when {
                titleLink.contains("/blog/") -> {
                    RouteHelper.jumpBlogDetail(targetId)
                }

                titleLink.contains("/topic/") -> {
                    val topicType = "/topic/(.*?)/".toRegex()
                        .find(titleLink)?.groupValues?.getOrNull(1)
                        .orEmpty()

                    if (topicType.isNotBlank()) {
                        RouteHelper.jumpTopicDetail(targetId, topicType)
                    }
                }
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
    }

    companion object {
        fun newInstance(topicTab: SuperTopicTab): RakuenPageFragment {
            return RakuenPageFragment().apply {
                arguments = bundleOf(NavKey.KEY_PARCELABLE to topicTab)
            }
        }
    }
}