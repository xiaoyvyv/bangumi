package com.xiaoyv.common.kts

import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.blankj.utilcode.util.BarUtils
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout

/**
 * 直接拦截消费掉 WindowInsets
 */
fun View.clearApplyWindowInsets() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { _, _ -> WindowInsetsCompat.CONSUMED }
}

/**
 * 强制适配 FitSystemWindow 状态栏
 */
fun AppBarLayout.forceFitStatusBar() {
    clearApplyWindowInsets()
    val lastInsetsFiled = AppBarLayout::class.java.getDeclaredField("lastInsets").apply {
        isAccessible = true
    }
    lastInsetsFiled.set(
        this,
        WindowInsetsCompat.Builder()
            .setInsets(
                WindowInsetsCompat.Type.statusBars(),
                Insets.of(0, BarUtils.getStatusBarHeight(), 0, 0)
            )
            .build()
    )
    requestLayout()
}


/**
 * 强制适配 FitSystemWindow 状态栏
 */
fun CollapsingToolbarLayout.forceFitStatusBar() {
    clearApplyWindowInsets()
    val lastInsetsFiled = CollapsingToolbarLayout::class.java.getDeclaredField("lastInsets").apply {
        isAccessible = true
    }
    lastInsetsFiled.set(
        this,
        WindowInsetsCompat.Builder()
            .setInsets(
                WindowInsetsCompat.Type.statusBars(),
                Insets.of(0, BarUtils.getStatusBarHeight(), 0, 0)
            )
            .build()
    )
    requestLayout()
}