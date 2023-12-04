package com.xiaoyv.common.kts

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnEnd
import com.xiaoyv.widget.kts.dpf
import kotlin.random.Random

private val breathDistance by lazy { 5.dpf }

val randomOffset: Float
    get() = (Random.nextFloat() - 0.5f) * breathDistance

/**
 * 设置无限循环的动画
 */
fun randomX(view: View, startX: Float) {
    val offsetX = randomOffset
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
fun randomY(view: View, startY: Float) {
    val offsetY = randomOffset
    val animatorX = ObjectAnimator.ofFloat(view, "translationY", startY, offsetY)
    animatorX.interpolator = AccelerateDecelerateInterpolator()
    animatorX.repeatCount = 0
    animatorX.duration = 2000
    animatorX.doOnEnd { randomY(view, offsetY) }
    animatorX.start()
}

