package com.xiaoyv.common.widget.appbar

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.doOnAttach
import androidx.core.view.size
import com.google.android.material.appbar.MaterialToolbar


/**
 * Class: [AnimeToolbar]
 *
 * @author why
 * @since 12/8/23
 */
class AnimeToolbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MaterialToolbar(context, attrs) {
    private var customColor = 0

    private val customMenuColorHandler = Runnable {
        if (customColor != 0 && menu.size != 0) {
            setMenuIconTint(customColor)
        }
    }

    init {
        doOnAttach {
            removeCallbacks(customMenuColorHandler)
            postDelayed(customMenuColorHandler, 500)
        }
    }

    fun refreshColorTheme(color: Int) {
        customColor = color
        setNavigationIconTint(color)
        setTitleTextColor(color)
        setMenuIconTint(color)
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