package com.xiaoyv.bangumi.ui.profile

import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.tabs.TabLayoutMediator
import com.xiaoyv.bangumi.databinding.FragmentProfileBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.loadImageBlur

/**
 * Class: [ProfileFragment]
 *
 * @author why
 * @since 11/24/23
 */
class ProfileFragment : BaseViewModelFragment<FragmentProfileBinding, ProfileViewModel>() {

    private val vpAdapter by lazy {
        ProfileAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
    }

    private val tabLayoutMediator by lazy {
        TabLayoutMediator(binding.tableLayout, binding.vpContent) { tab, position ->
            tab.text = vpAdapter.tabs[position].title
        }
    }

    override fun initView() {
        binding.vpContent.adapter = vpAdapter
        binding.vpContent.offscreenPageLimit = vpAdapter.itemCount

        tabLayoutMediator.attach()
    }

    override fun initData() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabLayoutMediator.detach()
    }

    override fun initListener() {
        binding.toolbar.menu.apply {
            add("Timeline")
                .setIcon(CommonDrawable.ic_timeline)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                .setOnMenuItemClickListener {

                    true
                }

            add("Message")
                .setIcon(CommonDrawable.ic_email_normal)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                .setOnMenuItemClickListener {

                    true
                }

            add("Setting")
                .setIcon(CommonDrawable.ic_setting)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                .setOnMenuItemClickListener {

                    true
                }
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        UserHelper.observe(this) {
            if (!it.isLogout) {
                binding.ivBanner.loadImageBlur(it.avatar?.large)
                binding.ivAvatar.loadImageAnimate(it.avatar?.large)
                binding.tvEmail.text = it.email

                binding.toolbarLayout.title = it.username.orEmpty().ifBlank { it.nickname }
            } else {
                debugLog { "未登录！" }
            }
        }
    }

    companion object {
        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }
}