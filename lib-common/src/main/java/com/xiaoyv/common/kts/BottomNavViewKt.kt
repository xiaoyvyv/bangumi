package com.xiaoyv.common.kts

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.xiaoyv.widget.kts.fetchActivity


/**
 * 主页自定义的 底栏兼容
 */
fun BottomNavigationView.customApplyWindowInsets() {
    val view = context.fetchActivity?.window?.decorView ?: return

    // 剔除 BottomNavigationView 构造器配置的 ApplyWindowInsetsListener
    setOnApplyWindowInsetsListener(null)

    // 根据根布局自定义 WindowInsets 的内边距（补充适配输入法的高度）
    ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
        val insetsNavBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
        val bottomCompat = insetsNavBar.bottom.coerceAtLeast(0)
        updatePadding(bottom = bottomCompat)
        insets
    }
}