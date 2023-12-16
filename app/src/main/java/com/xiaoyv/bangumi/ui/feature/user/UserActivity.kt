package com.xiaoyv.bangumi.ui.feature.user

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.tabs.TabLayoutMediator
import com.xiaoyv.bangumi.databinding.ActivityUserBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.ReportType
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.loadImageBlur
import com.xiaoyv.common.kts.randomOffset
import com.xiaoyv.common.kts.randomX
import com.xiaoyv.common.kts.randomY
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.common.widget.dialog.AnimeReportDialog
import com.xiaoyv.widget.dialog.UiDialog


/**
 * Class: [UserActivity]
 *
 * @author why
 * @since 12/3/23
 */
class UserActivity : BaseViewModelActivity<ActivityUserBinding, UserViewModel>() {

    private val vpAdapter by lazy {
        UserAdapter(supportFragmentManager, this.lifecycle)
    }

    private val tabLayoutMediator by lazy {
        TabLayoutMediator(binding.tableLayout, binding.vpContent) { tab, position ->
            tab.text = vpAdapter.tabs[position].title
        }
    }

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.userId = bundle.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun initView() {
        randomX(binding.topLeftTextView, randomOffset)
        randomY(binding.topLeftTextView, randomOffset)

        randomX(binding.middleLeftTextView, randomOffset)
        randomY(binding.middleLeftTextView, randomOffset)

        randomX(binding.bottomLeftTextView, randomOffset)
        randomY(binding.bottomLeftTextView, randomOffset)

        randomX(binding.topRightTextView, randomOffset)
        randomY(binding.topRightTextView, randomOffset)

        randomX(binding.middleRightTextView, randomOffset)
        randomY(binding.middleRightTextView, randomOffset)

        randomX(binding.bottomRightTextView, randomOffset)
        randomY(binding.bottomRightTextView, randomOffset)

        binding.toolbar.initNavBack(this)
    }

    override fun initData() {
        vpAdapter.userId = viewModel.userId

        binding.vpContent.adapter = vpAdapter
        binding.vpContent.offscreenPageLimit = vpAdapter.itemCount

        tabLayoutMediator.attach()
    }

    override fun initListener() {

    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onUserInfoLiveData.observe(this) {
            it ?: return@observe
            debugLog { "UserInfo: " + it.toJson(true) }
            binding.toolbarLayout.title = it.nickname + "@" + it.id
            binding.ivBanner.loadImageBlur(it.avatar)
            binding.ivAvatar.loadImageAnimate(it.avatar)

            binding.topLeftTextView.text = it.createTime
            binding.middleLeftTextView.text = it.userSynchronize.rate.ifBlank { "同步率 -%" }
            binding.bottomLeftTextView.text =
                String.format("最近活跃 %s", it.lastOnlineTime.ifBlank { "暂无" })
            binding.topRightTextView.text = "Ta 的人物"
            binding.middleRightTextView.text = "Ta 的日志"
            binding.bottomRightTextView.text = "Ta 的目录"

            invalidateMenu()
        }

        UserHelper.observeDeleteAction(this) {
            if (it == BgmPathType.TYPE_USER) {
                viewModel.queryUserInfo()
            }
        }
    }

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (viewModel.onUserInfoLiveData.value == null) {
            return super.onCreateOptionsMenu(menu)
        }

        when {
            viewModel.requireIsFriend -> {
                menu.add("私信")
                    .setIcon(CommonDrawable.ic_chat)
                    .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                    .setOnMenuItemClickListener {
                        RouteHelper.jumpSendMessage(viewModel.userId)
                        true
                    }
                menu.add("解除好友")
                    .setOnMenuItemClickListener {
                        showConfirmDialog(
                            message = "是否解除${viewModel.requireUserName}的好友关系？",
                            onConfirmClick = {
                                viewModel.actionFriend(false)
                            }
                        )
                        true
                    }
            }

            else -> {
                menu.add("加为好友")
                    .setIcon(CommonDrawable.ic_add_friend)
                    .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                    .setOnMenuItemClickListener {
                        showConfirmDialog(
                            message = "是否将${viewModel.requireUserName}加为好友关系？",
                            onConfirmClick = {
                                viewModel.actionFriend(true)
                            }
                        )
                        true
                    }
                menu.add("屏蔽TA")
                    .setOnMenuItemClickListener {
                        showConfirmDialog(
                            message = "是否彻底屏蔽${viewModel.requireUserName}？",
                            onConfirmClick = {
                                viewModel.blockUser()
                            }
                        )
                        true
                    }
            }
        }

        menu.add("举报")
            .setOnMenuItemClickListener {
                AnimeReportDialog.show(viewModel.requireUserNumberId, ReportType.TYPE_USER)
                true
            }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}