package com.xiaoyv.bangumi.ui.process

import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.tabs.TabLayoutMediator
import com.xiaoyv.bangumi.databinding.FragmentProcessBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.bangumi.ui.discover.container.FragmentContainerActivity
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.widget.kts.adjustScrollSensitivity
import com.xiaoyv.widget.kts.getAttrDrawable

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
        binding.vp2.offscreenPageLimit = 5
        binding.vp2.adjustScrollSensitivity(ConfigHelper.vpTouchSlop.toFloat())
        binding.vp2.adapter = vpAdapter

        tabLayoutMediator.attach()

        refreshToolbarTitle()

        // 嵌套在 FragmentContainerActivity 内
        val activity = requireActivity()
        if (activity is FragmentContainerActivity) {
            binding.toolbar.navigationIcon = activity.getAttrDrawable(GoogleAttr.homeAsUpIndicator)
            binding.toolbar.setNavigationOnClickListener {
                requireActivity().finish()
            }
        }
    }

    override fun initData() {

    }

    override fun initListener() {
        super.initListener()

        binding.toolbar.menu.add("搜索")
            .setIcon(CommonDrawable.ic_search)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            .setOnMenuItemClickListener {
                RouteHelper.jumpSearch()
                true
            }
    }

    override fun LifecycleOwner.initViewObserver() {
        UserHelper.observeAction(this) {
            if (it == BgmPathType.TYPE_TIMELINE) {
                refreshToolbarTitle()
            }
        }
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