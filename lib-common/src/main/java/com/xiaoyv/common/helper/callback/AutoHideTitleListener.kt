package com.xiaoyv.common.helper.callback

import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.xiaoyv.widget.kts.dpi
import kotlin.math.abs

/**
 * Class: [AutoHideTitleListener]
 *
 * @author why
 * @since 12/23/23
 */
class AutoHideTitleListener(private val toolbar: Toolbar, private val title: () -> String) :
    OnOffsetChangedListener {
    private val offset by lazy { 40.dpi }

    override fun onOffsetChanged(p0: AppBarLayout, p1: Int) {
        val targetTitle = title()

        // 完全折叠
        if (abs(p1) > p0.totalScrollRange - offset) {
            if (toolbar.title != targetTitle) toolbar.title = targetTitle
        } else {
            if (toolbar.title != null) toolbar.title = null
        }
    }
}