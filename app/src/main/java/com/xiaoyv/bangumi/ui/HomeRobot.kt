package com.xiaoyv.bangumi.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.PopupWindow
import androidx.core.view.doOnPreDraw
import com.blankj.utilcode.util.ScreenUtils
import com.xiaoyv.bangumi.databinding.ActivityHomeRobotBinding
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.widget.musume.LAppDelegate
import com.xiaoyv.widget.kts.dpi
import com.xiaoyv.widget.kts.useNotNull

/**
 * Class: [HomeRobot]
 *
 * @author why
 * @since 11/26/23
 */
class HomeRobot(private val homeActivity: HomeActivity) {
    private var binding: ActivityHomeRobotBinding? = null
    private var popupWindow: PopupWindow? = null
    private var attachedToWindow: Boolean = false
    private var anchorView: View? = null

    private var showX = 0
    private var showY = 0

    private var targetWidth = 120.dpi

    fun onResume() {
        binding = ActivityHomeRobotBinding.inflate(homeActivity.layoutInflater)
        binding?.robotView?.init()
        binding?.robotView?.onResume()

        val targetWidth = ScreenUtils.getScreenWidth() / 2
        val targetHeight = ScreenUtils.getScreenWidth() / 2

        popupWindow = PopupWindow(requireNotNull(binding).root, targetWidth, targetHeight)
        popupWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow?.isFocusable = false
        popupWindow?.isTouchable = false
        popupWindow?.contentView?.bringToFront();

        if (attachedToWindow && anchorView != null) {
            show()
        }
    }

    fun onAttachedToWindow(targetView: View) {
        attachedToWindow = true
        anchorView = targetView
        show()
    }


    fun onPause() {
        binding?.robotView?.onPause()

        popupWindow?.dismiss()
        popupWindow = null

        binding = null
    }

    fun onTouchEvent(event: MotionEvent) {
        if (popupWindow == null || binding == null) {
            return
        }

        val pointX = event.x
        val pointY = event.y

        // 变换位置
        if (pointX >= showX && pointY >= showY && pointY <= showY + 200.dpi) {
            val transformX = pointX - showX
            val transformY = pointY - showY

            debugLog { "x:$transformY, y:$transformX" }

            when (event.action) {
                MotionEvent.ACTION_DOWN -> LAppDelegate.getInstance()
                    .onTouchBegan(transformX, transformY)

                MotionEvent.ACTION_UP -> LAppDelegate.getInstance()
                    .onTouchEnd(transformX, transformY)

                MotionEvent.ACTION_MOVE -> LAppDelegate.getInstance()
                    .onTouchMoved(transformX, transformY)
            }
        }
    }

    fun onSay(text: String) {
        if (popupWindow == null || binding == null) {
            return
        }
        binding?.tvSpeech?.text = text
    }

    private fun show() {
        val locationOnScreen = intArrayOf(0, 0)

        useNotNull(binding?.robotView) {
            doOnPreDraw {
                getLocationOnScreen(locationOnScreen)

                showX = locationOnScreen[0]
                showY = locationOnScreen[1]

                debugLog { "show($showX,$showY)" }
            }
        }

        popupWindow?.showAsDropDown(anchorView, 0, 0, Gravity.END)
    }
}