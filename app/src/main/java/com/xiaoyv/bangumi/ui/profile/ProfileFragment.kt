package com.xiaoyv.bangumi.ui.profile

import android.annotation.SuppressLint
import android.view.MenuItem
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.xiaoyv.bangumi.databinding.FragmentProfileBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.common.config.annotation.LocalCollectionType
import com.xiaoyv.common.currentApplication
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.CommonId
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.loadImageBlur
import com.xiaoyv.common.kts.setBadgeNumber
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.kts.adjustScrollSensitivity
import com.xiaoyv.widget.kts.dpi
import kotlin.math.abs


/**
 * Class: [ProfileFragment]
 *
 * @author why
 * @since 11/24/23
 */
class ProfileFragment : BaseViewModelFragment<FragmentProfileBinding, ProfileViewModel>() {
    private var notifyBadge: BadgeDrawable? = null
    private var messageBadge: BadgeDrawable? = null

    private val vpAdapter by lazy {
        ProfileAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
    }

    private val tabLayoutMediator by lazy {
        TabLayoutMediator(binding.tableLayout, binding.vpContent) { tab, position ->
            tab.text = vpAdapter.tabs[position].title
        }
    }

    override fun initView() {
        binding.vpContent.adjustScrollSensitivity(ConfigHelper.vpTouchSlop.toFloat())
        binding.vpContent.offscreenPageLimit = vpAdapter.itemCount
        binding.vpContent.adapter = vpAdapter

        tabLayoutMediator.attach()

        binding.toolbar.setNavigationIcon(CommonDrawable.ic_menu)
    }

    override fun initData() {

    }

    override fun initListener() {
        binding.toolbar.setNavigationOnClickListener {
            PopupMenu(requireActivity(), it)
                .apply {
                    menu.add("我的空间")
                        .setOnMenuItemClickListener {
                            if (UserHelper.isLogin) {
                                RouteHelper.jumpUserDetail(UserHelper.currentUser.id)
                            } else {
                                RouteHelper.jumpSignIn()
                            }
                            true
                        }

                    menu.add("下载中心")
                        .setOnMenuItemClickListener {
                            RouteHelper.jumpThunder()
                            true
                        }

                    menu.add("我收藏的话题")
                        .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
                        .setOnMenuItemClickListener {
                            if (UserHelper.isLogin) {
                                RouteHelper.jumpCollection(LocalCollectionType.TYPE_TOPIC)
                            } else {
                                RouteHelper.jumpSignIn()
                            }
                            true
                        }

                    menu.add("我收藏的日志")
                        .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
                        .setOnMenuItemClickListener {
                            if (UserHelper.isLogin) {
                                RouteHelper.jumpCollection(LocalCollectionType.TYPE_BLOG)
                            } else {
                                RouteHelper.jumpSignIn()
                            }
                            true
                        }
                }
                .show()
        }

        binding.toolbar.menu.apply {
            add(0, CommonId.profile_notify, 0, "Notify")
                .setIcon(CommonDrawable.ic_notifications)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                .setOnMenuItemClickListener {
                    RouteHelper.jumpNotify()
                    true
                }

            add(0, CommonId.profile_message, 0, "Message")
                .setIcon(CommonDrawable.ic_email_normal)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                .setOnMenuItemClickListener {
                    RouteHelper.jumpMessage()
                    true
                }

            add("Setting")
                .setIcon(CommonDrawable.ic_setting)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                .setOnMenuItemClickListener {
                    RouteHelper.jumpSetting()
                    true
                }
        }

        binding.appBar.addOnOffsetChangedListener { appBarLayout, i ->
            if (abs(i) >= appBarLayout.totalScrollRange - 50.dpi) {
                binding.toolbar.navigationIcon = null
            } else {
                binding.toolbar.setNavigationIcon(CommonDrawable.ic_menu)
            }
        }

        binding.ivBanner.setOnFastLimitClickListener {
            if (UserHelper.isLogin) {
                RouteHelper.jumpConfigBg()
            }
        }

        binding.tableLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(p0: TabLayout.Tab?) {
                binding.appBar.setExpanded(false, true)
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {
                binding.appBar.setExpanded(false, true)
            }
        })

        binding.ivAvatar.setOnFastLimitClickListener {
            editProfileOrLogin()
        }

        binding.tvEmail.setOnFastLimitClickListener {
            editProfileOrLogin()
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun LifecycleOwner.initViewObserver() {
        UserHelper.observeUserInfo(this) {
            if (!it.isEmpty) {
                loadUserBg()
                binding.ivAvatar.loadImageAnimate(it.avatar)
                binding.tvEmail.text = UserHelper.cacheEmail

                binding.toolbarLayout.title = it.username.ifBlank { it.nickname }
            } else {
                debugLog { "未登录！" }
                binding.toolbarLayout.title = "访客身份"
                binding.tvEmail.text = "点击去登录"
                binding.ivAvatar.setImageResource(0)
                binding.ivBanner.setImageResource(0)
            }
        }

        // 消息提醒
        currentApplication.globalNotify.observe(this) {
            // 消息提醒
            notifyBadge = binding.toolbar.setBadgeNumber(
                CommonId.profile_notify, it.first, notifyBadge
            )

            // 短信提醒
            messageBadge = binding.toolbar.setBadgeNumber(
                CommonId.profile_message, it.second, messageBadge
            )
        }
    }

    /**
     * 加载背景
     */
    private fun loadUserBg() {
        if (UserHelper.currentUser.roomPic.isBlank().not()) {
            binding.ivBanner.loadImageAnimate(UserHelper.currentUser.roomPic)
        } else {
            binding.ivBanner.loadImageBlur(UserHelper.currentUser.avatar)
        }
    }

    private fun editProfileOrLogin() {
        if (UserHelper.isLogin) {
            RouteHelper.jumpEditProfile()
        } else {
            RouteHelper.jumpSignIn()
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserBg()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabLayoutMediator.detach()
    }

    companion object {
        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }
}