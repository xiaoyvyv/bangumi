package com.xiaoyv.common.widget.text

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.textview.MaterialTextView
import com.xiaoyv.common.R

/**
 * Class: [AnimeTextView]
 *
 * @author why
 * @since 11/24/23
 */
class AnimeTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MaterialTextView(context, attrs) {

    init {
//        runCatching { setTypeface(ResourcesCompat.getFont(context, R.font.font)) }
    }
}