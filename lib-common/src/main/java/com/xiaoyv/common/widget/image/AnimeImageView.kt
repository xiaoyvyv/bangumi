package com.xiaoyv.common.widget.image

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.imageview.ShapeableImageView
import com.xiaoyv.widget.kts.getColor

/**
 * Class: [AnimeImageView]
 *
 * @author why
 * @since 11/24/23
 */
class AnimeImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ShapeableImageView(context, attrs) {

    init {
        setBackgroundColor(getColor(com.xiaoyv.widget.R.color.ui_text_c4))
    }
}