package com.xiaoyv.bangumi.ui.feature.user

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.tabs.TabLayoutMediator
import com.xiaoyv.bangumi.databinding.ActivityUserBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.loadImageBlur
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.kts.dpf
import com.xiaoyv.widget.kts.dpi
import kotlin.math.abs
import kotlin.random.Random


/**
 * Class: [UserActivity]
 *
 * @author why
 * @since 12/3/23
 */
class UserActivity : BaseViewModelActivity<ActivityUserBinding, UserViewModel>() {
    private val breathDistance by lazy { 5.dpf }

    private val vpAdapter by lazy {
        UserAdapter(supportFragmentManager, this.lifecycle)
    }

    private val tabLayoutMediator by lazy {
        TabLayoutMediator(binding.tableLayout, binding.vpContent) { tab, position ->
            tab.text = vpAdapter.tabs[position].title
        }
    }

    internal val appBarLayout
        get() = binding.appBar

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.userId = bundle.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun initView() {
        randomX(binding.topLeftTextView, getRandomOffsetX())
        randomY(binding.topLeftTextView, getRandomOffsetY())

        randomX(binding.middleLeftTextView, getRandomOffsetX())
        randomY(binding.middleLeftTextView, getRandomOffsetY())

        randomX(binding.bottomLeftTextView, getRandomOffsetX())
        randomY(binding.bottomLeftTextView, getRandomOffsetY())

        randomX(binding.topRightTextView, getRandomOffsetX())
        randomY(binding.topRightTextView, getRandomOffsetY())

        randomX(binding.middleRightTextView, getRandomOffsetX())
        randomY(binding.middleRightTextView, getRandomOffsetY())

        randomX(binding.bottomRightTextView, getRandomOffsetX())
        randomY(binding.bottomRightTextView, getRandomOffsetY())

        binding.vpContent.adapter = vpAdapter
        binding.vpContent.offscreenPageLimit = 5

        tabLayoutMediator.attach()
    }

    override fun initData() {

    }

    override fun initListener() {
        binding.appBar.addOnOffsetChangedListener { _, i ->
            binding.fabTop.isVisible =
                abs(i) < 100.dpi && binding.vpContent.currentItem == vpAdapter.itemCount - 1
        }

        binding.fabTop.setOnFastLimitClickListener {
            binding.appBar.setExpanded(false, true)
        }
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

    /**
     * 设置无限循环的动画
     */
    private fun randomX(view: View, startX: Float) {
        val offsetX = getRandomOffsetX()
        val animatorX = ObjectAnimator.ofFloat(startX, offsetX)
        animatorX.interpolator = AccelerateDecelerateInterpolator()
        animatorX.repeatCount = 0
        animatorX.duration = 2000
        animatorX.addUpdateListener {
            view.translationX = it.animatedValue as Float
        }
        animatorX.doOnEnd { randomX(view, offsetX) }
        animatorX.start()
    }

    /**
     * 设置无限循环的动画
     */
    private fun randomY(view: View, startY: Float) {
        val offsetY = getRandomOffsetX()
        val animatorX = ObjectAnimator.ofFloat(view, "translationY", startY, offsetY)
        animatorX.interpolator = AccelerateDecelerateInterpolator()
        animatorX.repeatCount = 0
        animatorX.duration = 2000
        animatorX.doOnEnd { randomY(view, offsetY) }
        animatorX.start()
    }

    private fun getRandomOffsetX(): Float {
        return (Random.nextFloat() - 0.5f) * breathDistance
    }

    private fun getRandomOffsetY(): Float {
        return (Random.nextFloat() - 0.5f) * breathDistance
    }
}