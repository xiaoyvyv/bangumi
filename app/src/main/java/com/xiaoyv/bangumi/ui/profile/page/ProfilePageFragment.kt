package com.xiaoyv.bangumi.ui.profile.page

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.xiaoyv.bangumi.databinding.FragmentTimelinePageBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.config.bean.ProfileTab
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.widget.kts.getAttrColor
import com.xiaoyv.widget.kts.getParcelObj

/**
 * Class: [ProfilePageFragment]
 *
 * @author why
 * @since 11/24/23
 */
class ProfilePageFragment :
    BaseViewModelFragment<FragmentTimelinePageBinding, ProfilePageViewModel>() {

    private val contentAdapter by lazy { ProfilePageAdapter() }

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.rprofileTab = arguments.getParcelObj(NavKey.KEY_PARCELABLE)
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
        fun newInstance(profileTab: ProfileTab): ProfilePageFragment {
            return ProfilePageFragment().apply {
                arguments = bundleOf(NavKey.KEY_PARCELABLE to profileTab)
            }
        }
    }
}