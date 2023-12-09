package com.xiaoyv.bangumi.ui

import android.view.MotionEvent
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.badge.BadgeDrawable
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.ActivityHomeBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.common.currentApplication
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.dialog.UiDialog


/**
 * Class: [HomeActivity]
 *
 * @author why
 * @since 11/24/23
 */
class HomeActivity : BaseViewModelActivity<ActivityHomeBinding, HomeViewModel>() {
    private val vpAdapter by lazy { HomeAdapter(this) }

    private val robot by lazy { HomeRobot(this) }

    override fun initView() {
        binding.vpView.isUserInputEnabled = false
        binding.vpView.offscreenPageLimit = vpAdapter.itemCount
        binding.vpView.adapter = vpAdapter
    }

    override fun initData() {
        robot.disable()
    }

    override fun initListener() {
        binding.navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_menu_home -> binding.vpView.setCurrentItem(0, false)
                R.id.bottom_menu_timeline -> binding.vpView.setCurrentItem(1, false)
                R.id.bottom_menu_media -> binding.vpView.setCurrentItem(2, false)
                R.id.bottom_menu_discover -> binding.vpView.setCurrentItem(3, false)
                R.id.bottom_menu_profile -> binding.vpView.setCurrentItem(4, false)
            }
            true
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        currentApplication.globalRobotSpeech.observe(this) {
            debugLog { "春菜：$it" }
            robot.onSay(it)
        }

        currentApplication.globalNotify.observe(this) {
            val badge = binding.navView.getOrCreateBadge(R.id.bottom_menu_profile)
            if (it != 0) {
                badge.number = it
                badge.badgeGravity = BadgeDrawable.TOP_END
            } else {
                binding.navView.removeBadge(R.id.bottom_menu_profile)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        robot.onResume()
    }

    override fun onPause() {
        super.onPause()
        robot.onPause()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        robot.onAttachedToWindow(binding.navView)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        robot.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }
}