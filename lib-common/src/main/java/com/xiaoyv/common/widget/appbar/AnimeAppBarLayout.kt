package com.xiaoyv.common.widget.appbar

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.doOnPreDraw
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
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
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar) ?: return
        if (isToolbarCollapsed) {
            toolbar.setNavigationIconTint(context.getAttrColor(android.R.attr.textColorPrimary))
            toolbar.setTitleTextColor(context.getAttrColor(android.R.attr.textColorPrimary))
            toolbar.setMenuIconTint(context.getAttrColor(android.R.attr.textColorPrimary))
        } else {
            toolbar.setNavigationIconTint(context.getAttrColor(GoogleAttr.colorOnPrimarySurface))
            toolbar.setTitleTextColor(context.getAttrColor(GoogleAttr.colorOnPrimarySurface))
            toolbar.setMenuIconTint(context.getAttrColor(GoogleAttr.colorOnPrimarySurface))
        }
    }

    /**
     * 菜单项图标
     */
    private fun Toolbar.setMenuIconTint(tintColor: Int) {
        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)
            val icon = menuItem.icon ?: continue
            val tintedIcon = DrawableCompat.wrap(icon)
            DrawableCompat.setTint(tintedIcon, tintColor)
            menuItem.icon = tintedIcon
        }
    }
}
