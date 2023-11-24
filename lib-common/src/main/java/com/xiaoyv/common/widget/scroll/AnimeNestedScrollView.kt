package com.xiaoyv.common.widget.scroll

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView
import com.xiaoyv.common.helper.FixHelper

/**
 * Class: [AnimeNestedScrollView]
 *
 * @author why
 * @since 11/23/23
 */
class AnimeNestedScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : NestedScrollView(context, attrs) {

    init {
        FixHelper.fuckMiuiOverScroller(this)
    }
}