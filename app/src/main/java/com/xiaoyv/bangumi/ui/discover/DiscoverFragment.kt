package com.xiaoyv.bangumi.ui.discover

import androidx.lifecycle.LifecycleOwner
import com.google.android.material.tabs.TabLayoutMediator
import com.xiaoyv.bangumi.databinding.FragmentDiscoverBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.debugLog

/**
 * Class: [DiscoverFragment]
 *
 * @author why
 * @since 11/24/23
 */
class DiscoverFragment : BaseViewModelFragment<FragmentDiscoverBinding, DiscoverViewModel>() {

    private val vpAdapter by lazy {
        DiscoverAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
    }

    private val tabLayoutMediator by lazy {
        TabLayoutMediator(binding.tableLayout, binding.vpContent) { tab, position ->
            tab.text = vpAdapter.tabs[position].title
        }
    }

    override fun initView() {

    }

    override fun initData() {
        binding.vpContent.adapter = vpAdapter
//        binding.vpContent.offscreenPageLimit = 5

        tabLayoutMediator.attach()
    }

    override fun initListener() {

    }


    override fun LifecycleOwner.initViewObserver() {
        UserHelper.observe(this) {
            if (!it.isEmpty) {

            } else {
                debugLog { "未登录！" }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabLayoutMediator.detach()
    }

    companion object {
        fun newInstance(): DiscoverFragment {
            return DiscoverFragment()
        }
    }
}