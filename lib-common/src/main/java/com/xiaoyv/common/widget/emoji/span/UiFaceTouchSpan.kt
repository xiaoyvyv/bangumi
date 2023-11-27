package com.xiaoyv.common.widget.emoji.span

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.view.ViewCompat

/**
 * 可 Touch 的 Span，在 [.setPressed] 后根据是否 pressed 来触发不同的UI状态
 * 提供设置 span 的文字颜色和背景颜色的功能, 在构造时传入
 *
 * @author why
 */
abstract class UiFaceTouchSpan(
        @field:ColorInt @param:ColorInt var normalTextColor: Int,
        @field:ColorInt @param:ColorInt var pressedTextColor: Int,
        @field:ColorInt @param:ColorInt val normalBackgroundColor: Int,
        @field:ColorInt @param:ColorInt val pressedBackgroundColor: Int
) : ClickableSpan(), IUiFaceTouchSpan {
    private var mIsPressed = false

    var isNeedUnderline = false

    override fun setPressed(pressed: Boolean) {
        mIsPressed = pressed
    }

    override fun onClick(widget: View) {
        if (ViewCompat.isAttachedToWindow(widget)) {
            onSpanClick(widget)
        }
    }

    /**
     * onSpanClick
     *
     * @param widget widget
     */
    abstract fun onSpanClick(widget: View)

    fun isPressed(): Boolean {
        return mIsPressed
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.color = if (mIsPressed) pressedTextColor else normalTextColor
        ds.bgColor = if (mIsPressed) pressedBackgroundColor else normalBackgroundColor
        ds.isUnderlineText = isNeedUnderline
    }
}