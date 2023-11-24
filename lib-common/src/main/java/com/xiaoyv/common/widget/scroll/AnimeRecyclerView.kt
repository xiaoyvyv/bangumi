package com.xiaoyv.common.widget.scroll

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.xiaoyv.common.helper.FixHelper

/**
 * Class: [AnimeRecyclerView]
 *
 * @author why
 * @since 11/23/23
 */
class AnimeRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {

    init {
        FixHelper.fuckMiuiOverScroller(this)
    }
}