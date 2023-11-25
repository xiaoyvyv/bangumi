package com.xiaoyv.bangumi.ui.timeline

import com.google.android.material.tabs.TabLayoutMediator
import com.xiaoyv.bangumi.databinding.FragmentTimelineBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment

/**
 * Class: [TimelineFragment]
 *
 * @author why
 * @since 11/24/23
 */
class TimelineFragment : BaseViewModelFragment<FragmentTimelineBinding, TimelineViewModel>() {

    private val vpAdapter by lazy {
        TimelineAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
    }

    private val tabLayoutMediator by lazy {
        TabLayoutMediator(binding.tabLayout, binding.vp2) { tab, position ->
            tab.text = vpAdapter.bottomTabs[position].title
        }
    }

    override fun initView() {
        binding.vp2.adapter = vpAdapter
        binding.vp2.offscreenPageLimit = 5

        tabLayoutMediator.attach()
    }

    override fun initData() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabLayoutMediator.detach()
    }

    companion object {
        fun newInstance(): TimelineFragment {
            return TimelineFragment()
        }
    }
}