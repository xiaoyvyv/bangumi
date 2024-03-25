package com.xiaoyv.bangumi.ui.timeline

import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import androidx.core.view.updatePadding
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.BarUtils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.xiaoyv.bangumi.databinding.FragmentTimelineBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.TimelinePageType
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.helper.callback.SimpleTabSelectedListener
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.showInputDialog
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.kts.adjustScrollSensitivity
import com.xiaoyv.widget.kts.orEmpty
import kotlinx.coroutines.delay

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
        binding.vp2.adjustScrollSensitivity(ConfigHelper.vpTouchSlop.toFloat())
        binding.vp2.offscreenPageLimit = 5
        binding.vp2.adapter = vpAdapter

        tabLayoutMediator.attach()

        refreshToolbarTitle()
    }

    override fun initData() {

    }

    override fun initListener() {
        viewModel.defaultTab.observe(this) {
            if (binding.vp2.currentItem != it) {
                binding.vp2.setCurrentItem(it.orEmpty(), true)
            }
        }

        binding.tabLayout.addOnTabSelectedListener(object : SimpleTabSelectedListener {
            override fun onTabSelected(p0: TabLayout.Tab?) {
                viewModel.defaultTab.value = p0?.position ?: return
            }
        })

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
                    ConfigHelper.timelinePageType = TimelinePageType.TYPE_ALL
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
                    ConfigHelper.timelinePageType = TimelinePageType.TYPE_FRIEND
                    UserHelper.notifyActionChange(BgmPathType.TYPE_TIMELINE)
                    true
                }

            // 自己的时间线
            add(getString(CommonString.timeline_mine_title))
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
                .setOnMenuItemClickListener {
                    if (UserHelper.isLogin.not()) {
                        RouteHelper.jumpLogin()
                        return@setOnMenuItemClickListener true
                    }
                    ConfigHelper.timelinePageType = TimelinePageType.TYPE_MINE
                    UserHelper.notifyActionChange(BgmPathType.TYPE_TIMELINE)
                    true
                }

        }

        binding.fabComment.setOnFastLimitClickListener {
            requireActivity().showInputDialog(
                title = "吐槽",
                inputHint = "吐个槽",
                maxInput = 380,
                minLines = 4,
                onInput = {
                    if (it.isNotBlank()) {
                        viewModel.addTimelineComment(it)
                    }
                }
            )
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        UserHelper.observeAction(this) {
            if (it == BgmPathType.TYPE_TIMELINE) {
                refreshToolbarTitle()
            }
        }

        UserHelper.observeUserInfo(this) {
            binding.fabComment.isVisible = it.isEmpty.not()
        }
    }

    private fun refreshToolbarTitle() {
        binding.toolbar.title = when (ConfigHelper.timelinePageType) {
            1 -> getString(CommonString.timeline_user_title)
            2 -> getString(CommonString.timeline_mine_title)
            else -> getString(CommonString.timeline_title)
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