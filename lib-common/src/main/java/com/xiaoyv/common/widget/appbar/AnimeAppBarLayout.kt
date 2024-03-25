package com.xiaoyv.common.widget.appbar

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.graphics.Insets
import androidx.core.util.ObjectsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.blankj.utilcode.util.BarUtils
import com.google.android.material.appbar.AppBarLayout
import com.xiaoyv.widget.kts.showToastCompat

/**
 * Class: [AnimeAppBarLayout]
 *
 * @author why
 * @since 12/7/23
 */
class AnimeAppBarLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : AppBarLayout(context, attrs) {
    private val lastInsetsFiled by lazy {
        AppBarLayout::class.java.getDeclaredField("lastInsets").apply {
            isAccessible = true
        }
    }

    private var lastInsets: WindowInsetsCompat?
        get() = lastInsetsFiled.get(this) as? WindowInsetsCompat
        set(value) = lastInsetsFiled.set(this, value)


    init {
      /*  ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
            val i = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            val compat = WindowInsetsCompat.Builder()
                .setInsets(
                    WindowInsetsCompat.Type.systemBars(),
                    Insets.of(i.left, BarUtils.getStatusBarHeight(), i.right, i.bottom)
                )
                .build()

            var newInsets: WindowInsetsCompat? = null
            if (ViewCompat.getFitsSystemWindows(this)) {
                newInsets = compat
            }

            if (!ObjectsCompat.equals(lastInsets, newInsets)) {
                lastInsets = newInsets
                updateWillNotDraw()
                requestLayout()
            }

            insets
        }*/

//        updatePadding(top = BarUtils.getStatusBarHeight())
    }

    private fun updateWillNotDraw() {
        setWillNotDraw(!shouldDrawStatusBarForeground())
    }

    private fun shouldDrawStatusBarForeground(): Boolean {
        return statusBarForeground != null && getTopInset() > 0
    }

    private fun getTopInset(): Int {
        return lastInsets?.getInsets(WindowInsetsCompat.Type.systemBars())?.top ?: 0
    }
}
