@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.ui.rakuen

import com.google.android.material.tabs.TabLayoutMediator
import com.xiaoyv.bangumi.databinding.FragmentSuperBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment

/**
 * Class: [RakuenFragment]
 *
 * @author why
 * @since 11/24/23
 */
class RakuenFragment : BaseViewModelFragment<FragmentSuperBinding, RakuenViewModel>() {
    private val vpAdapter by lazy {
        RakuenAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
    }

    private val tabLayoutMediator by lazy {
        TabLayoutMediator(binding.tabLayout, binding.vp2) { tab, position ->
            tab.text = vpAdapter.tabs[position].title
        }
    }

    override fun initView() {
        binding.vp2.adapter = vpAdapter
        binding.vp2.offscreenPageLimit = vpAdapter.itemCount

        tabLayoutMediator.attach()
    }

    override fun initData() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabLayoutMediator.detach()
    }

    companion object {
        fun newInstance(): RakuenFragment {
            return RakuenFragment()
        }
    }
}