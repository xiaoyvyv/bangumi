package com.xiaoyv.common.widget.text

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText

/**
 * Class: [AnimeEditTextView]
 *
 * @author why
 * @since 11/25/23
 */
class AnimeEditTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextInputEditText(context, attrs) {
    init {
//        runCatching { setTypeface(ResourcesCompat.getFont(context, R.font.font)) }
    }
}