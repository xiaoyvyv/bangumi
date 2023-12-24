package com.xiaoyv.bangumi.ui.process

import androidx.lifecycle.LifecycleOwner
import com.google.android.material.tabs.TabLayoutMediator
import com.xiaoyv.bangumi.databinding.FragmentProcessBinding
import com.xiaoyv.bangumi.ui.discover.container.FragmentContainerActivity
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.initNavBack

/**
 * Class: [ProcessFragment]
 *
 * @author why
 * @since 12/24/23
 */
class ProcessFragment : BaseViewModelFragment<FragmentProcessBinding, ProcessViewModel>() {
    private val vpAdapter by lazy {
        ProcessAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
    }

    private val tabLayoutMediator by lazy {
        TabLayoutMediator(binding.tabLayout, binding.vp2) { tab, position ->
            tab.text = vpAdapter.tabs[position].title
        }
    }

    override fun initView() {
        binding.vp2.adapter = vpAdapter
        binding.vp2.offscreenPageLimit = 5

        tabLayoutMediator.attach()

        refreshToolbarTitle()

        // 嵌套在 FragmentContainerActivity 内
        val activity = requireActivity()
        if (activity is FragmentContainerActivity) {
            binding.toolbar.initNavBack(activity, true)
        }
    }

    override fun initData() {

    }

    override fun LifecycleOwner.initViewObserver() {
        UserHelper.observeAction(this) {
            if (it == BgmPathType.TYPE_TIMELINE) {
                refreshToolbarTitle()
            }
        }
    }

    internal fun setVpEnable(enable: Boolean) {
        binding.vp2.isUserInputEnabled = enable
    }

    private fun refreshToolbarTitle() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabLayoutMediator.detach()
    }

    companion object {
        fun newInstance(): ProcessFragment {
            return ProcessFragment()
        }
    }
}