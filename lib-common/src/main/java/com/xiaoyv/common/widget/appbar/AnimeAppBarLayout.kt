package com.xiaoyv.common.widget.appbar

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.doOnPreDraw
import com.google.android.material.appbar.AppBarLayout
import com.xiaoyv.common.R
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.widget.kts.getAttrColor
import kotlin.math.abs

/**
 * Class: [AnimeAppBarLayout]
 *
 * @author why
 * @since 12/7/23
 */
class AnimeAppBarLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppBarLayout(context, attrs) {
    private var isToolbarCollapsed = false

    init {
        addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) == appBarLayout.totalScrollRange) {
                // 完全折叠
                if (!isToolbarCollapsed) {
                    isToolbarCollapsed = true
                    configToolbar()
                }
            } else {
                // 没有完全折叠
                if (isToolbarCollapsed) {
                    isToolbarCollapsed = false
                    configToolbar()
                }
            }
        }

        doOnPreDraw {
            configToolbar()
        }
    }

    /**
     * 仅配置嵌套
     */
    private fun configToolbar() {
        if (findViewById<View>(R.id.toolbar_layout) == null) return
        val toolbar = findViewById<AnimeToolbar>(R.id.toolbar) ?: return
        if (isToolbarCollapsed) {
            toolbar.refreshColorTheme(context.getAttrColor(android.R.attr.textColorPrimary))
        } else {
            toolbar.refreshColorTheme(context.getAttrColor(GoogleAttr.colorOnPrimarySurface))
        }
    }
}
