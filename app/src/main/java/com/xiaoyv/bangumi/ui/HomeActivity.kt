package com.xiaoyv.bangumi.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.widget.PopupWindow
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.ActivityHomeBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.common.widget.musume.LAppMusumeView
import com.xiaoyv.widget.dialog.UiDialog
import com.xiaoyv.widget.kts.dpi
import kotlinx.coroutines.delay

/**
 * Class: [HomeActivity]
 *
 * @author why
 * @since 11/24/23
 */
class HomeActivity : BaseViewModelActivity<ActivityHomeBinding, HomeViewModel>() {
    private val vpAdapter by lazy { HomeAdapter(this) }

    private lateinit var floatingView: LAppMusumeView
    private var popupWindow: PopupWindow? = null

    override fun initView() {
        binding.vpView.isUserInputEnabled = false
        binding.vpView.offscreenPageLimit = vpAdapter.itemCount
        binding.vpView.adapter = vpAdapter
    }

    override fun initData() {
        createRobot()

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

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }

    override fun onResume() {
        super.onResume()
        floatingView.onResume()
    }

    override fun onPause() {
        super.onPause()
        floatingView.onPause()
    }

    override fun finish() {
        super.finish()
        popupWindow?.dismiss()
        popupWindow = null
    }

    override fun onDestroy() {
        super.onDestroy()
        floatingView.release()
    }

    /**
     * 创建春菜窗口
     */
    private fun createRobot() {
        floatingView = LAppMusumeView(this).apply {
            init()
        }
        popupWindow = PopupWindow(floatingView, 120.dpi, 200.dpi)
        popupWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        launchUI {
            delay(2000)
            popupWindow?.showAsDropDown(binding.navView, 0, 0, Gravity.END)
        }
    }
}