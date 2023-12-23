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
    override fun onOffsetChanged(p0: AppBarLayout, p1: Int) {
        // 完全折叠
        if (abs(p1) > p0.totalScrollRange - 40.dpi) {
            toolbar.title = title()
        } else {
            toolbar.title = null
        }
    }
}