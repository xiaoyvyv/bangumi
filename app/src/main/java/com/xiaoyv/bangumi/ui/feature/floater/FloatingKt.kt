@file:Suppress("DEPRECATION")

package com.xiaoyv.bangumi.ui.feature.floater

import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.WindowManager

/**
 * FloatingKt
 *
 * @author why
 * @since 2023/4/17
 */
fun compatFloatParam(
    canTouch: Boolean = false,
    noLimit: Boolean = false,
    alpha: Float = 1f
): WindowManager.LayoutParams {
    val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    } else {
        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
    }
    var flag = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
            (if (noLimit) WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS else WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN) or
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED or
            WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
    if (canTouch.not()) {
        flag = flag or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    }

    val params = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        type, flag,
        PixelFormat.TRANSLUCENT
    )
    params.alpha = alpha
    params.format = PixelFormat.RGBA_8888
    params.gravity = Gravity.TOP or Gravity.START
    return params
}