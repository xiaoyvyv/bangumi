package com.xiaoyv.common.helper.callback

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

/**
 * Class: [InterceptTouchListener]
 *
 * @author why
 * @since 12/19/23
 */
class InterceptTouchListener : View.OnTouchListener {

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                (v.parent as? ViewGroup)?.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                (v.parent as? ViewGroup)?.requestDisallowInterceptTouchEvent(false)
            }
        }
        return false
    }
}