package com.xiaoyv.bangumi.ui.timeline

import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.tabs.TabLayoutMediator
import com.xiaoyv.bangumi.databinding.FragmentTimelineBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.CommonString

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

        refreshToolbarTitle()
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

            // 全部时间线
            add(getString(CommonString.timeline_title))
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
                .setOnMenuItemClickListener {
                    ConfigHelper.isShowUserTimeline = false
                    UserHelper.notifyActionChange(BgmPathType.TYPE_TIMELINE)
                    true
                }

            // 好友时间线
            add(getString(CommonString.timeline_user_title))
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
                .setOnMenuItemClickListener {
                    if (UserHelper.isLogin.not()) {
                        RouteHelper.jumpLogin()
                        return@setOnMenuItemClickListener true
                    }
                    ConfigHelper.isShowUserTimeline = true
                    UserHelper.notifyActionChange(BgmPathType.TYPE_TIMELINE)
                    true
                }
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
        binding.toolbar.title = if (ConfigHelper.isShowUserTimeline) {
            getString(CommonString.timeline_user_title)
        } else {
            getString(CommonString.timeline_title)
        }
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