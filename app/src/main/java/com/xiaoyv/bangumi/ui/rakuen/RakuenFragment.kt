@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.ui.rakuen

import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.xiaoyv.bangumi.databinding.FragmentSuperBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.common.config.annotation.LocalCollectionType
import com.xiaoyv.common.config.annotation.SuperType
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.helper.callback.SimpleTabSelectedListener
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.showOptionsDialog
import com.xiaoyv.widget.kts.adjustScrollSensitivity
import com.xiaoyv.widget.kts.orEmpty

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
            if (position == 1) {
                tab.text = buildIconTab(vpAdapter.tabs[position].title)
            } else {
                tab.text = vpAdapter.tabs[position].title
            }
        }
    }

    override fun initView() {
        binding.vp2.adjustScrollSensitivity(ConfigHelper.vpTouchSlop.toFloat())
        binding.vp2.offscreenPageLimit = vpAdapter.itemCount
        binding.vp2.adapter = vpAdapter

        tabLayoutMediator.attach()

        refreshToolbarMenu()
    }

    override fun initData() {

    }

    override fun initListener() {
        binding.tabLayout.addOnTabSelectedListener(object : SimpleTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
                p0 ?: return

                // 点击小组的 TAB
                if (p0.position == 1) {
                    changeGroupType()
                }
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                viewModel.defaultTab.value = p0?.position ?: return
            }
        })
    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.defaultTab.observe(this) {
            if (binding.vp2.currentItem != it) {
                binding.vp2.setCurrentItem(it.orEmpty(), true)
            }
        }

        viewModel.rakuenGroupType.observe(this) {
            when (it) {
                // 小组
                SuperType.TYPE_GROUP -> {
                    binding.tabLayout.getTabAt(1)?.setText(buildIconTab("小组"))
                }
                // 加入的小组
                SuperType.TYPE_MY_GROUP -> {
                    binding.tabLayout.getTabAt(1)?.setText(buildIconTab("加入的小组"))
                }
            }
        }
    }

    /**
     * 更改小组类型 全部小组|我加入的小组
     */
    private fun changeGroupType() {
        requireActivity().showOptionsDialog(
            title = "切换小组类型",
            items = listOf("全部小组", "我加入的小组"),
            onItemClick = { _, index ->
                when (index) {
                    0 -> viewModel.rakuenGroupType.value = SuperType.TYPE_GROUP
                    1 -> viewModel.rakuenGroupType.value = SuperType.TYPE_MY_GROUP
                }
            }
        )
    }

    private fun buildIconTab(title: String): CharSequence {
        return "$title ↓"
    }

    private fun refreshToolbarMenu() {
        binding.toolbar.menu.apply {
            add(getString(CommonString.common_search))
                .setIcon(CommonDrawable.ic_search)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                .setOnMenuItemClickListener {
                    RouteHelper.jumpSearch()
                    true
                }

            add("我发表的话题")
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
                .setOnMenuItemClickListener {
                    if (UserHelper.isLogin.not()) {
                        RouteHelper.jumpSignIn()
                        return@setOnMenuItemClickListener true
                    }
                    RouteHelper.jumpMyTopics(true)
                    true
                }

            add("我回复的话题")
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
                .setOnMenuItemClickListener {
                    if (UserHelper.isLogin.not()) {
                        RouteHelper.jumpSignIn()
                        return@setOnMenuItemClickListener true
                    }
                    RouteHelper.jumpMyTopics(false)
                    true
                }

            add("我收藏的话题")
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
                .setOnMenuItemClickListener {
                    if (UserHelper.isLogin.not()) {
                        RouteHelper.jumpSignIn()
                        return@setOnMenuItemClickListener true
                    }
                    RouteHelper.jumpCollection(LocalCollectionType.TYPE_TOPIC)
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