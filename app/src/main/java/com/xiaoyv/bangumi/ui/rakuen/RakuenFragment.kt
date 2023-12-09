@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.ui.rakuen

import android.view.MenuItem
import androidx.core.view.ViewCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.xiaoyv.bangumi.databinding.FragmentSuperBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.CommonString
import kotlinx.coroutines.delay

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

    override fun initListener() {
        binding.toolbar.menu.apply {
            add(getString(CommonString.common_search))
                .setIcon(CommonDrawable.ic_search)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                .setOnMenuItemClickListener {
                    RouteHelper.jumpSearch()
                    true
                }
        }
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