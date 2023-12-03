package com.xiaoyv.common.widget.star

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatRatingBar

/**
 * Class: [AnimeRatingBar]
 *
 * @author why
 * @since 12/3/23
 */
class AnimeRatingBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatRatingBar(context, attrs) {

    var onRatingSeekChangeListener: (Float) -> Unit = {}

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val b = super.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                onRatingSeekChangeListener(rating)
            }
        }
        return b
    }
}