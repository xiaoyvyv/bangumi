package com.xiaoyv.bangumi.ui.feature.user

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.tabs.TabLayoutMediator
import com.xiaoyv.bangumi.databinding.ActivityUserBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.loadImageBlur
import com.xiaoyv.common.kts.randomOffset
import com.xiaoyv.common.kts.randomX
import com.xiaoyv.common.kts.randomY
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.kts.dpi
import kotlin.math.abs


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
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}