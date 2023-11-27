package com.xiaoyv.common.widget.emoji.span

import android.view.View

/**
 * ITouchableSpan
 *
 * @author why
 * @since 2012-03-20
 */
interface IUiFaceTouchSpan {
    fun setPressed(pressed: Boolean)

    fun onClick(widget: View)
}